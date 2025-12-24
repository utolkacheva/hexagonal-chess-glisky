package ru.vsu.cs.tolkacheva_u_a.task2.view;

import ru.vsu.cs.tolkacheva_u_a.task2.Main;
import ru.vsu.cs.tolkacheva_u_a.task2.controller.GameController;
import ru.vsu.cs.tolkacheva_u_a.task2.utils.GameTimer;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * Класс для интерфейса игры.
 */
public class GameUI {
    private final Stage primaryStage;
    private GameController gameController;
    private HexBoardView boardView;
    private GameTimer gameTimer;

    private Label statusLabel;
    private Label timerLabel;
    private Label playerLabel;

    public GameUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Инициализирует графический интерфейс игры.
     */
    public void initialize(Main.GameMode gameMode, Main.PlayerColor playerColor) {

        BorderPane root = new BorderPane();

        boardView = new HexBoardView();
        root.setCenter(boardView.getBoardPane());

        VBox statusPanel = createStatusPanel();
        root.setTop(statusPanel);

        VBox controlPanel = createControlPanel();
        root.setRight(controlPanel);

        gameController = new GameController(boardView, this);
        boardView.setController(gameController);

        gameController.initializeGame(gameMode, playerColor);

        Scene scene = new Scene(root, 1200, 800);

        String title = "Шестиугольные шахматы Глинского";
        if (gameMode == Main.GameMode.NON_INTERACTIVE) {
            title += " [РЕЖИМ НАБЛЮДАТЕЛЯ]";
        }

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        boardView.updateSize(scene.getWidth() - 200, scene.getHeight() - 100);

        startGameTimer();
    }

    /**
     * Создает верхнюю панель статуса игры.
     */
    private VBox createStatusPanel() {
        VBox statusPanel = new VBox(10);
        statusPanel.setPadding(new Insets(10));
        statusPanel.setStyle("-fx-background-color: #34495e; -fx-border-color: #2c3e50; -fx-border-width: 0 0 2 0;");

        Label titleLabel = new Label("ШЕСТИУГОЛЬНЫЕ ШАХМАТЫ ГЛИНСКОГО");
        titleLabel.setFont(Font.font("Arial", 20));
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        HBox infoPanel = new HBox(20);
        infoPanel.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("Статус: Ожидание начала игры");
        statusLabel.setFont(Font.font("Arial", 14));
        statusLabel.setStyle("-fx-text-fill: white;");

        timerLabel = new Label("Время: 05:00");
        timerLabel.setFont(Font.font("Arial", 14));
        timerLabel.setStyle("-fx-text-fill: white;");

        playerLabel = new Label("Игрок: Белые");
        playerLabel.setFont(Font.font("Arial", 14));
        playerLabel.setStyle("-fx-text-fill: white;");

        infoPanel.getChildren().addAll(statusLabel, timerLabel, playerLabel);
        statusPanel.getChildren().addAll(titleLabel, infoPanel);
        return statusPanel;
    }

    /**
     * Создает правую панель управления игрой.
     * Содержит кнопки для управления игровым процессом.
     */
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(15);
        controlPanel.setPadding(new Insets(20));
        controlPanel.setPrefWidth(200);
        controlPanel.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 0 0 0 2;");


        Button newGameButton = createStyledButton("Новая игра");
        newGameButton.setOnAction(e -> gameController.startNewGame());

        Button resignButton = createStyledButton("Сдаться");
        resignButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        resignButton.setOnAction(e -> gameController.resign());

        controlPanel.getChildren().addAll(
                newGameButton,
                resignButton
        );

        return controlPanel;
    }

    /**
     * Создает стилизованную кнопку с единым оформлением.
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(160);
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 5; -fx-border-radius: 5;");

        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 5; -fx-border-radius: 5;"));
        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 5; -fx-border-radius: 5;"));

        return button;
    }

    /**
     * Запускает игровой таймер.
     */
    private void startGameTimer() {
        gameTimer = new GameTimer(new GameTimer.TimerCallback() {
            @Override
            public void onTimeUpdate(int minutes, int seconds) {
                updateTimerDisplay(minutes, seconds);
            }

            @Override
            public void onTimeExpired() {
                handleTimeExpired();
            }
        });
        gameTimer.start();
    }

    /**
     * Обновляет отображение статуса игры.
     */
    public void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText("Статус: " + message);
        }
    }

    /**
     * Обновляет отображение таймера.
     */
    public void updateTimerDisplay(int minutes, int seconds) {
        if (timerLabel != null) {
            timerLabel.setText(String.format("Время: %02d:%02d", minutes, seconds));
        }
    }

    /**
     * Обновляет отображение информации о текущем игроке.
     * Выделяет цветом и добавляет пометку, если это ход текущего игрока.
     */
    public void updatePlayerDisplay(String playerName, boolean isCurrentPlayerTurn) {
        if (playerLabel != null) {
            String text = "Игрок: " + playerName;
            if (isCurrentPlayerTurn) {
                text += " (Ваш ход)";
                playerLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            } else {
                playerLabel.setStyle("-fx-text-fill: white;");
            }
            playerLabel.setText(text);
        }
    }

    /**
     * Обрабатывает событие окончания времени.
     */
    private void handleTimeExpired() {
        if (gameController != null) {
            gameController.handleTimeExpired();
        }
    }

    /**
     * Показывает диалоговое окно с информацией о игре.
     */
    public void showGameDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }
}