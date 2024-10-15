package me.legofreak107.sx.sxcore.libraries.inventory;

import lombok.Getter;
import lombok.Setter;
import me.legofreak107.sx.sxcore.SXCore;
import me.legofreak107.sx.sxcore.exceptions.InvalidGuiLayoutException;
import me.legofreak107.sx.sxcore.modules.usermodule.objects.SXUser;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

@Getter
@Setter
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

    private boolean locked = true;
    private String title;
    private HashMap<Integer, GuiItem> itemMasks = new HashMap<>();
    private HashMap<Integer, GuiItem> linkedItems = new HashMap<>();


    private GuiMenuCallback callback;

    public void init(int[][] layout, HashMap<Integer, GuiItem> itemMasks, Component title) {
        this.layout = layout;
        this.itemMasks = itemMasks;
        inventory = Bukkit.createInventory(null, layout.length * 9, title);
        int rowNumber = 0;
        for (int[] row : layout) {
            int slotNumber = 0;
            if (row.length != 9) {
                SXCore.getPluginLogger().severe(new InvalidGuiLayoutException().getMessage());
                continue;
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

    public void init(int[][] layout, HashMap<Integer, GuiItem> itemMasks, String title) {
        this.layout = layout;
        this.itemMasks = itemMasks;
        this.title = title;
        inventory = Bukkit.createInventory(null, layout.length * 9, Component.text(title));
        int rowNumber = 0;
        for (int[] row : layout) {
            int slotNumber = 0;
            if (row.length != 9) {
                SXCore.getPluginLogger().severe(new InvalidGuiLayoutException().getMessage());
                continue;
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

    public void open(GuiMenuCallback callback, SXUser player) {
        this.callback = callback;
        GuiMenu menu = this;
        try {
            new BukkitRunnable(){
                @Override
                public void run() {
                    if (player.getCurOpenGui() != null) {
                        player.getCurOpenGui().getCallback().cancel(player);
                        player.getPlayer().closeInventory();
                    }
                    player.setCurOpenGui(menu);
                    player.getPlayer().openInventory(inventory);
                }
            }.runTask(SXCore.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
