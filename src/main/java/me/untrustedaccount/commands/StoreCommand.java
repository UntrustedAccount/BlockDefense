package me.untrustedaccount.commands;

import me.untrustedaccount.BlockDefensePlugin;
import me.untrustedaccount.items.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StoreCommand implements CommandExecutor {
    private final BlockDefensePlugin plugin;

    public StoreCommand(BlockDefensePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Only players can execute this command.");
            return true;
        }

        store(player);
        return true;
    }

    private void store(Player player) {
        Merchant merchant = Bukkit.createMerchant(Component.text("Store").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));

        ArrayList<ItemStack> inputItems = new ArrayList<ItemStack>();
        ArrayList<ItemStack> outputItems = new ArrayList<ItemStack>();
        inputItems.add(new ItemStack(Material.COPPER_INGOT, 1));
        outputItems.add(new ItemStack(Material.REDSTONE, 16));

        inputItems.add(new ItemStack(Material.COPPER_INGOT, 4));
        outputItems.add(new ItemStack(Material.IRON_INGOT, 1));

        inputItems.add(new ItemStack(Material.COPPER_INGOT, 8));
        outputItems.add(new ItemStack(Material.GOLD_INGOT, 1));

        inputItems.add(new ItemStack(Material.COPPER_INGOT, 16));
        outputItems.add(new ItemStack(Material.TNT, 1));

        inputItems.add(new ItemStack(Material.COPPER_INGOT, 10));
        outputItems.add(new ItemStack(Material.WIND_CHARGE, 1));

        inputItems.add(new ItemStack(Material.COPPER_INGOT, 4));
        outputItems.add(new ItemStack(Material.COBWEB, 1));

        inputItems.add(new ItemStack(Material.COPPER_INGOT, 16));
        outputItems.add(new ItemStack(Material.POWDER_SNOW_BUCKET, 1));

        inputItems.add(new ItemStack(Material.COPPER_INGOT, 4));
        outputItems.add(new ItemStack(Material.OAK_LOG, 64));

        inputItems.add(new ItemStack(Material.COPPER_INGOT, 4));
        outputItems.add(new ItemStack(Material.STONE_BRICKS, 64));

        for (CustomItem customItem : plugin.customItems) {
            ItemStack itemPrice = customItem.getItemPrice();
            ItemStack item = customItem.getItem();
            if (itemPrice == null || item == null) continue;

            inputItems.add(itemPrice);
            outputItems.add(item);
        }

        ArrayList<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();

        for (int i=0; i<inputItems.size(); i++) {
            MerchantRecipe recipe = new MerchantRecipe(outputItems.get(i), (int) 10e6);
            recipe.addIngredient(inputItems.get(i));
            recipes.add(recipe);
        }

        merchant.setRecipes(recipes);
        player.openMerchant(merchant, true);
    }
}
