package com.zalith.vibe;

import com.zalith.vibe.bridge.AndroidVibratorBridge;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZalithVibeMod implements ClientModInitializer {
    public static final String MOD_ID = "zalith_vibe";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ZalithVibeMod instance;
    private final AndroidVibratorBridge bridge = new AndroidVibratorBridge();

    @Override
    public void onInitializeClient() {
        instance = this;
        bridge.initialize();
        LOGGER.info("[ZalithVibe] Initialized with TouchController alias and multi-pulse haptics!");
    }

    public static ZalithVibeMod getInstance() {
        return instance;
    }

    public AndroidVibratorBridge getBridge() {
        return bridge;
    }

    // Установка блока: мягкий одиночный отклик (1 импульс)
    public void onBlockPlaced() {
        bridge.vibrate(1);
    }

    // Ломание блока: уверенный двойной отклик (2 импульса)
    public void onBlockBroken() {
        bridge.vibrate(2);
    }

    // Удар по мобу: сильный акцент (2 быстрых импульса)
    public void onEntityAttacked() {
        bridge.vibrate(2);
    }
}
