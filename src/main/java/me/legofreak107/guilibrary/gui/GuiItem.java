package me.legofreak107.guilibrary.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class GuiItem {

    private ItemStack itemStack;
    private Consumer<InventoryClickEvent> clickHandler;

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Consumer<InventoryClickEvent> getClickHandler() {
        return clickHandler;
    }

    public GuiItem(ItemStack itemStack, Consumer<InventoryClickEvent> clickHandler) {
        this.itemStack = itemStack;
        this.clickHandler = clickHandler;
    }
}
