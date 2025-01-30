package me.untrustedaccount;

import org.bukkit.util.Vector;

import java.util.Random;

public class Constants {
    public static final int alarmActivationDistance = 50;
    public static final int respawnTime = 10;
    public static final int attackerDistance = 2000;
    public static final Vector[] spawnOffset =  {new Vector(1, 0, 0), new Vector(-1, 0, 0), new Vector(0, 0, 1), new Vector(0, 0, -1)};
    public static final Random random = new Random();

    public static Vector randomSpawnOffset() {
        int offsetIndex = random.nextInt(spawnOffset.length);
        return spawnOffset[offsetIndex].clone();
    }

    private Constants() {}
}
