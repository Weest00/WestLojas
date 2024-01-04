package br.com.west.lojas.listeners;

import br.com.west.lojas.Main;
import br.com.west.lojas.enums.ChatType;
import br.com.west.lojas.managers.LojaManager;
import br.com.west.lojas.models.Loja;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {

    @EventHandler
    void onPlayerChat(AsyncPlayerChatEvent event) {
        PlotAPI plotAPI = new PlotAPI();
        Player player = event.getPlayer();
        String playerName = player.getName();
        String message = event.getMessage().replace('&', '§');

        LojaManager manager = Main.getInstance().getLojaManager();
        Loja playerLoja = manager.getLoja(playerName);
        ChatType chatType = manager.getChat().get(playerName);

        if (chatType == null) return;
        event.setCancelled(true);

        if (message.equalsIgnoreCase("cancelar")) {
            player.sendMessage("§cOperação cancelada com sucesso.");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
            manager.getChat().remove(playerName);
            return;
        }

        switch (chatType) {
            case ANNOUNCEMENT:
                if (message.length() > 65){
                    player.sendMessage("§cSeu novo anúncio passou do limite permitido §7(65)§c.");
                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                    return;
                }

                player.sendMessage("§bA mensagem da sua loja foi alterada para '" + message + "§b' com sucesso.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                playerLoja.setAnnouncement(message);
                manager.getChat().remove(playerName);
                break;

            case LOCATION:
                if (plotAPI.getPlot(player.getLocation()) == null) {
                    player.sendMessage("§cErro ao setar a localização da loja, você precisa estar em um terreno.");
                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                    return;
                }

                Plot plot = plotAPI.getPlot(player.getLocation());

                if (!plot.getOwners().contains(player.getUniqueId())) {
                    player.sendMessage("§cErro ao setar a localização da loja, você não é o dono do terreno.");
                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                    return;
                }


                player.sendMessage("§bA localização da sua loja foi definida com sucesso.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                playerLoja.setLocation(player.getLocation());
                manager.getChat().remove(playerName);
                break;

            case VISIT:
                Loja targetLoja = manager.getLoja().get(message);

                if (targetLoja == null) {
                    player.sendMessage("§cO jogador §7" + message + "§c não possui uma loja no momento.");
                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                    manager.getChat().remove(playerName);
                    return;
                }

                if (targetLoja.getOwner().equals(playerName)) {
                    player.sendMessage("§cVocê não pode visitar a sua própria loja.");
                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                    manager.getChat().remove(playerName);
                    return;
                }

                if (targetLoja.getLocation() == null) {
                    player.sendMessage("§cA loja do jogador §7" + targetLoja.getOwner() + "§c está sem localização no momento atual.");
                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                    manager.getChat().remove(playerName);
                    return;
                }

                if (!targetLoja.getStatus()) {
                    player.sendMessage("§cA loja do jogador &7" + targetLoja.getOwner() + "§c não está aberta ao público no momento.");
                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                    manager.getChat().remove(playerName);
                    return;
                }

                player.sendMessage("§bVocê foi teleportado até a loja do jogador §7" + targetLoja.getOwner() + "§b.");
                player.teleport(targetLoja.getLocation());
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                targetLoja.setVisits(targetLoja.getVisits() + 1);
                manager.getChat().remove(playerName);
                break;
        }
    }
}
