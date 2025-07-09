package ru.neoflex.keycloak.util;

import java.time.Duration;
import java.util.function.Supplier;

public class Deferred {
    private Deferred() {
    }

    public static void defer(Supplier<Boolean> condition, Runnable runnable) {
        defer(Duration.ofSeconds(1), 10, condition, runnable);
    }

    public static void defer(Duration delay, Integer attempts, Supplier<Boolean> condition, Runnable runnable) {
        new Thread(() -> {
            for (int i = 0; i < attempts; i++) {
                try {
                    Thread.sleep(delay.toMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException("ThreadUtils.deffer() was interrupted", e);
                }
                if (condition.get()) {
                    runnable.run();
                    return;
                }
            }
            throw new RuntimeException("ThreadUtils.deffer() attempts are over");
        }).start();
    }
}
