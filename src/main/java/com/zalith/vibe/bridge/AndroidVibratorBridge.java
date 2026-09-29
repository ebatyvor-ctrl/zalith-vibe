package com.zalith.vibe.bridge;

import com.zalith.vibe.ZalithVibeMod;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AndroidVibratorBridge {
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "ZalithVibratorThread");
        t.setDaemon(true);
        return t;
    });

    private volatile int socketFd = -1;
    private volatile boolean initialized = false;

    private Object socketFunc;
    private Object connectFunc;
    private Object writeFunc;
    private Object closeFunc;
    private Method invokeIntMethod;

    public void initialize() {
        executor.execute(this::setupJnaAndConnect);
    }

    private synchronized void setupJnaAndConnect() {
        try {
            Class<?> functionClass = Class.forName("com.sun.jna.Function");
            Method getFunctionMethod = functionClass.getMethod("getFunction", String.class, String.class);
            this.invokeIntMethod = functionClass.getMethod("invokeInt", Object[].class);

            this.socketFunc = getFunctionMethod.invoke(null, "c", "socket");
            this.connectFunc = getFunctionMethod.invoke(null, "c", "connect");
            this.writeFunc = getFunctionMethod.invoke(null, "c", "write");
            this.closeFunc = getFunctionMethod.invoke(null, "c", "close");

            ZalithVibeMod.LOGGER.info("[ZalithVibe] JNA native libc functions loaded successfully!");
        } catch (Throwable t) {
            ZalithVibeMod.LOGGER.warn("[ZalithVibe] JNA libc functions not available: " + t.getMessage());
        }

        tryConnect();
    }

    private synchronized boolean tryConnect() {
        if (socketFd >= 0) return true;
        if (socketFunc == null || connectFunc == null || invokeIntMethod == null) return false;

        String envSocket = System.getenv("TOUCH_CONTROLLER_PROXY_SOCKET");
        String[] targets = new String[] {
            (envSocket != null && !envSocket.isEmpty()) ? envSocket : "Zalith Launcher 2",
            "Zalith Launcher 2",
            "Zalith Launcher",
            "zalith.launcher",
            "pojavlauncher"
        };

        for (String target : targets) {
            try {
                int fd = (int) invokeIntMethod.invoke(socketFunc, (Object) new Object[]{1, 1, 0});
                if (fd < 0) continue;

                byte[] nameBytes = target.getBytes(StandardCharsets.US_ASCII);
                byte[] sockaddr = new byte[2 + 1 + nameBytes.length];
                sockaddr[0] = 1; // AF_UNIX
                sockaddr[1] = 0;
                sockaddr[2] = 0; // Abstract namespace null byte
                System.arraycopy(nameBytes, 0, sockaddr, 3, nameBytes.length);

                int result = (int) invokeIntMethod.invoke(connectFunc, (Object) new Object[]{fd, sockaddr, sockaddr.length});
                if (result == 0) {
                    this.socketFd = fd;
                    this.initialized = true;
                    ZalithVibeMod.LOGGER.info("[ZalithVibe] Connected to Zalith Launcher socket: @" + target);
                    return true;
                } else {
                    if (closeFunc != null) {
                        invokeIntMethod.invoke(closeFunc, (Object) new Object[]{fd});
                    }
                }
            } catch (Throwable ignored) {}
        }
        return false;
    }

    public void vibrate(final long durationMs, final int strength) {
        executor.execute(() -> {
            if (socketFd < 0) {
                tryConnect();
            }

            if (socketFd >= 0 && writeFunc != null && invokeIntMethod != null) {
                try {
                    byte[] packet = new byte[] {
                        8,              // Payload length
                        0, 0, 0, 4,     // Type 4: VibrateMessage
                        0, 0, 0, 0      // Kind 0: BLOCK_BROKEN
                    };
                    int res = (int) invokeIntMethod.invoke(writeFunc, (Object) new Object[]{socketFd, packet, packet.length});
                    if (res == packet.length) {
                        return;
                    } else {
                        if (closeFunc != null) {
                            invokeIntMethod.invoke(closeFunc, (Object) new Object[]{socketFd});
                        }
                        socketFd = -1;
                    }
                } catch (Throwable t) {
                    if (socketFd >= 0) {
                        try {
                            if (closeFunc != null) invokeIntMethod.invoke(closeFunc, (Object) new Object[]{socketFd});
                        } catch (Throwable ignored) {}
                        socketFd = -1;
                    }
                }
            }

            try {
                int ms = (int) Math.max(10, Math.min(durationMs, 500));
                Runtime.getRuntime().exec(new String[]{"cmd", "vibrator", "vibrate", String.valueOf(ms)});
            } catch (Throwable ignored) {}
        });
    }

    public boolean isAvailable() {
        return socketFd >= 0 || initialized;
    }
}
