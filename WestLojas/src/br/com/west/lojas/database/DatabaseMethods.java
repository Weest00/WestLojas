package br.com.west.lojas.database;

import br.com.west.lojas.Main;
import br.com.west.lojas.managers.LojaManager;
import br.com.west.lojas.models.Loja;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseMethods {

    public void create() {
        try {
            PreparedStatement createStatement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS west_lojas (owner TEXT,status BOOLEAN,location TEXT,announcement TEXT,visits INTEGER)");
            createStatement.executeUpdate();
            Bukkit.getConsoleSender().sendMessage("§a[MYSQL] §fTabela criada/carrega com sucesso.");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§a[MYSQL] §fErro ao criar tabela no banco de dados.");

        } finally {
            closeConnection();
        }
    }

    public void load() {
        LojaManager manager = Main.getInstance().getLojaManager();
        int i = 0;

        try {
            PreparedStatement loadStatement = getConnection().prepareStatement("SELECT * FROM west_lojas");
            ResultSet rs = loadStatement.executeQuery();
            while (rs.next()) {
                String owner = rs.getString("owner");
                boolean status = rs.getBoolean("status");
                String location = rs.getString("location");
                String announcement = rs.getString("announcement");
                int visits = rs.getInt("visits");
                Location desarializedlocation = Main.getInstance().deserializeLocation(location);
                Loja loja = new Loja(owner, announcement, desarializedlocation, visits, status);
                manager.loadLoja(owner, loja);
                manager.getLojas().add(loja);
                i++;
            }

            Bukkit.getConsoleSender().sendMessage("§a[MYSQL] §fForam carregadas " + i + "§f lojas do banco de dados.");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§a[MYSQL] §fErro ao carregar as lojas do banco de dados.");
        } finally {
            closeConnection();
        }
    }

    public void save() {
        LojaManager manager = Main.getInstance().getLojaManager();
        int i = 0;
        try (PreparedStatement insertStatement = getConnection().prepareStatement("INSERT INTO west_lojas (owner,status,location,announcement,visits) VALUES (?,?,?,?,?)")) {
            try (PreparedStatement updateStatement = getConnection().prepareStatement("UPDATE west_lojas SET status =?,location =?,announcement =?,visits =? WHERE owner =?")) {
                for (Loja loja : manager.getLoja().values()) {
                    if (!manager.getActivePlayers().contains(loja.getOwner())) continue;

                    if (manager.getLoadedPlayers().contains(loja.getOwner())) {
                        updateStatement.setBoolean(1, loja.getStatus());
                        updateStatement.setString(2, Main.getInstance().serializeLocation(loja.getLocation()));
                        updateStatement.setString(3, loja.getAnnouncement());
                        updateStatement.setInt(4, loja.getVisits());
                        updateStatement.setString(5, loja.getOwner());
                        updateStatement.addBatch();
                    } else {
                        insertStatement.setString(1, loja.getOwner());
                        insertStatement.setBoolean(2, loja.getStatus());
                        insertStatement.setString(3, Main.getInstance().serializeLocation(loja.getLocation()));
                        insertStatement.setString(4, loja.getAnnouncement());
                        insertStatement.setInt(5, loja.getVisits());
                        insertStatement.addBatch();
                    }
                    i++;
                }
                updateStatement.executeBatch();
                insertStatement.executeBatch();
                Bukkit.getConsoleSender().sendMessage("§a[MYSQL] §fForam salvos " + i + "§f lojas no banco de dados.");
            }

        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§a[MYSQL] §fErro ao salvar as lojas no banco de dados.");
        } finally {
            closeConnection();
        }
    }

    public void top() {
        LojaManager manager = Main.getInstance().getLojaManager();
        if (!manager.getTopVisits().isEmpty())
            manager.getTopVisits().clear();
        try {
            PreparedStatement topStatement = getConnection().prepareStatement("SELECT * FROM west_lojas ORDER BY visits DESC LIMIT 10");
            ResultSet rs = topStatement.executeQuery();
            while (rs.next()) {
                String user = rs.getString("owner");
                manager.getTopVisits().add(user);
            }
            Bukkit.getConsoleSender().sendMessage("§a[MYSQL] §fO TOP lojas mais visitadas foi atualizado com sucesso.");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§a[MYSQL] §fErro ao atualizar o top visitas.");
        } finally {
            closeConnection();
        }
    }

    private Connection getConnection() {
        return Main.getInstance().getDatabaseConnector().getConnection();
    }

    private void closeConnection() {
        Main.getInstance().getDatabaseConnector().closeConnection();
    }

}
