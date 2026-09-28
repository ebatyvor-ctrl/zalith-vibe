package com.zalith.vibe;

import com.zalith.vibe.bridge.AndroidVibratorBridge;
import com.zalith.vibe.config.VibrationConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Zalith Haptic Vibe
 * Lightweight Fabric 1.21.1 mod providing tactile haptic feedback for block actions in Zalith Launcher 2+.
 * Stripped-down to focus purely on high-performance Android vibration.
 */
public class ZalithVibeMod implements ClientModInitializer {
    public static final String MOD_ID = "zalith_vibe";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ZalithVibeMod instance;
    private final VibrationConfig config = new VibrationConfig();
    private final AndroidVibratorBridge bridge = new AndroidVibratorBridge();

    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("[ZalithVibe] Initializing Zalith Haptic Feedback mod for Minecraft 1.21.1...");
        
        // Load configuration from .minecraft/config/zalith_vibe.json
        config.load();

        // Initialize Android vibration bridge (Zalith Launcher 2+ / Pojav / OS reflection)
        bridge.initialize();

        LOGGER.info("[ZalithVibe] Ready! Android Bridge active: {}", bridge.isAvailable());
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

    /**
     * Called when a player successfully breaks a block.
     */
    public void onBlockBroken() {
        if (!config.enabled || !config.breakEnabled) return;
        bridge.vibrate(config.breakDurationMs, config.breakStrength);
    }

    /**
     * Called when a player places a block in the world.
     */
    public void onBlockPlaced() {
        if (!config.enabled || !config.placeEnabled) return;
        bridge.vibrate(config.placeDurationMs, config.placeStrength);
    }

    /**
     * Called periodically during prolonged mining of tough blocks (e.g. Obsidian).
     */
    public void onMiningTick() {
        if (!config.enabled || !config.miningPulseEnabled) return;
        bridge.vibrate(config.miningPulseMs, Math.max(50, config.breakStrength / 2));
    }

    /**
     * Called when attacking an entity.
     */
    public void onEntityAttacked() {
        if (!config.enabled || !config.attackEnabled) return;
        bridge.vibrate(config.attackDurationMs, config.breakStrength);
    }

    /**
     * Called when taking damage.
     */
    public void onPlayerHurt() {
        if (!config.enabled || !config.damageEnabled) return;
        bridge.vibrate(config.damageDurationMs, 255);
    }
}
