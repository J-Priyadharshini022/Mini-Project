package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection 
{
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce";  // Replace with your DB URL
    private static final String DB_USER = "root";      // Replace with your DB username
    private static final String DB_PASSWORD = "root";  // Replace with your DB password

    public static Connection getConnection() throws SQLException 
    {
        try 
        {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL driver class
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } 
        catch (ClassNotFoundException e) 
        {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }
    }
    public static void closeConnection(Connection connection) 
    {
        if (connection != null) 
        {
            try 
            {
                connection.close();
            } catch (SQLException e) 
            {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
