package dictionary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/dictionary_db";
    private static final String USER = "pratik2753";
    private static final String PASSWORD = "pratik2753"; // 🔁 Replace with your MySQL password

    // Static method to get DB connection
    public static Connection getConnection() {
        Connection connection = null;

        try {
            // Load the JDBC driver (optional for newer versions)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database Connected Successfully!");

        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to DB: " + e.getMessage());
        }

        return connection;
    }

    // Main method to test the connection
    public static void main(String[] args) {
        getConnection();
    }
}
