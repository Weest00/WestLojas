package br.com.west.lojas.commands;

import br.com.west.lojas.Main;
import br.com.west.lojas.managers.LojaManager;
import br.com.west.lojas.menus.LojasMenu;
import br.com.west.lojas.models.Loja;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LojasCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String lbl, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        String playerName = player.getName();
        LojaManager manager = Main.getInstance().getLojaManager();


        if (args.length == 0) {
                new LojasMenu(manager.getPlayerMenuUtility(playerName)).open();
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                return true;

            }

            if (args.length > 1) {
                player.sendMessage("§cErro! Use /lojas <jogador>.");
                player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                return true;
            }

            Loja targetLoja = manager.getLoja(args[0]);

            if (targetLoja == null) {
                player.sendMessage("§cO jogador §7" + args[0] + "§c não possui uma loja no momento.");
                player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                return true;
            }

            if (targetLoja.getOwner().equals(playerName)) {
                player.sendMessage("§cVocê não pode visitar a você mesmo.");
                player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                return true;
            }

            if (targetLoja.getLocation() == null) {
                player.sendMessage("§cA loja do jogador §7" + targetLoja.getOwner() + "§c não possui localização.");
                player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                return true;
            }

            if (!targetLoja.getStatus()) {
                player.sendMessage("§cA loja do jogador §7" + targetLoja.getOwner() + "§c está fechada no momento.");
                player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                return true;
            }

            player.sendMessage("§bVocê foi teleportado para a loja do jogador §7" + targetLoja.getOwner() + "§b.");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
            player.teleport(targetLoja.getLocation());
            targetLoja.setVisits(targetLoja.getVisits() + 1);


            return false;
        }
    }
