package br.com.west.lojas.database;

import br.com.west.lojas.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private Connection connection;
    private boolean active;


    public void openConnection() {
        String host = Main.getInstance().getConfig().getString("mysql.host");
        String database = Main.getInstance().getConfig().getString("mysql.database");
        String user = Main.getInstance().getConfig().getString("mysql.user");
        String password = Main.getInstance().getConfig().getString("mysql.password");
        String url = "jdbc:mysql://" + host + ":3306/" + database + "?rewriteBatchedStatements=true";
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (connection == null || !active)
            openConnection();
        return connection;
    }

    public void closeConnection() {
        if (connection != null || active) {
            try {
                active = false;
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
    }
}
