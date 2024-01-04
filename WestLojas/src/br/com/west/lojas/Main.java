package br.com.west.lojas;

import br.com.west.lojas.database.DatabaseConnector;
import br.com.west.lojas.database.DatabaseMethods;
import br.com.west.lojas.managers.LojaManager;
import br.com.west.lojas.runnables.SaveLojas;
import br.com.west.lojas.runnables.UpdateTopVisits;
import br.com.west.lojas.utils.ClassGeter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private LojaManager lojaManager;
    private DatabaseConnector databaseConnector;
    private DatabaseMethods databaseMethods;
    private Economy economy = null;
    private static Main instance;

    public void onEnable() {
        instance = this;
        databaseConnector = new DatabaseConnector();
        databaseMethods = new DatabaseMethods();
        lojaManager = new LojaManager();
        ClassGeter classGeter = new ClassGeter();
        classGeter.setupCommands();
        classGeter.setupListeners();
        saveDefaultConfig();
        loadDatabase();
        loadTasks();
        if (!setupEconomy()) Bukkit.getPluginManager().disablePlugin(this);

        getLogger().info("Plugin feito por WestDev");
        getLogger().info("Veja mais de meus plugins em https://gamersboard.com.br/profile/46546-westdev/");
        getLogger().info("Discord: weestmine");
        getLogger().info("Versão do Plugin: " + getDescription().getVersion());

    }
    
    public void onDisable() {
        getDatabaseMethods().save();

    }

    private void loadDatabase() {
        getDatabaseMethods().create();
        getDatabaseMethods().load();
    }

    private void loadTasks() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new SaveLojas(), 20 * 60 * 30, 20 * 60 * 30);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new UpdateTopVisits(), 0, 20 * 60 * 15);
    }

    public String serializeLocation(Location location) {
        if (location == null) return null;
        return location.getWorld().getName() + ',' +
                location.getX() + ',' +
                location.getY() + ',' +
                location.getZ() + ',' +

                location.getYaw() + ',' +
                location.getPitch();
    }

    public Location deserializeLocation(String location) {
        if (location == null) return null;
        String[] splitLocation = location.split(",");
        return new Location(
                Bukkit.getWorld(splitLocation[0]),
                Double.parseDouble(splitLocation[1]),
                Double.parseDouble(splitLocation[2]),
                Double.parseDouble(splitLocation[3]),
                Float.parseFloat(splitLocation[4]),
                Float.parseFloat(splitLocation[5]));
    }

    public String format(Double value) {
        String[] suffix = {
                "K", "M", "B", "T", "Q", "QQ", "S", "SS", "OC", "N",
                "D", "UN", "DD",
                "TR", "QT", "QN", "SD", "SSD", "OD", "ND",
                "VG", "UVG", "DVG", "TVG", "QVG", "QVN", "SEV", "SPV", "OVG",
                "NVG",
                "TG"};
        int size = (value.intValue() != 0) ? (int) Math.log10(value) : 0;
        if (size >= 3)
            while (size % 3 != 0)
                size--;
        double notation = Math.pow(10.0D, size);
        return (size >= 3) ? (
                String.valueOf(Math.round(value / notation * 100.0D) / 100.0D) + suffix[size / 3 - 1]) : String.valueOf(value.doubleValue());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public double getLojaPrice() {
        return getConfig().getDouble("criar_loja_preco");
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    public DatabaseMethods getDatabaseMethods() {
        return databaseMethods;
    }

    public LojaManager getLojaManager() {
        return lojaManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public static Main getInstance() {
        return instance;
    }
}
