/**
 * 
 */
package org.identityconnectors.oracle;

import java.sql.Connection;
import java.util.Collections;

import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.dbcommon.SQLUtil;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;
import org.junit.*;

/**
 * @author kitko
 *
 */
public class OracleOperationUpdateTest extends OracleConnectorAbstractTest{
    private static final String TEST_USER = "testUser";
    private static Uid uid;
    
    /**
     * Creates test user to be used in all test methods
     */
    @BeforeClass
    public static void createTestUser(){
        uid = new Uid(TEST_USER);
        try{
            connector.delete(ObjectClass.ACCOUNT, uid, null);
        }
        catch(UnknownUidException e){}
        Attribute authentication = AttributeBuilder.build(OracleConnector.ORACLE_AUTHENTICATION_ATTR_NAME, OracleConnector.ORACLE_AUTH_LOCAL);
        Attribute name = new Name(TEST_USER);
        Attribute passwordAttribute = AttributeBuilder.buildPassword(new GuardedString("hello".toCharArray()));
        Attribute privileges = AttributeBuilder.build(OracleConnector.ORACLE_PRIVS_ATTR_NAME, "create session");
        uid = connector.create(ObjectClass.ACCOUNT, CollectionUtil.newSet(authentication,name,passwordAttribute,privileges), null);
    }
    
    /**
     * Deletes test user
     */
    @AfterClass
    public static void deleteTestUser(){
        connector.delete(ObjectClass.ACCOUNT, uid, null);
    }
    
    /**
     * Test method for {@link org.identityconnectors.oracle.OracleOperationUpdate#update(org.identityconnectors.framework.common.objects.ObjectClass, org.identityconnectors.framework.common.objects.Uid, java.util.Set, org.identityconnectors.framework.common.objects.OperationOptions)}.
     */
    @Test
    public final void testUpdatePassword() {
        Attribute passwordAttribute = AttributeBuilder.buildPassword(new GuardedString("newPassword".toCharArray()));
        connector.update(ObjectClass.ACCOUNT, uid, Collections.singleton(passwordAttribute),null);
        //now try to create connection
        final Connection conn = testConf.createConnection(TEST_USER, (GuardedString) AttributeUtil.getSingleValue(passwordAttribute));
        SQLUtil.closeQuietly(conn);
        
    }
    
    

}
