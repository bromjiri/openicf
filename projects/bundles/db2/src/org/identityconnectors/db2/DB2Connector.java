package org.identityconnectors.db2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.common.security.GuardedString.Accessor;
import org.identityconnectors.dbcommon.SQLUtil;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.InvalidCredentialException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationalAttributeInfos;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.AuthenticateOp;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.SearchOp;

@ConnectorClass(
        displayNameKey = "DatabaseTable",
        configurationClass = DB2Configuration.class)
public class DB2Connector implements AuthenticateOp,SchemaOp,CreateOp,SearchOp<String>, PoolableConnector {
	
	private final static Log log = Log.getLog(DB2Connector.class);
	private Connection adminConn;
	private DB2Configuration cfg;
    // DB2 limitation on account id size
    private static final int maxNameSize = 30;
    private static final String USER_AUTH_GRANTS = "grants";


	public Uid authenticate(String username, GuardedString password,OperationOptions options) {
		log.info("authenticate user: {0}", username);
		//just try to create connection with passed credentials
		Connection conn = null;
		try{
			conn = createConnection(username, password);
		}
		catch(RuntimeException e){
			if(e.getCause() instanceof SQLException){
				SQLException sqlE = (SQLException) e.getCause();
				if("28000".equals(sqlE.getSQLState()) && -4214 ==sqlE.getErrorCode()){
					//Wrong user or password, log it here and rethrow
					log.info(e,"Invalid user/passord for user: {0}",username);
					throw new InvalidCredentialException("invalid user/password",e.getCause());
				}
			}
			throw e;
		}
		finally{
			SQLUtil.closeQuietly(conn);
		}
		log.info("User {0} authenticated",username);
		return null;
	}
	
	public Schema schema() {
        //The Name is supported attribute
        Set<AttributeInfo> attrInfoSet = new HashSet<AttributeInfo>();
        attrInfoSet.add(AttributeInfoBuilder.build(Name.NAME,true,true,true,true));
        //Password is operationalAttribute 
        attrInfoSet.add(OperationalAttributeInfos.PASSWORD);

        // Use SchemaBuilder to build the schema. Currently, only ACCOUNT type is supported.
        SchemaBuilder schemaBld = new SchemaBuilder(getClass());
        schemaBld.defineObjectClass(ObjectClass.ACCOUNT_NAME, attrInfoSet);
        return schemaBld.build();
    } 

	public void checkAlive() {
		DB2Specifics.testConnection(adminConn);
	}

	public void dispose() {
		SQLUtil.closeQuietly(adminConn);
	}

	public Configuration getConfiguration() {
		return cfg;
	}

	public void init(Configuration cfg) {
		this.cfg = (DB2Configuration) cfg;
		this.adminConn = createAdminConnection();
	}
	
	private Connection createAdminConnection(){
		return createConnection(cfg.getAdminAccount(),cfg.getAdminPassword());
	}
	
	private Connection createConnection(String user,GuardedString password){
		String driver = cfg.getJdbcDriver();
		String host = cfg.getHost();
		String port = cfg.getPort();
		String subProtocol = cfg.getJdbcSubProtocol();
		String databaseName = cfg.getDatabaseName();
		return DB2Specifics.createDB2Connection(driver, host, port, subProtocol, databaseName, user, password);
	}


	public FilterTranslator<String> createFilterTranslator(ObjectClass oclass,
			OperationOptions options) {
		return new MyFilterTranslator();
	}
	
	private static class MyFilterTranslator implements FilterTranslator<String>{
		public List<String> translate(Filter filter) {
			return Arrays.asList("xx");
		}
		
	}

	public void executeQuery(ObjectClass oclass, String query,ResultsHandler handler, OperationOptions options) {
		Set<Attribute> attributeSet = new HashSet<Attribute>();
		attributeSet.add(new Name("xx"));
		attributeSet.add(new Uid("xx"));
		handler.handle(new ConnectorObject(ObjectClass.ACCOUNT,attributeSet));
	}
	
