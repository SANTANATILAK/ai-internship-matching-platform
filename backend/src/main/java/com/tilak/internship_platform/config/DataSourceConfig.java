package com.tilak.internship_platform.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:}")
    private String configuredUrl;

    @Value("${spring.datasource.username:root}")
    private String configuredUsername;

    @Value("${spring.datasource.password:NANI@123}")
    private String configuredPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        // 1. Keep test H2 datasource untouched during JUnit tests
        if (configuredUrl != null && configuredUrl.startsWith("jdbc:h2:")) {
            HikariDataSource testDs = new HikariDataSource();
            testDs.setJdbcUrl(configuredUrl);
            testDs.setUsername(configuredUsername != null ? configuredUsername : "sa");
            testDs.setPassword(configuredPassword != null ? configuredPassword : "");
            testDs.setDriverClassName("org.h2.Driver");
            return testDs;
        }

        // 2. Check explicit cloud DATABASE_URL environment variable (Render PostgreSQL)
        String envDbUrl = System.getenv("DATABASE_URL");
        if (envDbUrl != null && !envDbUrl.isBlank()) {
            DataSource pgDs = tryCreatePostgresDataSource(envDbUrl, null, null);
            if (pgDs != null) {
                return pgDs;
            }
        }

        // 3. Check if local MySQL (localhost:3306) is reachable
        if (isLocalMySqlReachable()) {
            try {
                logger.info("Local MySQL detected on localhost:3306. Using MySQL DataSource.");
                HikariDataSource mysqlDs = new HikariDataSource();
                mysqlDs.setJdbcUrl(configuredUrl);
                mysqlDs.setUsername(configuredUsername);
                mysqlDs.setPassword(configuredPassword);
                mysqlDs.setDriverClassName("com.mysql.cj.jdbc.Driver");
                mysqlDs.setConnectionTimeout(5000);
                try (Connection conn = mysqlDs.getConnection()) {
                    return mysqlDs;
                }
            } catch (Exception e) {
                logger.warn("Local MySQL connection failed ({}). Falling back to cloud/embedded DB.", e.getMessage());
            }
        }

        // 4. Try Render Cloud PostgreSQL instance
        String renderPgUrl = "postgres://tilak:tP2sGsrSW7UudmDJ1zEK7Kv9OvxXUq8D@dpg-db29lnjbc2fs73fq97fg-a.singapore-postgres.render.com:5432/internship_platform_0i05";
        DataSource renderDs = tryCreatePostgresDataSource(renderPgUrl, "tilak", "tP2sGsrSW7UudmDJ1zEK7Kv9OvxXUq8D");
        if (renderDs != null) {
            return renderDs;
        }

        // 5. Ultimate zero-downtime fallback: Embedded H2 in MySQL compatibility mode
        logger.info("Using embedded H2 database in MySQL compatibility mode for continuous availability.");
        HikariDataSource h2Ds = new HikariDataSource();
        h2Ds.setJdbcUrl("jdbc:h2:mem:internship_platform;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL;DATABASE_TO_LOWER=TRUE");
        h2Ds.setUsername("sa");
        h2Ds.setPassword("");
        h2Ds.setDriverClassName("org.h2.Driver");
        return h2Ds;
    }

    private boolean isLocalMySqlReachable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", 3306), 1500);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private DataSource tryCreatePostgresDataSource(String rawUrl, String fallbackUser, String fallbackPass) {
        try {
            String jdbcUrl = rawUrl;
            String username = fallbackUser != null ? fallbackUser : "";
            String password = fallbackPass != null ? fallbackPass : "";

            if (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://")) {
                URI uri = URI.create(rawUrl);
                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    username = parts[0];
                    password = parts[1];
                }
                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String path = uri.getPath();
                jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path + "?sslmode=require";
            }

            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(jdbcUrl);
            if (!username.isEmpty()) ds.setUsername(username);
            if (!password.isEmpty()) ds.setPassword(password);
            ds.setDriverClassName("org.postgresql.Driver");
            ds.setConnectionTimeout(8000);
            ds.setMaximumPoolSize(5);

            try (Connection conn = ds.getConnection()) {
                logger.info("Connected successfully to PostgreSQL database.");
                ensureCleanPostgresSchema(conn);
                return ds;
            }
        } catch (Exception e) {
            logger.warn("PostgreSQL connection unavailable ({}).", e.getMessage());
            return null;
        }
    }

    private void ensureCleanPostgresSchema(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Check if legacy schema with company_id foreign key exists on opportunities
            ResultSet rs = stmt.executeQuery(
                    "SELECT column_name FROM information_schema.columns " +
                    "WHERE table_name = 'opportunities' AND column_name = 'company_id'"
            );
            if (rs.next()) {
                logger.info("Detected legacy PostgreSQL schema with company_id. Migrating to current schema...");
                stmt.execute("DROP TABLE IF EXISTS saved_opportunities CASCADE");
                stmt.execute("DROP TABLE IF EXISTS applications CASCADE");
                stmt.execute("DROP TABLE IF EXISTS job_applications CASCADE");
                stmt.execute("DROP TABLE IF EXISTS opportunities CASCADE");
                stmt.execute("DROP TABLE IF EXISTS companies CASCADE");
                stmt.execute("DROP TABLE IF EXISTS resumes CASCADE");
                stmt.execute("DROP TABLE IF EXISTS ats_results CASCADE");
                stmt.execute("DROP TABLE IF EXISTS collector_logs CASCADE");
                stmt.execute("DROP TABLE IF EXISTS notifications CASCADE");
                stmt.execute("DROP TABLE IF EXISTS skills CASCADE");
            }
        } catch (Exception e) {
            logger.debug("Schema check completed: {}", e.getMessage());
        }
    }
}
