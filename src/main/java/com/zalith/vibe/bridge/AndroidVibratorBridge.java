package com.zalith.vibe.bridge;

import com.zalith.vibe.ZalithVibeMod;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Universal Android Haptic Bridge.
 * Extracted and optimized from TouchController:
 * Resolves vibration through:
 * 1. Zalith Launcher 2+ Native Bridge
 * 2. PojavLauncher Tools (net.kdt.pojavlaunch.Tools.vibrate)
 * 3. Android ActivityThread / Context / Vibrator reflection (Universal Android fallback)
 * 
 * All vibration requests are asynchronously dispatched so Minecraft's game loop is NEVER blocked.
 */
public class AndroidVibratorBridge {
    private final ExecutorService vibrationExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "ZalithVibratorThread");
        t.setDaemon(true);
        return t;
    });

    private boolean available = false;
    private VibrationMethod method = VibrationMethod.NONE;

    // Reflection caches
    private Method pojavVibrateMethod;
    private Method zalithVibrateMethod;
    private Object androidVibratorInstance;
    private Method androidVibrateMethod;
    private Method androidCreateOneShotMethod;

    public enum VibrationMethod {
        ZALITH_NATIVE,
        POJAV_TOOLS,
        ANDROID_REFLECTION,
        NONE
    }

    public void initialize() {
        // Tier 1: Try Zalith Launcher 2+ Direct Native Bridge
        try {
            Class<?> zalithClass = Class.forName("zalith.launcher.bridge.LauncherBridge");
            zalithVibrateMethod = zalithClass.getMethod("vibrate", long.class, int.class);
            method = VibrationMethod.ZALITH_NATIVE;
            available = true;
            ZalithVibeMod.LOGGER.info("[ZalithVibe] Initialized with Zalith Launcher 2+ Native Bridge!");
            return;
        } catch (Throwable ignored) {}

        // Tier 2: Try PojavLauncher Tools.vibrate(int ms)
        try {
            Class<?> toolsClass = Class.forName("net.kdt.pojavlaunch.Tools");
            pojavVibrateMethod = toolsClass.getMethod("vibrate", int.class);
            method = VibrationMethod.POJAV_TOOLS;
            available = true;
            ZalithVibeMod.LOGGER.info("[ZalithVibe] Initialized with PojavLauncher Tools bridge!");
            return;
        } catch (Throwable ignored) {}

        // Tier 3: Universal Android Framework Reflection (ActivityThread -> Application -> Vibrator)
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentApplicationMethod = activityThreadClass.getMethod("currentApplication");
            Object app = currentApplicationMethod.invoke(null);

            if (app != null) {
                Method getSystemServiceMethod = app.getClass().getMethod("getSystemService", String.class);
                androidVibratorInstance = getSystemServiceMethod.invoke(app, "vibrator");

                if (androidVibratorInstance != null) {
                    try {
                        // Check for VibrationEffect (Android O+ / API 26+)
                        Class<?> vibrationEffectClass = Class.forName("android.os.VibrationEffect");
                        androidCreateOneShotMethod = vibrationEffectClass.getMethod("createOneShot", long.class, int.class);
                        androidVibrateMethod = androidVibratorInstance.getClass().getMethod("vibrate", vibrationEffectClass);
                    } catch (Throwable fallbackPreO) {
                        // Fallback to legacy vibrate(long milliseconds)
                        androidVibrateMethod = androidVibratorInstance.getClass().getMethod("vibrate", long.class);
                    }
                    method = VibrationMethod.ANDROID_REFLECTION;
                    available = true;
                    ZalithVibeMod.LOGGER.info("[ZalithVibe] Initialized with Universal Android Vibrator Reflection!");
                    return;
                }
            }
        } catch (Throwable ignored) {}

        ZalithVibeMod.LOGGER.warn("[ZalithVibe] Running outside Android or launcher bridge unavailable. Vibration disabled.");
        available = false;
        method = VibrationMethod.NONE;
    }

    /**
     * Vibrate device with given duration and strength (1-255).
     */
    public void vibrate(final long durationMs, final int strength) {
        if (!available) return;
        final int clampedDuration = (int) Math.max(1, Math.min(durationMs, 1000));
        final int clampedStrength = Math.max(1, Math.min(strength, 255));

        vibrationExecutor.execute(() -> {
            try {
                switch (method) {
                    case ZALITH_NATIVE -> zalithVibrateMethod.invoke(null, (long) clampedDuration, clampedStrength);
                    case POJAV_TOOLS -> pojavVibrateMethod.invoke(null, clampedDuration);
                    case ANDROID_REFLECTION -> {
                        if (androidCreateOneShotMethod != null) {
                            Object effect = androidCreateOneShotMethod.invoke(null, (long) clampedDuration, clampedStrength);
                            androidVibrateMethod.invoke(androidVibratorInstance, effect);
                        } else if (androidVibrateMethod != null) {
                            androidVibrateMethod.invoke(androidVibratorInstance, (long) clampedDuration);
                        }
                    }
                    default -> {}
                }
            } catch (Throwable t) {
                // Silently handle transient vibration interruption
            }
        });
    }

    public boolean isAvailable() {
        return available;
    }

    public VibrationMethod getMethod() {
        return method;
    }
}
