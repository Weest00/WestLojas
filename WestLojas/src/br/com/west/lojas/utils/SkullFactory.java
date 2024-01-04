package br.com.west.lojas.utils;

import br.com.west.lojas.Main;
import br.com.west.lojas.models.Loja;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

public class SkullFactory {

    public static ItemStack set(String title, String textures, String... lore) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(Arrays.asList(lore));
        try {
            Field field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", textures));
            field.set(meta, profile);
        } catch (Exception ignored) {
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack set(Loja loja) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("owner", loja.getOwner());
        nmsItem.setTag(tag);
        item = CraftItemStack.asBukkitCopy(nmsItem);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(loja.getOwner());
        meta.setDisplayName("§bLoja de " + loja.getOwner());
        meta.setLore(Arrays.asList("", " §7Aberta: " + Main.getInstance().getLojaManager().getUserFormattedStatus(loja.getOwner()), " §7Anuncio: " + loja.getAnnouncement(), " §7Visitas: §f" + loja.getVisits(), "", "§bClique para visitar a loja."));
        item.setItemMeta(meta);
        return item;
    }
}
