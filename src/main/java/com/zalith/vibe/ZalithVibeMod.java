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
        LOGGER.info("[ZalithVibe] Advanced haptics loaded successfully!");
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

    public void onEntityAttacked(boolean isCrit) {
        if (!config.enabled) return;
        if (isCrit && config.shieldCritEnabled) {
            bridge.vibrate(3); // Мощный критический удар
        } else if (config.attackStrength > 0) {
            bridge.vibrate(config.attackStrength);
        }
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

    public void onFishingBite() {
        if (!config.enabled || !config.fishingEnabled) return;
        bridge.vibrate(2); // Чёткий рывок рыбы
    }

    public void onCreeperWarning() {
        if (!config.enabled || !config.creeperEnabled) return;
        bridge.vibrate(3); // Тревожный сигнал
    }

    public void onEatTick() {
        if (!config.enabled || !config.eatEnabled) return;
        bridge.vibrateMicro();
    }
}
