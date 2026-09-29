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
        LOGGER.info("[ZalithVibe] Advanced tactile haptics ready (Fishing, Creeper, Heartbeat, Shield, Bow)!");
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

    public void onBowPull() {
        if (!config.enabled || !config.bowEnabled) return;
        bridge.vibrateMicro();
    }

    public void onBowShoot() {
        if (!config.enabled || !config.bowEnabled) return;
        bridge.vibrate(1);
    }

    public void onHotbarChanged() {
        if (!config.enabled || !config.hotbarEnabled) return;
        bridge.vibrateMicro();
    }

    public void onHeartbeat() {
        if (!config.enabled || !config.heartbeatEnabled) return;
        bridge.vibrateMicro();
    }

    public void onSoundEvent(String path) {
        if (!config.enabled) return;

        // Поклёвка на рыбалке
        if (config.fishingEnabled && (path.contains("fishing_bobber.splash") || path.contains("fishing_bobber.retrieve"))) {
            bridge.vibrate(2);
        }
        // Шипение крипера / взрывчатка
        else if (config.creeperEnabled && (path.contains("creeper.primed") || path.contains("tnt.primed"))) {
            bridge.vibrate(3);
        }
        // Блок щитом
        else if (config.shieldCritEnabled && path.contains("shield.block")) {
            bridge.vibrate(2);
        }
        // Критический удар
        else if (config.shieldCritEnabled && path.contains("attack.crit")) {
            bridge.vibrate(2);
        }
        // Поломка инструмента / брони
        else if (config.itemBreakEnabled && path.contains("item.break")) {
            bridge.vibrate(3);
        }
        // Поедание пищи
        else if (config.eatEnabled && (path.contains("generic.eat") || path.contains("generic.drink"))) {
            bridge.vibrateMicro();
        }
    }
}
