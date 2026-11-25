package com.bank.skbankofindia.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static HikariDataSource dataSource;
    private static boolean isH2Fallback = false;

    static {
        initDataSource();
    }

    public static synchronized void initDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        try {
            HikariConfig config = new HikariConfig();

            String envDbUrl = System.getenv("DATABASE_URL");
            if (envDbUrl == null) envDbUrl = System.getenv("MYSQL_URL");

            String host = System.getenv().getOrDefault("DB_HOST", "localhost");
            String port = System.getenv().getOrDefault("DB_PORT", "3306");
            String dbName = System.getenv().getOrDefault("DB_NAME", "sk_bank_of_india");
            String user = System.getenv().getOrDefault("DB_USER", "root");
            String pass = System.getenv().getOrDefault("DB_PASS", "");

            String jdbcUrl;
            if (envDbUrl != null && !envDbUrl.isEmpty()) {
                jdbcUrl = envDbUrl;
            } else {
                jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8", host, port, dbName);
            }

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(pass);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setMaximumPoolSize(15);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(5000); // 5s timeout to attempt MySQL connection fast

            dataSource = new HikariDataSource(config);
            // Test connection
            try (Connection conn = dataSource.getConnection()) {
                LOGGER.info("Successfully connected to MySQL database: " + dbName);
                isH2Fallback = false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "MySQL connection failed. Activating H2 In-Memory Database Fallback for Cloud/Standalone execution.", e);
            initH2Fallback();
        }
    }

    private static synchronized void initH2Fallback() {
        try {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:h2:mem:sk_bank_of_india;MODE=MySQL;DB_CLOSE_DELAY=-1;CASE_INSENSITIVE_IDENTIFIERS=TRUE;NON_KEYWORDS=USER,VALUE");
            config.setUsername("sa");
            config.setPassword("");
            config.setDriverClassName("org.h2.Driver");
            config.setMaximumPoolSize(10);

            dataSource = new HikariDataSource(config);
            isH2Fallback = true;

            // Execute schema.sql and seed_data.sql in H2
            try (Connection conn = dataSource.getConnection()) {
                executeSqlScript(conn, "/db/schema.sql");
                executeSqlScript(conn, "/db/seed_data.sql");
                LOGGER.info("Successfully initialized H2 In-Memory Database schema & seed data.");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to initialize H2 Fallback Database", ex);
        }
    }

    private static void executeSqlScript(Connection conn, String scriptPath) {
        try (InputStream is = DatabaseConfig.class.getResourceAsStream(scriptPath)) {
            if (is == null) return;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("--") || line.trim().isEmpty()) continue;
                sb.append(line).append("\n");
            }

            String[] statements = sb.toString().split(";");
            try (Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    if (!sql.trim().isEmpty()) {
                        try {
                            stmt.execute(sql.trim());
                        } catch (SQLException e) {
                            // Ignore harmless DDL syntax differences in H2 fallback
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error executing script " + scriptPath, e);
        }
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            initDataSource();
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    public static boolean isH2Fallback() {
        return isH2Fallback;
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.info("HikariCP DataSource closed.");
        }
    }

    private DatabaseConfig() {}
}
