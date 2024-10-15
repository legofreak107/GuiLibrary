package me.legofreak107.guilibrary;

import me.legofreak107.guilibrary.gui.GuiMenu;
import me.legofreak107.guilibrary.listeners.InventoryClickListener;
import me.legofreak107.guilibrary.listeners.InventoryCloseListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class GuiLibrary {

    public static HashMap<Player, GuiMenu> menusOpen = new HashMap<>();

    public static void init(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), plugin);
    }

}
