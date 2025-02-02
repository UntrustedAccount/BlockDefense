package me.untrustedaccount;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.untrustedaccount.commands.*;
import me.untrustedaccount.items.*;
import me.untrustedaccount.items.Fireball;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class BlockDefensePlugin extends JavaPlugin implements Listener {

    public final Material goalMaterial = Material.ANCIENT_DEBRIS;

    public final ArrayList<CustomPlayer> players = new ArrayList<>();

    public final ArrayList<CustomItem> customItems = new ArrayList<>(Arrays.asList(new Fireball(this), new Alarm(this), new Compass(this), new PointedDripstone(this)));
    public final CustomItem compass = customItems.get(2);

    public boolean isGameActive;
    public Block goalBlock;
    public final ArrayList<Entity> spawnedEntities = new ArrayList<>();
    public boolean isAlarmActive = false;

    public ScoreboardTimer timer;

    @Override
    public void onEnable() {
        getLogger().info("Plugin starting...");
        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("kit")).setExecutor(new KitCommand(this));
        Objects.requireNonNull(getCommand("team")).setExecutor(new TeamCommand(this));
        Objects.requireNonNull(getCommand("start")).setExecutor(new StartCommand(this));
        Objects.requireNonNull(getCommand("store")).setExecutor(new StoreCommand(this));
        Objects.requireNonNull(getCommand("chat")).setExecutor(new ChatCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin stopping...");
    }

    public CustomPlayer getCustomPlayer(Player player) {
        if (player == null) return null;

        for (CustomPlayer customPlayer : players) {
            Player p = customPlayer.getPlayer();
            if (p != null && p.isOnline() && player.getUniqueId().equals(customPlayer.getPlayerUUID()))
                return customPlayer;
        }

        CustomPlayer newCustomPlayer = new CustomPlayer(this, player);
        players.add(newCustomPlayer);
        return newCustomPlayer;
    }

    public ArrayList<CustomPlayer> getDefenders() {
        ArrayList<CustomPlayer> defenders = new ArrayList<>();
        for (CustomPlayer customPlayer : players) {
            if (customPlayer.isDefender()) {
                defenders.add(customPlayer);
            }
        }
        return defenders;
    }

    public ArrayList<CustomPlayer> getAttackers() {
        ArrayList<CustomPlayer> attackers = new ArrayList<>();
        for (CustomPlayer customPlayer : players) {
            if (customPlayer.isAttacker()) {
                attackers.add(customPlayer);
            }
        }
        return attackers;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CustomPlayer customPlayer = getCustomPlayer(player);
        customPlayer.setName();
    }

    @EventHandler
    public void onItemRightClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!action.isRightClick()) {
            return;
        }

        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        for (CustomItem customItem : customItems) {
            if (!customItem.areItemsEqual(item)) continue;

            customItem.onRightClick(event, item);
            return;
        }
    }

    @EventHandler
    public void onPlayerTripAlarm(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        CustomPlayer customPlayer = getCustomPlayer(player);
        if (goalBlock == null) return;
        if (!isAlarmActive) return;
        if (player.getLocation().distance(goalBlock.getLocation()) >= Constants.alarmActivationDistance) return;
        if (!customPlayer.isAttacker()) return;

        // Trip alarm
        broadcastAlarm();
        isAlarmActive = false;
    }

    public void broadcastAlarm() {
        for (CustomPlayer customPlayer : getDefenders()) {
            Player player = customPlayer.getPlayer();
            player.sendTitlePart(TitlePart.TITLE, Component.text("Alarm triggered!").color(NamedTextColor.BLUE));
            player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onDestroyGoalBlock(BlockBreakEvent event) {
        if (goalBlock == null) return;

        Location location = event.getBlock().getLocation();
        if (!location.equals(goalBlock.getLocation())) return;

        Player player = event.getPlayer();
        CustomPlayer customPlayer = getCustomPlayer(player);
        if (!customPlayer.isAttacker()) {
            event.setCancelled(true);
            player.sendMessage("You cannot break the goal block.");
            return;
        }

        event.setDropItems(false);
        goalBlock = null;
        isGameActive = false;
        timer.stop();

        // The attacking team has broken the block and won
        broadcastWin();
    }

    public void broadcastWin() {
        for (CustomPlayer customPlayer : players) {
            Player player = customPlayer.getPlayer();
            player.sendTitlePart(TitlePart.TITLE, Component.text("The attackers have won!").color(NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onPlayerMoveDuringCountdown(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        CustomPlayer customPlayer = getCustomPlayer(player);

        if (!customPlayer.getMovementCooldown()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        CustomPlayer customPlayer = getCustomPlayer(player);

        if (!customPlayer.getMovementCooldown()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        CustomPlayer customPlayer = getCustomPlayer(player);

        if (player.getRespawnLocation() == null) {
            event.setRespawnLocation(customPlayer.getSpawnPoint());
        }

        player.setGameMode(GameMode.SPECTATOR);
        customPlayer.setMovementCooldown(true);

        new BukkitRunnable() {
            int countdown = Constants.respawnTime; // Set the countdown duration in seconds

            @Override
            public void run() {
                if (countdown > 0) {
                    player.sendTitlePart(TitlePart.TITLE, Component.text("Respawning in " + countdown).color(NamedTextColor.GOLD));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                    countdown--;
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                    customPlayer.setMovementCooldown(false);
                    cancel();
                }
            }
        }.runTaskTimer(this, 0, 20);
    }

    private TextColor getColor(CustomPlayer sender) {
        TextColor color = NamedTextColor.WHITE;
        if (sender.isAttacker()) {
            color = NamedTextColor.RED;
        }
        if (sender.isDefender()) {
            color = NamedTextColor.BLUE;
        }
        return color;
    }

    private Component getTeamMessage (CustomPlayer sender, Component message) {
        Player player = sender.getPlayer();
        TextColor color = getColor(sender);
        Component prefix = Component.text("[TEAM] ").color(color);
        Component senderName = Component.text(player.getName()).color(color);
        Component divider = Component.text(": ").color(NamedTextColor.WHITE);
        return prefix.append(senderName).append(divider).append(message);
    }

    private Component getAllMessage(CustomPlayer sender, Component message) {
        Player player = sender.getPlayer();
        TextColor color = getColor(sender);
        Component prefix = Component.text("[ALL] ").color(NamedTextColor.WHITE);
        Component senderName = Component.text(player.getName()).color(color);
        Component divider = Component.text(": ").color(NamedTextColor.WHITE);
        return prefix.append(senderName).append(divider).append(message);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        CustomPlayer customPlayer = getCustomPlayer(player);
        Component message = event.message().color(NamedTextColor.WHITE);

        event.setCancelled(true);

        Component fullMessage = getTeamMessage(customPlayer, message);
        if (customPlayer.getChat() == CustomPlayer.Chat.TEAM) {
            if (customPlayer.isAttacker()) {
                for (CustomPlayer customReceiver : getAttackers()) {
                    customReceiver.getPlayer().sendMessage(fullMessage);
                }
                return;
            }

            if (customPlayer.isDefender()) {
                for (CustomPlayer customReceiver : getDefenders()) {
                    customReceiver.getPlayer().sendMessage(fullMessage);
                }
                return;
            }
        }

        fullMessage = getAllMessage(customPlayer, message);
        for (CustomPlayer customReceiver : players) {
            customReceiver.getPlayer().sendMessage(fullMessage);
        }
        return;
    }
}
