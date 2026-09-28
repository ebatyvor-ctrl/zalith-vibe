package com.zalith.vibe.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zalith.vibe.ZalithVibeMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class VibrationConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "zalith_vibe.json");

    // Master switch
    public boolean enabled = true;

    // Block breaking
    public boolean breakEnabled = true;
    public int breakDurationMs = 45;
    public int breakStrength = 220;

    // Block placing
    public boolean placeEnabled = true;
    public int placeDurationMs = 25;
    public int placeStrength = 160;

    // Mining tick pulse (continuous feedback while mining obsidian, etc.)
    public boolean miningPulseEnabled = true;
    public int miningPulseMs = 15;

    // Combat & interactions
    public boolean attackEnabled = true;
    public int attackDurationMs = 35;

    public boolean bowEnabled = true;
    public int bowDurationMs = 30;

    public boolean damageEnabled = true;
    public int damageDurationMs = 80;

    public void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            VibrationConfig loaded = GSON.fromJson(reader, VibrationConfig.class);
            if (loaded != null) {
                this.enabled = loaded.enabled;
                this.breakEnabled = loaded.breakEnabled;
                this.breakDurationMs = loaded.breakDurationMs;
                this.breakStrength = loaded.breakStrength;
                this.placeEnabled = loaded.placeEnabled;
                this.placeDurationMs = loaded.placeDurationMs;
                this.placeStrength = loaded.placeStrength;
                this.miningPulseEnabled = loaded.miningPulseEnabled;
                this.miningPulseMs = loaded.miningPulseMs;
                this.attackEnabled = loaded.attackEnabled;
                this.attackDurationMs = loaded.attackDurationMs;
                this.bowEnabled = loaded.bowEnabled;
                this.bowDurationMs = loaded.bowDurationMs;
                this.damageEnabled = loaded.damageEnabled;
                this.damageDurationMs = loaded.damageDurationMs;
            }
        } catch (Exception e) {
            ZalithVibeMod.LOGGER.error("[ZalithVibe] Failed to load config, keeping defaults", e);
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
}
