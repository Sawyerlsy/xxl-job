package com.xxl.job.admin.core.conf;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * dabaseIdProvider
 *
 * @author Sawyer
 * @version 1.0
 * @date 2025-07-03 10:54
 */
public class ExactMatchDatabaseIdProvider implements DatabaseIdProvider {

    private Properties properties;

    @Override
    public String getDatabaseId(DataSource dataSource) {
        if (dataSource == null) {
            throw new NullPointerException("dataSource cannot be null");
        }
        try {
            return getDatabaseName(dataSource);
        } catch (Exception e) {
            ExactMatchDatabaseIdProvider.LogHolder.log.error("Could not get a databaseId from dataSource", e);
        }
        return null;
    }

    @Override
    public void setProperties(Properties p) {
        this.properties = p;
    }

    private String getDatabaseName(DataSource dataSource) throws SQLException {
        String productName = getDatabaseProductName(dataSource);
        if (this.properties != null) {
            for (Map.Entry<Object, Object> property : properties.entrySet()) {
                if (productName.contains((String) property.getKey())) {
                    return (String) property.getValue();
                }
            }
            // no match, return null
            return null;
        }
        return productName;
    }

    private String getDatabaseProductName(DataSource dataSource) throws SQLException {
        try (Connection con = dataSource.getConnection()) {
            DatabaseMetaData metaData = con.getMetaData();
            return metaData.getDatabaseProductName();
        }

    }

    private static class LogHolder {
        private static final Log log = LogFactory.getLog(VendorDatabaseIdProvider.class);
    }
}
