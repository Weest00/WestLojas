package br.com.west.lojas.runnables;

import br.com.west.lojas.Main;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveLojas extends BukkitRunnable {
    @Override
    public void run() {
        Main.getInstance().getDatabaseMethods().save();
        Main.getInstance().getLojaManager().getActivePlayers().clear();
    }
}
