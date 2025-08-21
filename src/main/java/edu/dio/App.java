package edu.dio;

import edu.dio.persistence.config.ConnectionConfig;
import edu.dio.persistence.migration.MigrationStrategy;
import edu.dio.ui.MainMenu;

import java.sql.Connection;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException {
        System.out.println("Rodando");
        try(Connection connection = ConnectionConfig.getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        }
        new MainMenu().execute();
    }
}
