/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/cddl1.php
 * See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://opensource.org/licenses/cddl1.php.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.identityconnectors.oracleerp;

import static org.identityconnectors.oracleerp.OracleERPUtil.MSG_PASSWORD_BLANK;
import static org.identityconnectors.oracleerp.OracleERPUtil.MSG_USER_BLANK;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import oracle.jdbc.OracleConnection;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.dbcommon.DatabaseConnection;
import org.identityconnectors.dbcommon.DatabaseQueryBuilder;
import org.identityconnectors.dbcommon.JNDIUtil;
import org.identityconnectors.dbcommon.SQLParam;
import org.identityconnectors.dbcommon.SQLUtil;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.ConnectorMessages;
import org.identityconnectors.framework.spi.Configuration;

/**
 * Class to represent a OracleErp Connection.
 *
 * @author petr
 * @version 1.0
 * @since 1.0
 */
final class OracleERPConnection extends DatabaseConnection {
    /**
     * The default row prefetch used in Oracle statements.
     */
    private static final int DEFAULT_ROW_PREFETCH = 10;

    static final Log LOG = Log.getLog(OracleERPConnector.class);

    /**
     * Create connection the instance creation method.
     *
     * @param config
     * @return a new {@link OracleERPConnection} connection wrapper
     */
    static OracleERPConnection createConnection(OracleERPConfiguration config) {
        final Connection connection = getNativeConnection(config);
        return new OracleERPConnection(connection, config);
    }

    /**
     * @param config
     * @return native {@link Connection} connection
     */
    static Connection getNativeConnection(OracleERPConfiguration config) {
        Connection connection;
        final String user = config.getUser();
        final GuardedString password = config.getPassword();
        final String datasource = config.getDataSource();
        if (StringUtil.isNotBlank(datasource)) {
            LOG.ok("Create datasource connection {0}", datasource);
            final String[] jndiProperties = config.getJndiProperties();
            final ConnectorMessages connectorMessages = config.getConnectorMessages();
            final Hashtable<String, String> prop =
                    JNDIUtil.arrayToHashtable(jndiProperties, connectorMessages);
            if (StringUtil.isNotBlank(user) && password != null) {
                connection = SQLUtil.getDatasourceConnection(datasource, user, password, prop);
            } else {
                connection = SQLUtil.getDatasourceConnection(datasource, prop);
            }
        } else {
            connection = getDriverMangerConnection(config);
        }
        if (connection instanceof OracleConnection) {
            final OracleConnection oracleConn = (OracleConnection) connection;
            /* On Oracle enable the synonyms */
            try {
                if (!oracleConn.getIncludeSynonyms()) {
                    LOG.ok("setIncludeSynonyms on ORACLE");
                    oracleConn.setIncludeSynonyms(true);
                    LOG.ok("setIncludeSynonyms success");
                }
            } catch (Exception e) {
                LOG.error(e, "setIncludeSynonyms on ORACLE exception");
            }
            // Set default row Prefetch
            try {
                if (oracleConn.getDefaultRowPrefetch() != DEFAULT_ROW_PREFETCH) {
                    LOG.ok("setDefaultRowPrefetch on ORACLE");
                    oracleConn.setDefaultRowPrefetch(DEFAULT_ROW_PREFETCH);
                    LOG.ok("setDefaultRowPrefetch success");
                }
            } catch (SQLException expected) {
                // expected
                LOG.error(expected, "setDefaultRowPrefetch exception");
            }
        }
        // Disable auto-commit mode
        try {
            if (connection.getAutoCommit()) {
                LOG.ok("setAutoCommit(false)");
                connection.setAutoCommit(false);
            }
        } catch (SQLException expected) {
            // expected
            LOG.error(expected, "setAutoCommit(false) exception");
        }
        return connection;
    }

    private OracleERPConfiguration config = null;

    /**
     * Use the {@link Configuration} passed in to immediately connect to a
     * database. If the {@link Connection} fails a {@link RuntimeException} will
     * be thrown.
     *
     * @param conn
     *            Real connection.
     * @param config
     *            the Oracle ERP configuration
     * @throws RuntimeException
     *             if there is a problem creating a {@link java.sql.Connection}.
     */
    private OracleERPConnection(Connection conn, OracleERPConfiguration config) {
        super(conn);
        this.config = config;
    }

    /**
     * Close connection if pooled.
     */
    public void closeConnection() {
        if (getConnection() != null && StringUtil.isNotBlank(config.getDataSource())
        /*&& this.conn.getConnection() instanceof PooledConnection */) {
            LOG.ok("Close the pooled connection");
            dispose();
        }
    }

    /**
     * Create new connection if pooled and taken from the datasource.
     *
     * @throws SQLException
     */
    public void openConnection() throws SQLException {
        if (getConnection() == null || getConnection().isClosed()) {
            LOG.ok("Get new connection, it is closed");
            setConnection(getNativeConnection(config));
        }
    }

