package com.github.davidpotentini.cerne2.configuration.multitenancy;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class SchemaMultiTenantConnectionProvider
        extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String>
        implements HibernatePropertiesCustomizer {

    private static final Pattern SCHEMA_NAME = Pattern.compile("^[a-z0-9_]+$");

    private final DataSource dataSource;

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return dataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return dataSource;
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = selectDataSource(tenantIdentifier).getConnection();
        String schema = resolveSchema(tenantIdentifier);
        try (Statement statement = connection.createStatement()) {
            if (TenantIdentifierResolver.DEFAULT_TENANT.equals(schema)) {
                statement.execute("SET search_path TO public");
            } else {
                statement.execute("SET search_path TO \"" + schema + "\", public");
            }
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET search_path TO public");
        } finally {
            connection.close();
        }
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }

    private String resolveSchema(String tenantIdentifier) {
        if (tenantIdentifier == null || tenantIdentifier.isBlank()) {
            return TenantIdentifierResolver.DEFAULT_TENANT;
        }
        if (!SCHEMA_NAME.matcher(tenantIdentifier).matches()) {
            throw new IllegalArgumentException("Invalid tenant identifier: " + tenantIdentifier);
        }
        return tenantIdentifier;
    }
}
