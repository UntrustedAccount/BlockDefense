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

public class ChatCommand implements CommandExecutor, TabCompleter {

    private final BlockDefensePlugin plugin;

    public ChatCommand(BlockDefensePlugin plugin) {
        this.plugin = plugin;
    }

    private void chat(Player player, String[] args) {
        CustomPlayer customPlayer = plugin.getCustomPlayer(player);

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("team")) {
                customPlayer.setChat(CustomPlayer.Chat.TEAM);
            }
            else if (args[0].equalsIgnoreCase("all")) {
                customPlayer.setChat(CustomPlayer.Chat.ALL);
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Only players can execute this command.");
            return true;
        }

        chat(player, strings);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of("team", "all");
    }
}
