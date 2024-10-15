package me.legofreak107.sx.sxcore.libraries.utils.itemstack;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.changeme.nbtapi.NBT;
import lombok.Getter;
import me.legofreak107.sx.sxcore.SXCore;
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

    @Getter
    private static final boolean ANTIDUPE = false;

    public ItemStackBuilder(Material material) {
        item = new ItemStack(material);

        if (ItemStackBuilder.isANTIDUPE())
            setCustomString("antidupeids", UUID.randomUUID().toString());
    }

    public ItemStackBuilder(ItemStack item) {
        this.item = item;
    }

    public static ItemStack makeUndupable(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(SXCore.getInstance(), "antidupeids");
        container.set(key, PersistentDataType.STRING, UUID.randomUUID().toString());
        itemStack.setItemMeta(meta);
        return itemStack;
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

    private static GameProfile makeProfile(String b64) {
        // random uuid based on the b64 string
        UUID id = new UUID(
                b64.substring(b64.length() -  20).hashCode(),
                b64.substring(b64.length() -  10).hashCode()
        );
        GameProfile profile = new GameProfile(id, "Player");
        profile.getProperties().put("textures", new Property("textures", b64));
        return profile;
    }

    private static void mutateItemMeta(SkullMeta meta, String b64) {
        try {
            if (metaSetProfileMethod == null) {
                metaSetProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                metaSetProfileMethod.setAccessible(true);
            }
            metaSetProfileMethod.invoke(meta, makeProfile(b64));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            // if in an older API where there is no setProfile method,
            // we set the profile field directly.
            try {
                if (metaProfileField == null) {
                    metaProfileField = meta.getClass().getDeclaredField("profile");
                    metaProfileField.setAccessible(true);
                }
                metaProfileField.set(meta, makeProfile(b64));

            } catch (NoSuchFieldException | IllegalAccessException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public static void setAmount(ItemStack item, int amount) {
        if (!isANTIDUPE()) {
            item.setAmount(amount);
            return;
        }
        if (item.getAmount() < amount) {
            increaseAmount(item, amount -  item.getAmount());
        } else if (item.getAmount() > amount) {
            decreaseAmount(item, item.getAmount() -  amount);
        }
    }

    public static List<ItemStack> splitStack(ItemStack item, int toSplit) {
        ItemStack item1 = item.clone();
        ItemStack item2 = item.clone();
        ItemMeta meta1 = item.getItemMeta();
        ItemMeta meta2 = item.getItemMeta();
        PersistentDataContainer container = meta2.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(SXCore.getInstance(), "antidupeids");
        List<String> uids = List.of(container.get(key, PersistentDataType.STRING).split(","));
        List<String> newUids = new ArrayList<>();
        List<String> newUids2 = new ArrayList<>();
        for (int i = 0; i < item.getAmount(); i ++) {
            if (i < toSplit) {
                newUids.add(uids.get(i));
            } else {
                newUids2.add(uids.get(i));
            }
            meta1.getPersistentDataContainer().set(key, PersistentDataType.STRING, String.join(",", newUids));
            meta2.getPersistentDataContainer().set(key, PersistentDataType.STRING, String.join(",", newUids2));
        }
        item1.setItemMeta(meta1);
        item2.setItemMeta(meta2);
        item1.setAmount(newUids.size());
        item2.setAmount(newUids2.size());
        return List.of(item1, item2);
    }

    public static void addToStack(ItemStack toAddTo, ItemStack toAdd) {
        ItemMeta meta1 = toAddTo.getItemMeta();
        ItemMeta meta2 = toAdd.getItemMeta();
        PersistentDataContainer container1 = meta1.getPersistentDataContainer();
        PersistentDataContainer container2 = meta2.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(SXCore.getInstance(), "antidupeids");
        List<String> uids1 = new ArrayList<>(List.of(container1.get(key, PersistentDataType.STRING).split(",")));
        List<String> uids2 = List.of(container2.get(key, PersistentDataType.STRING).split(","));
        uids1.addAll(uids2);
        meta1.getPersistentDataContainer().set(key, PersistentDataType.STRING, String.join(",", uids1));
        toAddTo.setAmount(toAddTo.getAmount() + toAdd.getAmount());
        toAdd.setAmount(0);
        toAdd.setItemMeta(meta2);
        toAddTo.setItemMeta(meta1);
    }

    public static void decreaseAmount(ItemStack item, int amount) {
        if (!isANTIDUPE()) {
            item.setAmount(item.getAmount() -  amount);
            return;
        }
        for (int i = 0; i < amount; i ++) {
            item.setAmount(item.getAmount() -  1);
            if (item.getItemMeta() == null) return;
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(SXCore.getInstance(), "antidupeids");
            List<String> uids = List.of(container.get(key, PersistentDataType.STRING).split(","));
            uids.remove(uids.size() -  1);
            container.set(key, PersistentDataType.STRING, String.join(",", uids));
            item.setItemMeta(meta);
        }
    }

    public static void increaseAmount(ItemStack item, int amount) {
        if (!isANTIDUPE()) {
            item.setAmount(item.getAmount() + amount);
            return;
        }
        for (int i = 0; i < amount; i ++) {
            item.setAmount(item.getAmount() + 1);
            if (item.getItemMeta() == null) return;
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(SXCore.getInstance(), "antidupeids");
            container.set(key, PersistentDataType.STRING, container.get(key, PersistentDataType.STRING) + "," + UUID.randomUUID());
            item.setItemMeta(meta);
        }
    }

    public ItemStackBuilder setAmount(int amount) {
        if (!isANTIDUPE()) {
            item.setAmount(amount);
            return this;
        }
        item.setAmount(amount);
        if (item.getItemMeta() == null) return this;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(SXCore.getInstance(), "antidupeids");
        container.set(key, PersistentDataType.STRING, container.get(key, PersistentDataType.STRING) + "," + UUID.randomUUID());
        item.setItemMeta(meta);
        item.setAmount(item.getAmount());
        return this;
    }

    public ItemStackBuilder addGlow() {
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        return this;
    }

    public ItemStackBuilder setSkullSkin(String base) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        mutateItemMeta(meta, base);
        item.setItemMeta(meta);
        return this;
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

    public ItemStackBuilder addNbt(String nbt) {
        NBT.modify(item, nbt2 -> {
            nbt2.setString("mtcustom", nbt);
            nbt2.setString("nbt.mtcustom", nbt);
        });
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

    public ItemStackBuilder setCustomString(String key, String value) {
        if (item.getItemMeta() == null) return this;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(SXCore.getInstance(), key), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
        return this;
    }

}
