package br.com.compass.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DB {

    private static Connection conn = null;

    public static Connection getConnection() {
        try{
            if (conn == null || conn.isClosed()) {
                Properties props = loadProperties();
                String url = props.getProperty("dburl");
                conn = DriverManager.getConnection(url, props);

                databaseMigrations();
            }
        }
        catch (SQLException e) {
            throw new DbException("Error getting connection: " + e.getMessage());
        }
        return conn;
    };

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException e) {
                throw new DbException("Error closing connection: " + e.getMessage());
            }
        }
    }

    private static Properties loadProperties() {
        try(FileInputStream fs = new FileInputStream("db.properties")){
            Properties props = new Properties();
            props.load(fs);
            return props;
        }
        catch ( IOException e) {
            throw new DbException("Error loading database properties: " + e.getMessage());
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try{
                stmt.close();
            } catch (SQLException e) {
                throw new DbException("Error closing statement: " + e.getMessage());
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try{
                rs.close();
            } catch (SQLException e) {
                throw new DbException("Error closing ResultSet: " + e.getMessage());
            }
        }
    }


    private static void databaseMigrations() {
        String createUsersTable = """
        CREATE TABLE IF NOT EXISTS users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            birth_date DATE NOT NULL,
            cpf VARCHAR(11) UNIQUE NOT NULL,
            phone VARCHAR(15) NOT NULL,
            password VARCHAR(255) NOT NULL
        );
    """;

        String createAccountsTable = """
        CREATE TABLE IF NOT EXISTS accounts (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            account_type ENUM('CHECKING', 'PAYROLL', 'SAVINGS') NOT NULL,
            balance DECIMAL(15,2) DEFAULT 0.00,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        );
    """;

        String createTransactionsTable = """
        CREATE TABLE IF NOT EXISTS transactions (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            account_id INT NOT NULL,
            transaction_type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER') NOT NULL,
            amount DECIMAL(15,2) NOT NULL,
            transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            destination_account_id INT,
            FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
            FOREIGN KEY (destination_account_id) REFERENCES accounts(id),
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        );
    """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createAccountsTable);
            stmt.execute(createTransactionsTable);
        } catch (SQLException e) {
            throw new DbException("Error initializing database: " + e.getMessage());
        }
    }
}
