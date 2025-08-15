package org.apache.myfaces.transforma.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Utility class for managing SQLite database connections and initialization.
 * 
 * @author Transforma-Faces Team
 * @version 1.0.0
 */
public class DatabaseUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);
    private static final String DB_FILE = "transforma_tasks.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;
    
    private static HikariDataSource dataSource;
    
    static {
        initializeDatabase();
    }
    
    /**
     * Initialize the database and connection pool.
     */
    private static void initializeDatabase() {
        try {
            // Create database file if it doesn't exist
            File dbFile = new File(DB_FILE);
            if (!dbFile.exists()) {
                logger.info("Creating new SQLite database: {}", DB_FILE);
            }
            
            // Configure HikariCP connection pool
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setDriverClassName("org.sqlite.JDBC");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setPoolName("TransformaFacesPool");
            
            // Create the data source
            dataSource = new HikariDataSource(config);
            
            // Initialize database schema
            initializeSchema();
            
            logger.info("Database initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Initialize the database schema with required tables.
     */
    private static void initializeSchema() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS tasks (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title TEXT NOT NULL," +
            "description TEXT," +
            "priority TEXT NOT NULL DEFAULT 'Medium'," +
            "status TEXT NOT NULL DEFAULT 'To Do'," +
            "created_date TEXT NOT NULL," +
            "updated_date TEXT" +
            ")";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTableSQL);
            logger.info("Database schema initialized");
            
            // Insert sample data if table is empty
            insertSampleDataIfEmpty(conn);
            
        } catch (SQLException e) {
            logger.error("Failed to initialize schema", e);
            throw new RuntimeException("Schema initialization failed", e);
        }
    }
    
    /**
     * Insert sample data if the tasks table is empty.
     */
    private static void insertSampleDataIfEmpty(Connection conn) throws SQLException {
        String countSQL = "SELECT COUNT(*) FROM tasks";
        String insertSQL = "INSERT INTO tasks (title, description, priority, status, created_date) VALUES " +
            "('Complete project setup', 'Set up the development environment and configure dependencies', 'High', 'In Progress', datetime('now')), " +
            "('Design user interface', 'Create wireframes and mockups for the main application', 'Medium', 'To Do', datetime('now')), " +
            "('Implement core functionality', 'Develop the main business logic and data models', 'High', 'To Do', datetime('now')), " +
            "('Write unit tests', 'Create comprehensive test coverage for all components', 'Medium', 'To Do', datetime('now')), " +
            "('Documentation', 'Write user and developer documentation', 'Low', 'To Do', datetime('now'))";
        
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(countSQL);
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute(insertSQL);
                logger.info("Sample data inserted");
            }
        }
    }
    
    /**
     * Get a database connection from the pool.
     * 
     * @return a database connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not initialized");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Get the data source for advanced usage.
     * 
     * @return the data source
     */
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * Close the database connection pool.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
} 