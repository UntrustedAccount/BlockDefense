package me.untrustedaccount.items;

import me.untrustedaccount.BlockDefensePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Compass implements CustomItem{

    private final ItemStack compass;
    private final ItemStack price;
    private final BlockDefensePlugin plugin;

    public Compass(BlockDefensePlugin plugin) {
        this.plugin = plugin;

        compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        Component compassName = Component.text("Compass").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false);
        compassMeta.displayName(compassName);
        compass.setItemMeta(compassMeta);

        price = null;
    }

    public ItemStack getItemPrice() {
        return price;
    }

    public ItemStack getItem() {
        return compass;
    }

    public void onRightClick(PlayerInteractEvent event, ItemStack item) {
        Player player = event.getPlayer();
        event.setCancelled(true);
        if (plugin.goalBlock == null) {
            Component message = Component.text("Location not found.");
            player.sendActionBar(message);
            return;
        }

        Location goalLocation = plugin.goalBlock.getLocation();

        player.setCompassTarget(goalLocation);
        Component message = Component.text(String.format("%d blocks away.", (int) player.getLocation().distance(goalLocation)));
        player.sendActionBar(message);
    }

    public boolean areItemsEqual(ItemStack other) {
        return other.getType().equals(compass.getType()) && other.getItemMeta().equals(compass.getItemMeta());
    }
}
