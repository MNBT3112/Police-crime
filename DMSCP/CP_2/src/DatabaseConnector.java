import java.sql.*;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/policedb";
    private static final String USER = "root";
    private static final String PASSWORD = "4321";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("✅ Connected to Database Successfully!");
        return conn;
    }

    // Test method
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Connection Test Passed!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Connection Failed: " + e.getMessage());
        }
    }
}
