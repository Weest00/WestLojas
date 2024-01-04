package br.com.west.lojas.menus;

import br.com.west.lojas.Main;
import br.com.west.lojas.enums.ChatType;
import br.com.west.lojas.managers.LojaManager;
import br.com.west.lojas.models.Loja;
import br.com.west.lojas.models.PlayerMenuUtility;
import br.com.west.lojas.utils.ItemBuilder;
import br.com.west.lojas.utils.PaginatedMenu;
import br.com.west.lojas.utils.SkullFactory;
import net.minecraft.server.v1_8_R3.ItemStack;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import java.util.ArrayList;

public class LojasMenu extends PaginatedMenu {
    public LojasMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "§8Lojas do Servidor";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        LojaManager manager = Main.getInstance().getLojaManager();
        ArrayList<Loja> lojas = manager.getLojas();
        int slot = event.getSlot();

        ItemStack nmsItem = CraftItemStack.asNMSCopy(event.getCurrentItem());
        if (nmsItem.hasTag() && nmsItem.getTag().hasKey("owner")) {
            String owner = nmsItem.getTag().getString("owner");
            Loja targetLoja = manager.getLoja(owner);

            if (targetLoja.getLocation() == null) {
                player.sendMessage("§cA loja do jogador §7" + targetLoja.getOwner() + "§c ainda não foi definida.");
                player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                return;
            }

            if (targetLoja.getOwner().equals(playerName)) {
                player.sendMessage("§cVocê não pode visitar a sua própria loja.");
                player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                return;
            }

            if (!targetLoja.getStatus()) {
                player.sendMessage("§cA loja do jogador §7" + targetLoja.getOwner() + "§c está fechada no momento.");
                player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                return;
            }

            player.sendMessage("§bVocê foi teleportado até a loja do jogador §7" + owner + "§b.");
            player.closeInventory();
            player.teleport(targetLoja.getLocation());
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
            targetLoja.setVisits(targetLoja.getVisits() + 1);
        }


        switch (slot) {
            case 48:
                if (page == 0) {
                    player.sendMessage("§cVocê já está na primeira página.");
                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                } else {
                    page = page - 1;
                    super.open();
                }
                break;

            case 4:
                if (manager.getLoja(playerName) == null) {
                    if (!Main.getInstance().getEconomy().has(player, Main.getInstance().getLojaPrice())) {
                        player.sendMessage("§cVocê não possui §2$§f" + Main.getInstance().format(Main.getInstance().getLojaPrice()) + "§c de coins para criar a loja.");
                        player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                        return;
                    }

                    player.sendMessage("§aParabéns! Sua loja foi criada com sucesso.");
                    Main.getInstance().getEconomy().withdrawPlayer(player, Main.getInstance().getLojaPrice());
                    Loja loja = new Loja(playerName, "§bVenha comprar diamantes!", null, 0, false);
                    Main.getInstance().getLojaManager().getActivePlayers().add(playerName);
                    manager.getLoja().put(playerName, loja);
                    manager.getLojas().add(loja);
                }
                new PlayerLojaMenu(manager.getPlayerMenuUtility(playerName)).open();
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                break;

            case 49:
                player.sendMessage("§aDigite o nome da loja do jogador. \npara cancelar a ação digite §7§ncancelar" + "§a.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                player.closeInventory();
                manager.getChat().put(playerName, ChatType.VISIT);
                break;

            case 50:
                if (!((index + 1) >= lojas.size())) {
                    page = page + 1;
                    super.open();
                } else {
                    player.sendMessage("§cVocê já está na ultima página.");
                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 1F);
                }
                break;
        }
    }

    @Override
    public void setMenuItems() {
        LojaManager manager = Main.getInstance().getLojaManager();
        Loja playerLoja = manager.getLoja(playerMenuUtility.getViewer());
        addMenuBorder();
        ArrayList<Loja> lojas = manager.getLojas();

        if (lojas != null && !lojas.isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= lojas.size()) break;
                if (lojas.get(index) != null) {
                    inventory.addItem(SkullFactory.set(lojas.get(index)));
                }
            }
        }


        if (playerLoja == null) {
            inventory.setItem(4, new ItemBuilder(Material.GOLD_INGOT).name("§cVocê não possui loja...").lore("", " §7Preço de criação: §2$§f" + Main.getInstance().format(Main.getInstance().getLojaPrice()), "", "§7Clique para criar sua loja.").build());
        } else {
            inventory.setItem(4, new ItemBuilder(Material.SKULL_ITEM).durability(3).owner(playerLoja.getOwner()).name("§bSua loja").lore("§7Clique para gerenciar sua loja.").build());
        }

        inventory.setItem(49, new ItemBuilder(Material.BOOK_AND_QUILL)
                .name("§bVisitar Loja")
                .lore("§7Clique para visitar a loja de outro jogador.")
                .build());
    }
}
