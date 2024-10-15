package me.legofreak107.guilibrary.gui;

import me.legofreak107.guilibrary.GuiLibrary;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.function.Consumer;

public class GuiMenu {

    private Inventory inventory;
    private int[][] layout = {
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
    };

    private HashMap<Integer, GuiItem> itemMasks = new HashMap<>();
    private HashMap<Integer, GuiItem> linkedItems = new HashMap<>();
    private Consumer<InventoryCloseEvent> closeHandler;
    private boolean locked = false;

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public HashMap<Integer, GuiItem> getLinkedItems() {
        return linkedItems;
    }

    public Consumer<InventoryCloseEvent> getCloseHandler() {
        return closeHandler;
    }

    public void init(int[][] layout, HashMap<Integer, GuiItem> itemMasks, Component title, Consumer<InventoryCloseEvent> closeHandler) throws InvalidGuiLayoutException {
        this.layout = layout;
        this.itemMasks = itemMasks;
        this.closeHandler = closeHandler;
        inventory = Bukkit.createInventory(null, layout.length * 9, title);
        int rowNumber = 0;
        for (int[] row : layout) {
            int slotNumber = 0;
            if (row.length != 9) {
                throw new InvalidGuiLayoutException();
            }
            for (int value : row) {
                int slot = (rowNumber * 9) + slotNumber;
                inventory.setItem(slot, itemMasks.get(value).getItemStack());
                linkedItems.put(slot, itemMasks.get(value));
                slotNumber ++;
            }
            rowNumber ++;
        }
    }

    public void init(int[][] layout, HashMap<Integer, GuiItem> itemMasks, String title, Consumer<InventoryCloseEvent> closeHandler) throws InvalidGuiLayoutException {
        init(layout, itemMasks, Component.text(title), closeHandler);
    }

    public void open(Player player) {
        player.openInventory(inventory);
        GuiLibrary.menusOpen.put(player, this);
    }
}
