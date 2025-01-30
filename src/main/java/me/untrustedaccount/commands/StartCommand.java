package me.untrustedaccount.commands;

import me.untrustedaccount.BlockDefensePlugin;
import me.untrustedaccount.CustomPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class StartCommand implements CommandExecutor {

    private final BlockDefensePlugin plugin;

    public StartCommand(BlockDefensePlugin plugin) {
        this.plugin = plugin;
    }

    private void setCooldown(boolean cooldown) {
        for (CustomPlayer customPlayer : plugin.players) {
            customPlayer.setMovementCooldown(cooldown);
        }
    }

    private void sendCountdownTitle(String text, Sound sound) {
        for (CustomPlayer customPlayer : plugin.players) {
            Player player = customPlayer.getPlayer();
            player.sendTitlePart(TitlePart.TITLE, Component.text(text).color(NamedTextColor.GOLD));
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    private void startCountdown(int time) {
        setCooldown(true);

        new BukkitRunnable() {
            int countdown = time; // Set the countdown duration in seconds

            @Override
            public void run() {
                if (countdown > 0) {
                    sendCountdownTitle(String.valueOf(countdown), Sound.BLOCK_NOTE_BLOCK_CHIME);
                    countdown--;
                } else {
                    sendCountdownTitle("Start!", Sound.BLOCK_NOTE_BLOCK_PLING);
                    setCooldown(false);
                    cancel();
                }
            }
        }.runTaskTimer(this.plugin, 0, 20);
    }

    private void startGame() {
        startCountdown(5);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        startGame();
        return true;
    }
}
