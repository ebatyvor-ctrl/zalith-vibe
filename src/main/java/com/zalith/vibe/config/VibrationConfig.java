package com.zalith.vibe.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zalith.vibe.ZalithVibeMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class VibrationConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "zalith_vibe.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean enabled = true;
    public int breakStrength = 2; // 0 = Выкл, 1 = Слабая, 2 = Средняя, 3 = Сильная
    public int placeStrength = 1;
    public int attackStrength = 2;

    public static VibrationConfig load() {
        if (!CONFIG_FILE.exists()) {
            VibrationConfig cfg = new VibrationConfig();
            cfg.save();
            return cfg;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            VibrationConfig cfg = GSON.fromJson(reader, VibrationConfig.class);
            return cfg != null ? cfg : new VibrationConfig();
        } catch (Exception e) {
            ZalithVibeMod.LOGGER.error("[ZalithVibe] Failed to load config", e);
            return new VibrationConfig();
        }
    }

    public void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (Exception e) {
            ZalithVibeMod.LOGGER.error("[ZalithVibe] Failed to save config", e);
        }
    }

    public static String getStrengthLabel(int strength) {
        return switch (strength) {
            case 0 -> "ВЫКЛ";
            case 1 -> "Слабая (1x)";
            case 2 -> "Средняя (2x)";
            case 3 -> "Сильная (3x)";
            default -> "ВЫКЛ";
        };
    }
}
