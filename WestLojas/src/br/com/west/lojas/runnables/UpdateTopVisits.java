package br.com.west.lojas.runnables;

import br.com.west.lojas.Main;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateTopVisits extends BukkitRunnable {
    @Override
    public void run() {
        Main.getInstance().getDatabaseMethods().top();
    }
}
