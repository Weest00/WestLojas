package br.com.west.lojas.menus;

import br.com.west.lojas.Main;
import br.com.west.lojas.managers.LojaManager;
import br.com.west.lojas.models.PlayerMenuUtility;
import br.com.west.lojas.utils.ItemBuilder;
import br.com.west.lojas.utils.Menu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TopVisitsMenu extends Menu {
    public TopVisitsMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "§8Ranking de Lojas (Visitas)";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == 40) {
            new PlayerLojaMenu(Main.getInstance().getLojaManager().getPlayerMenuUtility(player.getName())).open();
            player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1F, 1F);
        }
    }

    @Override
    public void setMenuItems() {
        LojaManager manager = Main.getInstance().getLojaManager();
        int[] slots = {9, 10, 11, 12, 13, 14, 15, 16, 21, 22, 23};
        int count = 1;

        if (manager.getTopVisits().isEmpty()) {
            inventory.setItem(22, new ItemBuilder(Material.WEB).name("§cNenhum jogador disponível...").build());
        } else {
            for (String top : manager.getTopVisits()) {
                inventory.setItem(slots[count], new ItemBuilder(Material.SKULL_ITEM).durability(3).owner(top).name("§b" + count + "§bº: §7" + top).lore("", "§7Este jogador já recebeu §fx" + manager.getLoja(top).getVisits() + "§7 visitas em sua loja.").build());
                count++;

            }
        }

        inventory.setItem(40, new ItemBuilder(Material.ARROW).name("§bMenu").lore("§7Clique para voltar ao menu.").build());

    }

}
