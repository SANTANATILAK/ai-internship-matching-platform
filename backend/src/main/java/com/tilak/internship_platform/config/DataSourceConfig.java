package com.tilak.internship_platform.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String envDbUrl = System.getenv("DATABASE_URL");

        // If Render or cloud environment provides DATABASE_URL in postgres:// format
        if (envDbUrl != null && (envDbUrl.startsWith("postgres://") || envDbUrl.startsWith("postgresql://"))) {
            try {
                logger.info("Detecting cloud DATABASE_URL, parsing URI...");
                URI uri = URI.create(envDbUrl);
                String userInfo = uri.getUserInfo();
                String username = "";
                String password = "";

                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    username = parts[0];
                    password = parts[1];
                }

                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String path = uri.getPath(); // e.g. /internship_platform_0i05

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path + "?sslmode=require";
                logger.info("Configured JDBC URL: jdbc:postgresql://{}:{}{}", host, port, path);

                properties.setUrl(jdbcUrl);
                if (!username.isEmpty()) properties.setUsername(username);
                if (!password.isEmpty()) properties.setPassword(password);
                properties.setDriverClassName("org.postgresql.Driver");

            } catch (Exception e) {
                logger.warn("Could not parse cloud DATABASE_URL: {}. Falling back to default properties.", e.getMessage());
            }
        }

        return properties.initializeDataSourceBuilder().build();
    }
}
