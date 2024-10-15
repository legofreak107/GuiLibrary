package me.legofreak107.guilibrary.listeners;

import me.legofreak107.guilibrary.GuiLibrary;
import me.legofreak107.guilibrary.gui.GuiItem;
import me.legofreak107.guilibrary.gui.GuiMenu;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (!GuiLibrary.menusOpen.containsKey(player)) return;
        GuiMenu open = GuiLibrary.menusOpen.get(player);
        if (open.getCloseHandler() == null) return;
        open.getCloseHandler().accept(e);
        GuiLibrary.menusOpen.remove(player);
    }

}
