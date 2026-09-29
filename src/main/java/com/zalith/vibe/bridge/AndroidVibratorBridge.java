package com.zalith.vibe.bridge;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.zalith.vibe.ZalithVibeMod;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AndroidVibratorBridge {
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "ZalithVibratorThread");
        t.setDaemon(true);
        return t;
    });

    public interface LibC extends Library {
        LibC INSTANCE = Native.load("c", LibC.class);
        int socket(int domain, int type, int protocol);
        int connect(int fd, byte[] sockaddr, int addrlen);
        int write(int fd, byte[] buf, int count);
        int close(int fd);
    }

    private volatile int socketFd = -1;
    private volatile boolean initialized = false;

    public void initialize() {
        executor.execute(this::tryConnect);
    }

    private synchronized boolean tryConnect() {
        if (socketFd >= 0) return true;

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
                int fd = LibC.INSTANCE.socket(1, 1, 0);
                if (fd < 0) continue;

                byte[] nameBytes = target.getBytes(StandardCharsets.US_ASCII);
                byte[] sockaddr = new byte[2 + 1 + nameBytes.length];
                sockaddr[0] = 1;
                sockaddr[1] = 0;
                sockaddr[2] = 0;
                System.arraycopy(nameBytes, 0, sockaddr, 3, nameBytes.length);

                int result = LibC.INSTANCE.connect(fd, sockaddr, sockaddr.length);
                if (result == 0) {
                    this.socketFd = fd;
                    this.initialized = true;
                    ZalithVibeMod.LOGGER.info("[ZalithVibe] Connected to socket: @" + target);
                    return true;
                } else {
                    LibC.INSTANCE.close(fd);
                }
            } catch (Throwable ignored) {}
        }
        return false;
    }

    public void vibrate(final int repeats) {
        executor.execute(() -> {
            if (socketFd < 0) {
                tryConnect();
            }

            int count = Math.max(1, Math.min(repeats, 4));
            byte[] packet = new byte[] {
                8,
                0, 0, 0, 4,
                0, 0, 0, 0
            };

            for (int i = 0; i < count; i++) {
                if (socketFd >= 0) {
                    try {
                        LibC.INSTANCE.write(socketFd, packet, packet.length);
                    } catch (Throwable t) {
                        try { LibC.INSTANCE.close(socketFd); } catch (Throwable ignored) {}
                        socketFd = -1;
                    }
                } else {
                    try {
                        Runtime.getRuntime().exec(new String[]{"cmd", "vibrator", "vibrate", "60"});
                    } catch (Throwable ignored) {}
                }

                if (i < count - 1) {
                    try { Thread.sleep(70); } catch (InterruptedException ignored) {}
                }
            }
        });
    }

    public boolean isAvailable() {
        return socketFd >= 0 || initialized;
    }
}
