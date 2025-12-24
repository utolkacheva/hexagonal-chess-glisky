package ru.vsu.cs.tolkacheva_u_a.task2;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.vsu.cs.tolkacheva_u_a.task2.view.GameUI;

/**
 * Главный класс приложения шестиугольных шахмат Глинского.
 * Запускает игру в интерактивном режиме или режиме наблюдения.
 * Обрабатывает аргументы командной строки для выбора режима игры.
 */
public class Main extends Application {

    private static GameMode gameMode = GameMode.INTERACTIVE;
    private static PlayerColor playerColor = PlayerColor.RANDOM;

    /**
     * Точка входа в приложение. Обрабатывает аргументы командной строки.
     */
    public static void main(String[] args) {
        parseArguments(args);
        launch(args);
    }

    /**
     * Парсит аргументы командной строки для определения режима игры.
     */
    private static void parseArguments(String[] args) {
        if (args.length > 0) {
            String firstArg = args[0].trim();

            if (firstArg.equalsIgnoreCase("Хочу играть!")) {
                gameMode = GameMode.INTERACTIVE;

                if (args.length > 1) {
                    String colorArg = args[1].trim().toUpperCase();
                    try {
                        playerColor = PlayerColor.valueOf(colorArg);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Неверный цвет. Используются: WHITE или BLACK. Выбран случайный цвет.");
                        playerColor = PlayerColor.RANDOM;
                    }
                }
            } else if (firstArg.equalsIgnoreCase("Я наблюдатель")) {
                gameMode = GameMode.NON_INTERACTIVE;
            } else {
                System.out.println("Неверный аргумент. Используйте:");
                System.out.println("  'Хочу играть!' [WHITE|BLACK] - для игры");

            }
        }
    }

    /**
     * Запускает JavaFX приложение. Создает главное окно и интерфейс игры.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        GameUI gameUI = new GameUI(primaryStage);
        gameUI.initialize(gameMode, playerColor);
    }

    /**
     * Перечисление возможных режимов игры.
     */
    public enum GameMode {
        /** Интерактивный режим - игрок управляет фигурами */
        INTERACTIVE,
        /** Режим наблюдения - игра происходит автоматически */
        NON_INTERACTIVE
    }

    /**
     * Перечисление возможных цветов игрока.
     */
    public enum PlayerColor {
        /** Белые фигуры */
        WHITE,
        /** Черные фигуры */
        BLACK,
        /** Случайный выбор цвета */
        RANDOM
    }
}