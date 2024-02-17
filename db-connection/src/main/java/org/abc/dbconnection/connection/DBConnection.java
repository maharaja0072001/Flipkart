package org.abc.dbconnection.connection;

import org.abc.dbconnection.exceptions.ConnectionFailedException;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * <p>
 * Provides connection with the database.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class DBConnection {

    private static Connection connection;
    private static final Logger LOGGER = LogManager.getLogger(DBConnection.class);

    /**
     * <p>
     * Default constructor of DBConnection class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private DBConnection() {}

    /**
     * <p>
     * Creates a connection with database and returns it.
     * </p>
     *
     * @return {@link Connection} of the database.
     */
    public static Connection getConnection() {
        if (Objects.isNull(connection)) {
            final Properties properties = new Properties();

            try (FileReader fileReader = new FileReader(String.join("",
                    System.getenv("DB_CONFIG_PATH"), "/db.properties"))) {
                properties.load(fileReader);
                Class.forName("org.postgresql.Driver");

                connection = DriverManager.getConnection(properties.getProperty("url"),
                        properties.getProperty("username"), properties.getProperty("password"));

                LOGGER.info("Database is connected");
            } catch (final Exception exception) {
                LOGGER.error("Database Connection failed");
                throw new ConnectionFailedException(exception.getMessage());
            }
        }

        return connection;
    }
}
