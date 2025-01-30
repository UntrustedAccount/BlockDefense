package me.untrustedaccount.commands;

import me.untrustedaccount.BlockDefensePlugin;
import me.untrustedaccount.CustomPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeamCommand implements CommandExecutor, TabCompleter {

    private final BlockDefensePlugin plugin;

    public TeamCommand(BlockDefensePlugin plugin) {
        this.plugin = plugin;
    }

    private void team(CustomPlayer customPlayer, String[] args) {

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("defender")) {
                addDefender(customPlayer);
            }
            else if (args[0].equalsIgnoreCase("attacker")) {
                addAttacker(customPlayer);
            }
            else if (args[0].equalsIgnoreCase("clear")) {
                clearTeam(customPlayer);
            }
            customPlayer.updatePlayer();
        }

        customPlayer.getPlayer().sendMessage("On team " + customPlayer.getTeam());
    }

    private void addAttacker(CustomPlayer customPlayer) {

        Player player = customPlayer.getPlayer();

        if (customPlayer.isAttacker()) {
            player.sendMessage("You are already on the attackers.");
            return;
        }

        if (customPlayer.isDefender()) {
            player.sendMessage("You are already on the defenders.");
            return;
        }

        customPlayer.setAttacker();
        return;
    }

    private void addDefender(CustomPlayer customPlayer) {

        Player player = customPlayer.getPlayer();

        if (customPlayer.isAttacker()) {
            player.sendMessage("You are already on the attackers.");
            return;
        }

        if (customPlayer.isDefender()) {
            player.sendMessage("You are already on the defenders.");
            return;
        }

        customPlayer.setDefender();
        return;
    }

    private void clearTeam(CustomPlayer customPlayer) {
        customPlayer.setNone();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Only players can execute this command.");
            return true;
        }

        CustomPlayer customPlayer = plugin.getCustomPlayer(player);

        team(customPlayer, strings);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of("attacker", "defender", "clear");
    }
}
