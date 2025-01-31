package me.untrustedaccount;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;


public class CustomPlayer {

    public enum Team {
        ATTACKING {
            @Override
            public String toString() {
                return "attacker";
            }
        },
        DEFENDING {
            @Override
            public String toString() {
                return "defender";
            }
        },
        NONE {
            @Override
            public String toString() {
                return "none";
            }
        }
    }

    public enum Chat {
        TEAM,
        ALL
    }

    public Location spawnPoint;

    private final UUID playerUUID;
    private Team team = Team.NONE;
    private Chat chat = Chat.ALL;
    private boolean hasClaimedKit;
    private boolean hasMovementCooldown;
    private final BlockDefensePlugin plugin;

    public CustomPlayer(BlockDefensePlugin plugin, Player player) {
        this.plugin = plugin;
        this.playerUUID = player.getUniqueId();
        this.spawnPoint = player.getWorld().getSpawnLocation();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public void setAttacker() {
        this.team = Team.ATTACKING;
    }

    public void setDefender() {
        this.team = Team.DEFENDING;
    }

    public void setNone() {
        this.team = Team.NONE;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Chat getChat() {
        return this.chat;
    }

    public void setClaimedKit(boolean hasClaimedKit) {
        this.hasClaimedKit = hasClaimedKit;
    }

    public boolean getClaimedKit() {
        return this.hasClaimedKit;
    }

    public void setMovementCooldown(boolean hasMovementCooldown) {
        this.hasMovementCooldown = hasMovementCooldown;
    }

    public boolean getMovementCooldown() {
        return this.hasMovementCooldown;
    }

    public boolean isAttacker() {
        return team == Team.ATTACKING;
    }

    public boolean isDefender() {
        return team == Team.DEFENDING;
    }

    public void resetPlayer() {
        setMovementCooldown(false);
        setClaimedKit(false);

        Player player = getPlayer();
        player.teleport(spawnPoint);
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();
    }

    public void setSpawnPoint(Location newSpawnPoint) {
        spawnPoint = newSpawnPoint;
        Player player = getPlayer();
        player.setRespawnLocation(newSpawnPoint, true);
    }

    public void updatePlayer() {
        setName();
    }

    public void setName() {
        if (isDefender()) {
            setDefendingTeamName();
            return;
        }

        if (isAttacker()) {
            setAttackingTeamName();
            return;
        }

        resetDisplayName();
    }

    private void resetDisplayName() {
        Player player = getPlayer();
        Component name = Component.text(player.getName()).color(NamedTextColor.WHITE);
        player.displayName(name);
        player.playerListName(name);
    }

    private void setAttackingTeamName() {
        Player player = getPlayer();
        Component redName = Component.text("[Attacker] " + player.getName()).color(NamedTextColor.RED);
        player.displayName(redName);
        player.playerListName(redName);
    }

    private void setDefendingTeamName() {
        Player player = getPlayer();
        Component blueName = Component.text("[Defender] " + player.getName()).color(NamedTextColor.BLUE);
        player.displayName(blueName);
        player.playerListName(blueName);
    }
}
