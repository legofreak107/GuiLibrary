package me.legofreak107.guilibrary;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemStackBuilder {

    private static Method metaSetProfileMethod;
    private static Field metaProfileField;
    private ItemStack item;

    public ItemStackBuilder(Material material) {
        item = new ItemStack(material);
    }

    public ItemStackBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemStackBuilder addEnchant(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public static boolean same(ItemStack item, ItemStack item2) {
        if (item == null || item2 == null) return false;
        if (item.getType() != item2.getType()) return false;
        if (!item.hasItemMeta() && !item2.hasItemMeta()) return true;
        if (!item.hasItemMeta() || !item2.hasItemMeta()) return false;
        if (item.getItemMeta().hasDisplayName() && item2.getItemMeta().hasDisplayName()) {
            if (!item.getItemMeta().getDisplayName().equals(item2.getItemMeta().getDisplayName())) return false;
        } else if (item.getItemMeta().hasDisplayName() || item2.getItemMeta().hasDisplayName()) return false;
        return true;
    }

    public static void setAmount(ItemStack item, int amount) {
        item.setAmount(amount);
        return;
    }

    public ItemStackBuilder setColor(int r, int g, int b) {
        if (!(item.getItemMeta() instanceof LeatherArmorMeta)) return this;
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(r, g, b));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder setName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder setModelData(int data) {
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(data);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder addLore(Component line) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        if (meta.lore() != null) {
            lore = meta.lore();
        }
        lore.add(line);
        meta.lore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder addLore(String line) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        if (meta.lore() != null) {
            lore = meta.lore();
        }
        lore.add(Component.text(line));
        meta.lore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder addLores(List<String> lines) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        if (meta.lore() != null) {
            lore = meta.lore();
        }
        for (String line : lines) {
            lore.add(Component.text(line));
        }
        meta.lore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return item;
    }

    public ItemStackBuilder setSkullOwner(UUID uid) {
        try {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(uid));
            item.setItemMeta(meta);
            return this;
        } catch (Exception e) {
            return this;
        }
    }

}
