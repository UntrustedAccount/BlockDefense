package me.untrustedaccount.items;

import me.untrustedaccount.BlockDefensePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Alarm implements CustomItem{

    private final ItemStack alarm;
    private final ItemStack price;
    private final BlockDefensePlugin plugin;

    public Alarm(BlockDefensePlugin plugin) {
        this.plugin = plugin;
        alarm = new ItemStack(Material.REDSTONE_TORCH);
        ItemMeta alarmMeta = alarm.getItemMeta();
        Component alarmName = Component.text("Alarm").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false);
        alarmMeta.displayName(alarmName);
        alarm.setItemMeta(alarmMeta);

        price = new ItemStack(Material.COPPER_INGOT, 64);
    }

    public ItemStack getItemPrice() {
        return price;
    }

    public ItemStack getItem() {
        return alarm;
    }

    public void onRightClick(PlayerInteractEvent event, ItemStack item) {
        Player player = event.getPlayer();

        event.setCancelled(true);
        if (!plugin.isAlarmActive) {
            plugin.isAlarmActive = true;
            item.setAmount(item.getAmount() - 1);
            return;
        }
        Component message = Component.text("Alarm is already active.");
        player.sendActionBar(message);
    }

    public boolean areItemsEqual(ItemStack other) {
        return other.getType().equals(alarm.getType()) && other.getItemMeta().equals(alarm.getItemMeta());
    }
}
