package me.untrustedaccount.commands;

import me.untrustedaccount.BlockDefensePlugin;
import me.untrustedaccount.CustomPlayer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KitCommand implements CommandExecutor, TabCompleter {

    private final BlockDefensePlugin plugin;

    public KitCommand(BlockDefensePlugin plugin) {
        this.plugin = plugin;
    }

    private void kit(Player player, String[] args) {
        CustomPlayer customPlayer = plugin.getCustomPlayer(player);
        if (customPlayer.isAttacker()) {
            giveAttackerKit(player);
            return;
        }

        if (customPlayer.getClaimedKit()) {
            player.sendMessage("You have already claimed a kit.");
            return;
        }

        if (customPlayer.isDefender()) {
            if (args.length == 1) {
                customPlayer.setClaimedKit(true);
                if (args[0].equalsIgnoreCase("miner")) {
                    giveMinerKit(player);
                    return;
                }
                if (args[0].equalsIgnoreCase("archer")) {
                    giveArcherKit(player);
                    return;
                }
                if (args[0].equalsIgnoreCase("knight")) {
                    giveKnightKit(player);
                    return;
                }
            }
            player.sendMessage("Specify either miner, archer, or knight");
            return;
        }

        player.sendMessage("Please first use /team to choose a team.");
        return;
    }

    private void giveKnightKit(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.setHelmet(new ItemStack(Material.IRON_HELMET, 1));
        playerInventory.setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
        playerInventory.setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
        playerInventory.setBoots(new ItemStack(Material.IRON_BOOTS, 1));
        playerInventory.addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
        playerInventory.setItemInOffHand(new ItemStack(Material.SHIELD, 1));
        playerInventory.addItem(plugin.compass.getItem());
        playerInventory.addItem(new ItemStack(Material.COOKED_BEEF, 16));
    }

    private void giveArcherKit(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
        playerInventory.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
        playerInventory.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
        playerInventory.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addEnchantment(Enchantment.POWER, 2);
        playerInventory.addItem(bow);
        playerInventory.addItem(plugin.compass.getItem());
        playerInventory.addItem(new ItemStack(Material.BREAD, 16));
        playerInventory.addItem(new ItemStack(Material.ARROW, 64));
    }

    private void giveMinerKit(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
        playerInventory.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
        playerInventory.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
        playerInventory.setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
        playerInventory.addItem(new ItemStack(Material.DIAMOND_PICKAXE, 1));
        playerInventory.addItem(new ItemStack(Material.DIAMOND_SHOVEL, 1));
        playerInventory.addItem(plugin.compass.getItem());
        playerInventory.addItem(new ItemStack(Material.BREAD, 16));
    }

    private void giveAttackerKit(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
        playerInventory.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
        playerInventory.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
        playerInventory.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
        playerInventory.addItem(new ItemStack(Material.STONE_AXE, 1));
        playerInventory.addItem(new ItemStack(Material.STONE_PICKAXE, 1));
        playerInventory.addItem(plugin.compass.getItem());
        playerInventory.addItem(new ItemStack(Material.BREAD, 16));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Only players can execute this command.");
            return true;
        }

        kit(player, strings);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length != 1) {
            return List.of();
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can autocomplete.");
            return List.of();
        }

        Player player = (Player) commandSender;
        CustomPlayer customPlayer = plugin.getCustomPlayer(player);

        if (customPlayer.isDefender()) {
            return List.of("miner", "archer", "knight");
        }

        return List.of();
    }
}
