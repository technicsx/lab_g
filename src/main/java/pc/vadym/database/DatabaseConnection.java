package pc.vadym.database;

import pc.vadym.configurator.PropsLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private Connection connection;
    private static DatabaseConnection instance;

    public DatabaseConnection() {
        Properties conf = PropsLoader.getInstance().props;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(conf.getProperty("dbUrl"), conf.getProperty("dbUser"), conf.getProperty("dbPass"));
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void closeExistingConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection to the PostgreSQL was closed.");
        }
    }

    public Connection getExisitingConnection() {
        return connection;
    }
}
