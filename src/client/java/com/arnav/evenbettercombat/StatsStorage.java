package com.arnav.evenbettercombat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public final class StatsStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static class SaveData {
        int streak = 0;
        int bestStreak = 0;
        int deaths = 0;
    }

    private static Path getPath(String serverAddress) {
        String safeName = serverAddress.replaceAll("[^a-zA-Z0-9._-]", "_");
        return FabricLoader.getInstance().getConfigDir()
            .resolve("evenbettercombat")
            .resolve(safeName + ".json");
    }

    public static void load(String serverAddress) {
        Path path = getPath(serverAddress);
        if (!Files.exists(path)) return;
        try (Reader r = Files.newBufferedReader(path)) {
            SaveData data = GSON.fromJson(r, SaveData.class);
            if (data != null) {
                TrackerState.streak = data.streak;
                TrackerState.bestStreak = data.bestStreak;
                TrackerState.deaths = data.deaths;
            }
        } catch (IOException ignored) {}
    }

    public static void save(String serverAddress) {
        Path path = getPath(serverAddress);
        try {
            Files.createDirectories(path.getParent());
            SaveData data = new SaveData();
            data.streak = TrackerState.streak;
            data.bestStreak = TrackerState.bestStreak;
            data.deaths = TrackerState.deaths;
            try (Writer w = Files.newBufferedWriter(path)) {
                GSON.toJson(data, w);
            }
        } catch (IOException ignored) {}
    }
}
