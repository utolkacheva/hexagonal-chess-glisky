package ru.vsu.cs.tolkacheva_u_a.task2.utils;

import javafx.application.Platform;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Таймер игры с ограничением времени 5 минут.
 */
public class GameTimer {
    private static final int GAME_DURATION_MINUTES = 5;
    private static final int MILLIS_PER_MINUTE = 60000;

    private Timer timer;
    private long startTime;
    private long elapsedTime;
    private boolean isRunning;
    private TimerCallback callback;

    /**
     * Интерфейс для обратного вызова.
     */
    public interface TimerCallback {
        void onTimeUpdate(int minutes, int seconds);
        void onTimeExpired();
    }

    /**
     * Конструктор таймера.
     */
    public GameTimer(TimerCallback callback) {
        this.callback = callback;
        this.timer = new Timer(true);
        this.isRunning = false;
        this.elapsedTime = 0;
    }

    /**
     * Запускает таймер.
     */
    public void start() {
        if (isRunning) {
            return;
        }

        startTime = System.currentTimeMillis();
        isRunning = true;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        }, 0, 1000);
    }

    /**
     * Обновляет время.
     */
    private void updateTime() {
        if (!isRunning) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        elapsedTime = currentTime - startTime;

        long remainingMillis = (GAME_DURATION_MINUTES * MILLIS_PER_MINUTE) - elapsedTime;

        if (remainingMillis <= 0) {
            stop();
            Platform.runLater(() -> {
                if (callback != null) {
                    callback.onTimeExpired();
                }
            });
            return;
        }

        int minutes = (int) (remainingMillis / MILLIS_PER_MINUTE);
        int seconds = (int) ((remainingMillis % MILLIS_PER_MINUTE) / 1000);

        Platform.runLater(() -> {
            if (callback != null) {
                callback.onTimeUpdate(minutes, seconds);
            }
        });
    }

    /**
     * Останавливает таймер.
     */
    public void stop() {
        isRunning = false;
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Сбрасывает таймер.
     */
    public void reset() {
        stop();
        elapsedTime = 0;
        timer = new Timer(true);
    }
}