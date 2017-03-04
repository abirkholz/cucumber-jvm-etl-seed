package cucumber_jvm_etl_seed.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connect {

    private static Connection connection = null;

    public static void setConnection(String environment, String database) {
        try {
            String propertiesFile = getPropertiesFile(environment);
            InputStream inputStream = Connect.class.getClassLoader().getResourceAsStream(propertiesFile);
            Properties props = new Properties();
            props.load(inputStream);
            String url = props.getProperty("url");

            if (environment.equals("postgres")) {
                url = url + "/"+ database;
            }
            else {
                props.setProperty("databaseName", database);
            }

            Class.forName(props.getProperty("driver")).newInstance();
            connection = DriverManager.getConnection(url, props);
            connection.setAutoCommit(true);

        } catch (IOException | IllegalAccessException | SQLException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try { connection.close(); } catch (SQLException e) {  e.printStackTrace(); }
    }

    private static String getPropertiesFile(String hostname) {

        String path = "cucumber_jvm_etl_seed/database/properties/";
        switch (hostname){
            case "postgres": return path + "postgres.properties";
            case "mssql-docker": return path + "mssql-docker.properties";
            default: return null;
        }
    }

}

