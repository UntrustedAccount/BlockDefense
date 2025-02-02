package me.untrustedaccount;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

public class ScoreboardTimer extends BukkitRunnable {
    private int time;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private BukkitTask task;

    public ScoreboardTimer() {
        this.time = 0;

        ScoreboardManager manager = Bukkit.getScoreboardManager();

        this.scoreboard = manager.getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("timer", Criteria.DUMMY, Component.text("Timer").color(NamedTextColor.GOLD));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    public void run() {
        updateScoreboard();
        this.time++;
    }

    public void start(JavaPlugin plugin) {
        if (this.task != null) {
            return;
        }
        this.task = this.runTaskTimer(plugin, 0, 20);
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    private void updateScoreboard() {
        this.scoreboard.getEntries().forEach(this.scoreboard::resetScores);

        Score timerScore = this.objective.getScore(formatTime(this.time));
        timerScore.setScore(1);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(this.scoreboard);
        }
    }

    private String formatTime(int time) {
        int min = time/60;
        int sec = time%60;
        return String.format("%02d:%02d", min, sec);
    }

}
