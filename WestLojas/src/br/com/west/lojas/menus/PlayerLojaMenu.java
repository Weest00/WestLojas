package br.com.west.lojas.menus;

import br.com.west.lojas.Main;
import br.com.west.lojas.enums.ChatType;
import br.com.west.lojas.managers.LojaManager;
import br.com.west.lojas.models.Loja;
import br.com.west.lojas.models.PlayerMenuUtility;
import br.com.west.lojas.utils.ItemBuilder;
import br.com.west.lojas.utils.Menu;
import br.com.west.lojas.utils.SkullFactory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerLojaMenu extends Menu {
    public PlayerLojaMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "§8Configurar Loja (" + playerMenuUtility.getViewer() + "§8)";
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        int slot = event.getSlot();
        LojaManager manager = Main.getInstance().getLojaManager();
        Loja playerLoja = manager.getLoja(playerName);

        switch (slot) {
            case 10:
                if (event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.SHIFT_LEFT) {
                    player.sendMessage("§aDigite qualquer coisa para confirmar a localização. \npara cancelar a ação digite §7§ncancelar" + "§a.");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                    player.closeInventory();
                    manager.getChat().put(playerName, ChatType.LOCATION);
                    return;
                }

                if (playerLoja.getLocation() == null) {
                    player.sendMessage("§cA localização da sua loja ainda não foi setada.");
                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                    return;
                }

                player.sendMessage("§aVocê foi teleportado até a sua loja.");
                player.closeInventory();
                player.teleport(playerLoja.getLocation());
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                break;

            case 11:
                player.sendMessage("§aDigite o anuncio da sua loja. \npara cancelar a ação digite §7§ncancelar" + "§a.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                player.closeInventory();
                manager.getChat().put(playerName, ChatType.ANNOUNCEMENT);
                break;
            case 14:
                if (playerLoja.getStatus()) {
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                    playerLoja.setStatus(false);
                    new PlayerLojaMenu(manager.getPlayerMenuUtility(playerName)).open();
                    return;
                }

                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                playerLoja.setStatus(true);
                new PlayerLojaMenu(manager.getPlayerMenuUtility(playerName)).open();
                break;

            case 15:
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                new PlayerLojaMenu(manager.getPlayerMenuUtility(playerName)).open();
                break;

            case 16:
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                new TopVisitsMenu(manager.getPlayerMenuUtility(playerName)).open();
                break;

            case 31:
                new LojasMenu(manager.getPlayerMenuUtility(playerName)).open();
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                break;

        }
    }

    @Override
    public void setMenuItems() {
        String playerName = playerMenuUtility.getViewer();
        LojaManager manager = Main.getInstance().getLojaManager();
        Loja playerLoja = manager.getLoja(playerName);

        inventory.setItem(10, new ItemBuilder(Material.DARK_OAK_DOOR_ITEM)
                .name("§bLocalização")
                .lore("§7Clique para ir até a sua loja.", "", "§7Clique com shift para redefinir a localização.")
                .build());

        inventory.setItem(11, new ItemBuilder(Material.BOOK_AND_QUILL)
                .name("§bAnúncio")
                .lore("§7Defina uma mensagem para que os outros jogadores", "§7possam saber do que se trata sua loja.", "", "§7Anúncio atual: " + playerLoja.getAnnouncement(), "", "§7Clique para alterar a mensagem.")
                .build());


        if (playerLoja.getStatus()) {
            inventory.setItem(14, SkullFactory.set("§cFechar Loja", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQ4ZDdkMWUwM2UxYWYxNDViMDEyNWFiODQxMjg1NjcyYjQyMTI2NWRhMmFiOTE1MDE1ZjkwNTg0MzhiYTJkOCJ9fX0=",
                    "§7Clique para fechar sua loja."));
        } else {
            inventory.setItem(14, SkullFactory.set("§aAbrir Loja", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=",
                    "§7Clique para abrir sua loja ao público."));
        }

        inventory.setItem(15, new ItemBuilder(Material.SKULL_ITEM)
                .durability(3).owner(playerLoja.getOwner())
                .name("§bInformações da sua loja")
                .lore("§7Dono: §f" + playerLoja.getOwner(), "§7Aberta: " + manager.getUserFormattedStatus(playerName), "§7Anuncio: " + playerLoja.getAnnouncement(), "§7Visitas: §f" + playerLoja.getVisits(), "", "§7Clique para atualizar as informações.")
                .build());

        inventory.setItem(16, new ItemBuilder(Material.GOLD_INGOT).name("§bRanking de Lojas").lore("§7Clique para visualizar as lojas mais visitadas.").build());

        inventory.setItem(31, new ItemBuilder(Material.ARROW).name("§bMenu").lore("§7Clique para voltar ao menu de lojas.").build());

    }

}
