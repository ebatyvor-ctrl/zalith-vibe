package com.zalith.vibe;

import com.zalith.vibe.bridge.AndroidVibratorBridge;
import com.zalith.vibe.config.VibrationConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZalithVibeMod implements ClientModInitializer {
    public static final String MOD_ID = "zalith_vibe";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ZalithVibeMod instance;
    private final AndroidVibratorBridge bridge = new AndroidVibratorBridge();
    private VibrationConfig config;

    @Override
    public void onInitializeClient() {
        instance = this;
        this.config = VibrationConfig.load();
        bridge.initialize();
        LOGGER.info("[ZalithVibe] Initialized with Bow Pull (0.5) and Shoot haptics!");
    }

    public static ZalithVibeMod getInstance() {
        return instance;
    }

    public VibrationConfig getConfig() {
        return config;
    }

    public AndroidVibratorBridge getBridge() {
        return bridge;
    }

    public void onBlockPlaced() {
        if (!config.enabled || config.placeStrength <= 0) return;
        bridge.vibrate(config.placeStrength);
    }

    public void onBlockBroken() {
        if (!config.enabled || config.breakStrength <= 0) return;
        bridge.vibrate(config.breakStrength);
    }

    public void onEntityAttacked() {
        if (!config.enabled || config.attackStrength <= 0) return;
        bridge.vibrate(config.attackStrength);
    }

    // Натяжение тетивы (0.5 - лёгкий микро-клик)
    public void onBowPull() {
        if (!config.enabled || !config.bowEnabled) return;
        bridge.vibrateMicro();
    }

    // Выстрел из лука (1 - импульс отдачи)
    public void onBowShoot() {
        if (!config.enabled || !config.bowEnabled) return;
        bridge.vibrate(1);
    }
}
