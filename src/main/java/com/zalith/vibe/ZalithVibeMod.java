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
        LOGGER.info("[ZalithVibe] Initialized with TouchController emulator & multi-strength haptics!");
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

    public void onBlockBroken() {
        if (!config.enabled || !config.breakEnabled) return;
        bridge.vibrate(config.breakStrength);
    }

    public void onBlockPlaced() {
        if (!config.enabled || !config.placeEnabled) return;
        bridge.vibrate(config.placeStrength);
    }

    public void onEntityAttacked() {
        if (!config.enabled || !config.attackEnabled) return;
        bridge.vibrate(config.attackStrength);
    }

    public void onPlayerHurt() {
        if (!config.enabled || !config.damageEnabled) return;
        bridge.vibrate(config.damageStrength);
    }

    public void onHotbarChanged() {
        if (!config.enabled || !config.hotbarEnabled) return;
        bridge.vibrate(config.hotbarStrength);
    }
}
