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

public class Fireball implements CustomItem{

    private final ItemStack fireball;
    private final ItemStack price;
    private final BlockDefensePlugin plugin;

    public Fireball(BlockDefensePlugin plugin) {
        this.plugin = plugin;

        fireball = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta fireballMeta = fireball.getItemMeta();
        Component fireballName = Component.text("Fireball").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
        fireballMeta.displayName(fireballName);
        fireball.setItemMeta(fireballMeta);

        price = new ItemStack(Material.COPPER_INGOT, 16);
    }

    public ItemStack getItemPrice() {
        return price;
    }

    public ItemStack getItem() {
        return fireball;
    }

    public void onRightClick(PlayerInteractEvent event, ItemStack item) {
        Player player = event.getPlayer();

        event.setCancelled(true);
        item.setAmount(item.getAmount() - 1);

        org.bukkit.entity.Fireball fireballEntity = player.launchProjectile(org.bukkit.entity.Fireball.class);
        fireballEntity.setIsIncendiary(true);
        fireballEntity.setYield(2.0f);
    }

    public boolean areItemsEqual(ItemStack other) {
        return other.getType().equals(fireball.getType()) && other.getItemMeta().equals(fireball.getItemMeta());
    }
}
