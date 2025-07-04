package com.xxl.job.admin.core.conf;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * mybatis configuration
 *
 * @author sawyer
 * @version 1.0
 * @date 2025-07-03 10:43
 */
@Configuration
public class MyBatisConfig {

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        ExactMatchDatabaseIdProvider provider = new ExactMatchDatabaseIdProvider();
        Properties properties = new Properties();
        // 国际主流数据库
        properties.put("MySQL", "mysql");
        properties.put("Oracle", "oracle");
        properties.put("PostgreSQL", "postgresql");
        properties.put("SQL Server", "sqlserver");
        properties.put("DB2", "db2");
        properties.put("MariaDB", "mariadb");
        // 国产数据库
        properties.put("DM DBMS", "dm");    // 达梦
        properties.put("KingbaseES", "kingbase");        // 人大金仓
        //properties.put("GaussDB", "gaussdb");            // 华为高斯,实际使用的是postgresql
        properties.put("OceanBase", "oceanbase");        // 蚂蚁OceanBase
        properties.put("TDSQL", "tdsql");                // 腾讯TDSQL
        // 云数据库 & 其他
        properties.put("Amazon Redshift", "redshift");
        properties.put("Snowflake", "snowflake");
        properties.put("SQLite", "sqlite");
        properties.put("Sybase", "sybase");
        properties.put("Informix", "informix");
        properties.put("H2", "h2");
        properties.put("HSQL Database Engine", "hsqldb");
        provider.setProperties(properties);
        return provider;
    }

}