    /**
     * OracleERP prepareCall statement with mapped callable statement parameters.
     *
     * @param sql
     *            a <CODE>String</CODE> sql statement definition
     * @return return a callable statement
     * @throws SQLException
     *             an exception in statement
     */
    public CallableStatement prepareCall(final String sql) throws SQLException {
        LOG.ok("prepareCall sql: {0}", sql);
        final CallableStatement cs = getConnection().prepareCall(sql);
        cs.setQueryTimeout(OracleERPUtil.ORACLE_TIMEOUT);
        return cs;
    }

    /**
     * OracleERP prepareCall statement with mapped callable statement parameters.
     *
     * @param sql
     *            a <CODE>String</CODE> sql statement definition
     * @param params
     *            the bind parameter values
     * @return return a callable statement
     * @throws SQLException
     *             an exception in statement
     */
    @Override
    public CallableStatement prepareCall(final String sql, final List<SQLParam> params)
            throws SQLException {
        LOG.ok("prepareCall sql: {0}", sql);
        final CallableStatement cs = super.prepareCall(sql, params);
        cs.setQueryTimeout(OracleERPUtil.ORACLE_TIMEOUT);
        return cs;
    }

    /**
     * OracleERP prepare statement using the query builder object.
     *
     * @param query
     *            DatabaseQueryBuilder query
     * @return return a prepared statement
     * @throws SQLException
     *             an exception in statement
     */
    @Override
    public PreparedStatement prepareStatement(DatabaseQueryBuilder query) throws SQLException {
        final PreparedStatement ps = super.prepareStatement(query);
        ps.setQueryTimeout(OracleERPUtil.ORACLE_TIMEOUT);
        return ps;
    }

    /**
     * OracleERP prepare statement.
     *
     * @param sql
     * @return the prepared statement
     * @throws SQLException
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        LOG.ok("prepareStatement sql: {0}", sql);
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setQueryTimeout(OracleERPUtil.ORACLE_TIMEOUT);
        return ps;
    }

    /**
     * OracleERP prepare statement with mapped prepare statement parameters.
     *
     * @param sql
     *            a <CODE>String</CODE> sql statement definition
     * @param params
     *            the bind parameter values
     * @return return a prepared statement
     * @throws SQLException
     *             an exception in statement
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final List<SQLParam> params)
            throws SQLException {
        LOG.ok("prepareStatement sql: {0}", sql);
        final PreparedStatement ps = super.prepareStatement(sql, params);
        ps.setQueryTimeout(OracleERPUtil.ORACLE_TIMEOUT);
        return ps;
    }

    /**
     * Return the driver manager connection.
     *
     * @param config
     *            parameters
     * @return
     */
    private static Connection getDriverMangerConnection(final OracleERPConfiguration config) {
        final String connectionUrl = config.getConnectionUrl();
        LOG.ok("getDriverMangerConnection connectionUrl {0}", connectionUrl);

        // create the connection base on the configuration..
        final Connection[] ret = new Connection[1];
        try {
            // load the driver class..
            Class.forName(config.getDriver());
            // get the database URL..
            if (config.getPassword() == null) {
                throw new IllegalStateException(config.getMessage(MSG_PASSWORD_BLANK));
            }

            if (StringUtil.isBlank(config.getUser())) {
                throw new IllegalStateException(config.getMessage(MSG_USER_BLANK));
            }
            // check if there is authentication involved.
            config.getPassword().access(new GuardedString.Accessor() {
                public void access(char[] clearChars) {
                    try {
                        java.util.Properties info = new java.util.Properties();
                        info.put("user", config.getUser());
                        info.put("password", new String(clearChars));
                        if (StringUtil.isNotBlank(config.getClientEncryptionType())) {
                            LOG.ok("getDriverMangerConnection set oracle.net.encryption_types_client {0}",
                                    config.getClientEncryptionType());
                            info.put("oracle.net.encryption_types_client", config
                                    .getClientEncryptionType());
                        }
                        if (StringUtil.isNotBlank(config.getClientEncryptionLevel())) {
                            LOG.ok("getDriverMangerConnection set oracle.net.encryption_client {0}",
                                    config.getClientEncryptionLevel());
                            info.put("oracle.net.encryption_client", config
                                    .getClientEncryptionLevel());
                        }

                        ret[0] = DriverManager.getConnection(connectionUrl, info);
                    } catch (SQLException e) {
                        // checked exception are not allowed in the access
                        // method
                        // Lets use the exception softening pattern
                        throw new RuntimeException(e);
                    }
                }
            });
            // turn off auto-commit
        } catch (Exception e) {
            throw ConnectorException.wrap(e);
        }
        return ret[0];
    }
}
