package me.legofreak107.sx.sxcore.libraries.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuiItem {

    private ItemStack itemStack;
    private GuiItemCallback callback;

}
