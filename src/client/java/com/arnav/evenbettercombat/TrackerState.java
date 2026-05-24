package com.arnav.evenbettercombat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class TrackerState {
    public static int streak = 0;
    public static int bestStreak = 0;
    public static int deaths = 0;
    public static boolean hudVisible = true;

    public static void onKill() {
        streak++;
        if (streak > bestStreak) bestStreak = streak;
    }

    public static void onDeath() {
        streak = 0;
        deaths++;
    }

    public static void reset() {
        streak = 0;
        bestStreak = 0;
        deaths = 0;
        hudVisible = true;
    }
}
