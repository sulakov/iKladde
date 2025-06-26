package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Hilfsklasse zum Zugriff auf die SQLite-Datenbank.
 * Stellt die Verbindung her und erstellt bei Bedarf die notwendigen Tabellen.
 */
public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:database/ikladde.db";

    /**
     * Stellt eine Verbindung zur Datenbank her und initialisiert das Schema falls nötig.
     *
     * @return Aktive Datenbankverbindung
     * @throws SQLException falls ein Fehler beim Verbindungsaufbau auftritt
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        initSchemaIfMissing(conn);
        return conn;
    }

    /**
     * Erstellt die benötigten Tabellen, falls sie noch nicht existieren.
     *
     * @param conn Aktive Verbindung zur Datenbank
     */
    private static void initSchemaIfMissing(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("PRAGMA foreign_keys = ON;");

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS recipe (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    photoPath TEXT,
                    rating INTEGER DEFAULT 0,
                    portions INTEGER DEFAULT 2,
                    createdDate TEXT
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS ingredient (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    recipe_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    quantity REAL,
                    unit TEXT,
                    FOREIGN KEY (recipe_id) REFERENCES recipe(id) ON DELETE CASCADE
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS step (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    recipe_id INTEGER NOT NULL,
                    number INTEGER NOT NULL,
                    description TEXT,
                    FOREIGN KEY (recipe_id) REFERENCES recipe(id) ON DELETE CASCADE
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS tag (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS recipe_tag (
                    recipe_id INTEGER NOT NULL,
                    tag_id INTEGER NOT NULL,
                    PRIMARY KEY (recipe_id, tag_id),
                    FOREIGN KEY (recipe_id) REFERENCES recipe(id) ON DELETE CASCADE,
                    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
                );
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