	public Uid create(ObjectClass oclass, Set<Attribute> attrs,OperationOptions options) {
        if ( oclass == null || !oclass.equals(ObjectClass.ACCOUNT)) {
            throw new IllegalArgumentException(
                    "Create operation requires an 'ObjectClass' attribute of type 'Account'.");
        }
        Name user = AttributeUtil.getNameFromAttributes(attrs);
        if (user == null || StringUtil.isBlank(user.getNameValue())) {
            throw new IllegalArgumentException("The Name attribute cannot be null or empty.");
        }
        // Password is Operational
        GuardedString password = AttributeUtil.getPasswordValue(attrs);
        if (password == null) {
            throw new IllegalArgumentException("The Password attribute cannot be null.");
        }
        updateAuthority(user.getNameValue(),password, attrs);
		
		return new Uid("xx");
	}
	
	
	/**
     *  Applies resources grants and revokes to the passed user.  Updates
     *  occur in a transaction.  Assumes connection is already open.
	 * @param password 
     */
    private void updateAuthority(String user, GuardedString password, Set<Attribute> attrs)   {
        checkDB2Validity(user,password);
        checkAdminConnection();

        Collection<String> grants = null;
        Attribute wsAttr = AttributeUtil.find(USER_AUTH_GRANTS, attrs);
        if (wsAttr != null) {
            String delimitedGrants = AttributeUtil.getStringValue(wsAttr);
            if (delimitedGrants != null) {
                // yuck, this utility method should be somewhere central...
                grants = DB2Specifics.divideString(delimitedGrants, ',', true);
            }
            else if (cfg.isRemoveAllGrants()) {
                    throw new IllegalStateException("When DB2 RA 'removeForeignGrants' = 1, " +
                                                "at least 1 grant is required.");
            }
        }
        else if (cfg.isRemoveAllGrants()) {
                throw new IllegalStateException("When DB2 RA 'removeForeignGrants' = 1, " +
                                            "at least 1 grant is required.");
        }
        try {
        	adminConn.setAutoCommit(false);
            if (grants != null) {
                if (cfg.isRemoveAllGrants()) {
                    revokeAllGrants(user);
                }
                executeGrants("", grants, "", user);
            }
            adminConn.commit();
            adminConn.setAutoCommit(true);
        }
        catch (Exception e) {
        	SQLUtil.rollbackQuietly(adminConn);
        	throw ConnectorException.wrap(e);
        }
    }
    
    
    private void checkAdminConnection() {
		if(adminConn == null){
			throw new IllegalStateException("No admin connection present");
		}
	}

	/**
     *  Checks a given account id and password to make sure they follow DB2
     *  rules for validity.  The rules are given in the DB2 SQL Reference
     *  Manual.  They include length limits, forbidden prefixes, and forbidden
     *  keywords.  Throws and exception if the name or password are invalid.
     */
    private void checkDB2Validity(String accountID, GuardedString password)  {
        if (accountID.length() > maxNameSize) {
        	throw new IllegalArgumentException("Name to short");
        }
        if (DB2Specifics.containsIllegalDB2Chars(accountID.toCharArray())) {
        	throw new IllegalArgumentException("Name contains illegal characters");
        }
        if (!DB2Specifics.isValidName(accountID.toUpperCase())) {
            throw new IllegalArgumentException("Name is reserved keyword or its substring");
        }

        if (password != null) {
        	password.access(new Accessor(){
				public void access(char[] clearChars) {
			        if (DB2Specifics.containsIllegalDB2Chars(clearChars)) {
			        	throw new IllegalArgumentException("Password contains illegal characters");
			        }
			        if (!DB2Specifics.isValidName(new String(clearChars).toUpperCase())) {
			            throw new IllegalArgumentException("Password is reserved keyword or its substring");
			        }
				}
        	});
        }
    }
    
    /**
     *  Removes all grants for a user on the resource.  Effectively
     *  deletes them from the resource.
     */
    private void revokeAllGrants(String user) throws Exception {
        checkDB2Validity(user,null);
        Collection<DB2Authority> allAuthorities = new DB2AuthorityReader(adminConn).readAllAuthorities(user);
        revokeGrants(allAuthorities);
    }

    
    
    /**
     *  For a given grant type and user, revokes the passed collection
     *  of grant objects from the resource.
     */
    private void revokeGrants(Collection<DB2Authority> db2AuthoritiesToRevoke)   throws  SQLException {
        for(DB2Authority auth : db2AuthoritiesToRevoke){
            DB2AuthorityTable authTable = (DB2AuthorityTable)DB2Specifics.authType2DB2AuthorityTable(auth.authorityType);
            String revokeSQL = authTable.generateRevokeSQL(auth);
            executeSQL(revokeSQL);
        }
    }
    
    
    private void executeSQL(String sql) throws SQLException {
        checkAdminConnection();
        Statement statement = null;
        try {
            statement = adminConn.createStatement();
            statement.execute(sql);
        }
        finally {
            SQLUtil.closeQuietly(statement);
        }
    }
    
    
    /**
     *  Executes a set of sql GRANT statements built using an sql
     *  prefix, a collection of grant objects, a postfix, and a user.
     *  Throws if anything goes wrong.
     */
    private void executeGrants(String prefix, Collection<String> grants,String postfix, String user)  throws SQLException{
        for(String grant : grants){
            String sql = "GRANT " + prefix + " " + grant
			             + " " + postfix + " TO USER "
			             + user.toUpperCase() + ";";
            executeSQL(sql);
        }
    }
    
    
    
    
  
}
