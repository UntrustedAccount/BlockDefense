package me.untrustedaccount.commands;

import me.untrustedaccount.BlockDefensePlugin;
import me.untrustedaccount.Constants;
import me.untrustedaccount.CustomPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ResetCommand implements CommandExecutor {
    private final BlockDefensePlugin plugin;

    public ResetCommand(BlockDefensePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Only players can execute this command.");
            return true;
        }

        resetGame(player);
        return true;
    }

    private void resetGame(Player player) {
        resetWorld(player);
        resetBlock();
        resetEntities();
        resetDefenders(player);
        resetAttackers(player);
    }

    private void resetWorld(Player player) {
        World world = player.getWorld();
        world.setClearWeatherDuration((int) 10e9);
        // Set the time to dawn
        world.setTime(1000);
    }

    private void resetBlock() {
        if (plugin.goalBlock != null) {
            plugin.goalBlock.setType(Material.AIR);
        }
    }

    private void resetEntities() {
        for (Entity entity : plugin.spawnedEntities) {
            if (!entity.isDead()) {
                entity.remove();
            }
        }
    }

    private Location getValidSpawn(World world, int x, int z) {
        Location highestBlockLocation = world.getHighestBlockAt(x, z).getLocation();

        return highestBlockLocation.add(0, 1, 0);
    }

    private void resetDefenders(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();

        location.getBlock().setType(plugin.goalMaterial);
        plugin.goalBlock = location.getBlock();

        for (CustomPlayer customPlayer : plugin.getDefenders()) {
            customPlayer.setSpawnPoint(randomDefenderSpawn(world, location));
            customPlayer.resetPlayer();
        }

        plugin.isAlarmActive = false;
    }

    private Location randomDefenderSpawn(World world, Location goalLocation) {
        double offsetX = Constants.random.nextDouble(50.0, 100.0);
        double offsetZ = Constants.random.nextDouble(50.0, 100.0);
        if (Constants.random.nextBoolean()) offsetX *= -1;
        if (Constants.random.nextBoolean()) offsetZ *= -1;
        Location offsetLocation = goalLocation.add(offsetX, 0, offsetZ);
        return getValidSpawn(world, (int)offsetLocation.getX(), (int)offsetLocation.getZ());
    }

    private void resetAttackers(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();

        Vector offset = Constants.randomSpawnOffset().multiply(Constants.attackerDistance);
        Location offsetLocation = location.add(offset);
        Location attackingSpawn = getValidSpawn(world, (int) offsetLocation.getX(), (int) offsetLocation.getZ());

        for (CustomPlayer customPlayer : plugin.getAttackers()) {
            customPlayer.setSpawnPoint(attackingSpawn);
            customPlayer.resetPlayer();
        }
    }
}
