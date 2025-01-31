package me.untrustedaccount.items;

import me.untrustedaccount.BlockDefensePlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PointedDripstone implements CustomItem{

    private final ItemStack dripstone;
    private final ItemStack price;
    private final BlockDefensePlugin plugin;

    public PointedDripstone(BlockDefensePlugin plugin) {
        this.plugin = plugin;

        dripstone = new ItemStack(Material.POINTED_DRIPSTONE);
        price = new ItemStack(Material.COPPER_INGOT, 2);
    }

    public ItemStack getItemPrice() {
        return price;
    }

    public ItemStack getItem() {
        return dripstone;
    }

    public void onRightClick(PlayerInteractEvent event, ItemStack item) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Block block = event.getClickedBlock();
        if (block == null) return;
        Location lowerBlockLocation = block.getLocation().add(0, -1, 0);
        Block lowerBlock = world.getBlockAt(lowerBlockLocation);
        if (!lowerBlock.getType().isAir()) {
            return;
        }

        event.setCancelled(true);
        item.setAmount(item.getAmount() - 1);

        BlockData blockData = Material.POINTED_DRIPSTONE.createBlockData();

        if (blockData instanceof org.bukkit.block.data.type.PointedDripstone pointedDripstone) {
            pointedDripstone.setVerticalDirection(BlockFace.DOWN);
        }

        FallingBlock fallingBlock = world.spawnFallingBlock(lowerBlockLocation.add(0.5, 0, 0.5), blockData);
        fallingBlock.setDropItem(true);
        fallingBlock.setHurtEntities(true);
        fallingBlock.setDamagePerBlock(5.0f);
    }

    public boolean areItemsEqual(ItemStack other) {
        return other.getType().equals(dripstone.getType()) && other.getItemMeta().equals(dripstone.getItemMeta());
    }
}
