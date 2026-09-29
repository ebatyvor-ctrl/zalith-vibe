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
                int fd = LibC.INSTANCE.socket(1, 1, 0); // AF_UNIX = 1, SOCK_STREAM = 1
                if (fd < 0) continue;

                byte[] nameBytes = target.getBytes(StandardCharsets.US_ASCII);
                // struct sockaddr_un на Linux:
                // short sun_family (1 = AF_UNIX)
                // char sun_path с ведущим '\0' для abstract namespace
                byte[] sockaddr = new byte[2 + 1 + nameBytes.length];
                sockaddr[0] = 1; // AF_UNIX low byte
                sockaddr[1] = 0; // AF_UNIX high byte
                sockaddr[2] = 0; // Abstract namespace null byte
                System.arraycopy(nameBytes, 0, sockaddr, 3, nameBytes.length);

                int result = LibC.INSTANCE.connect(fd, sockaddr, sockaddr.length);
                if (result == 0) {
                    this.socketFd = fd;
                    this.initialized = true;
                    ZalithVibeMod.LOGGER.info("[ZalithVibe] Connected to Zalith Launcher socket: @" + target);
                    return true;
                } else {
                    LibC.INSTANCE.close(fd);
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

            if (socketFd >= 0) {
                try {
                    // Пакет VibrateMessage (TouchController): Длина(8) + Тип(4) + Kind(0)
                    byte[] packet = new byte[] {
                        8,              // Payload length
                        0, 0, 0, 4,     // Type 4: VibrateMessage
                        0, 0, 0, 0      // Kind 0: BLOCK_BROKEN
                    };
                    int res = LibC.INSTANCE.write(socketFd, packet, packet.length);
                    if (res == packet.length) {
                        return; // Успешно отправлено в Zalith Launcher!
                    } else {
                        LibC.INSTANCE.close(socketFd);
                        socketFd = -1;
                    }
                } catch (Throwable t) {
                    if (socketFd >= 0) {
                        try { LibC.INSTANCE.close(socketFd); } catch (Throwable ignored) {}
                        socketFd = -1;
                    }
                }
            }

            // Запасной вызов через Android cmd
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
