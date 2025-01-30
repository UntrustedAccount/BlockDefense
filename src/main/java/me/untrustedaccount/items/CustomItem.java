package me.untrustedaccount.items;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface CustomItem {
    ItemStack getItemPrice();
    ItemStack getItem();
    void onRightClick(PlayerInteractEvent event, ItemStack item);
    boolean areItemsEqual(ItemStack other);
}