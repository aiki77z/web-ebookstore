package com.ebookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

@Configuration
public class TransactionConfig {

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        // 使用 JDBC 事务管理器，支持 NESTED
        DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
        txManager.setNestedTransactionAllowed(true); // 开启嵌套事务支持
        return txManager;
    }
}
