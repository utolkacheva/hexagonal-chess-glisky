package ru.vsu.cs.tolkacheva_u_a.task2.view;

import ru.vsu.cs.tolkacheva_u_a.task2.model.*;
import ru.vsu.cs.tolkacheva_u_a.task2.controller.GameController;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Класс для графического представления шестиугольной шахматной доски.
 */
public class HexBoardView {
    private static final double HEX_SIZE = 35.0;
    private static final double HEX_WIDTH = Math.sqrt(3) * HEX_SIZE;
    private static final double HEX_HEIGHT = 2 * HEX_SIZE;

    private final Pane boardPane;
    private final Map<Hex, Polygon> hexMap;
    private final Map<Hex, PieceView> pieceViews;
    private GameController controller;
    private Hex selectedHex;

    public HexBoardView() {
        this.boardPane = new Pane();
        this.hexMap = new HashMap<>();
        this.pieceViews = new HashMap<>();
        this.selectedHex = null;
        boardPane.setStyle("-fx-background-color: #2c3e50;");
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Создает графическое представление доски.
     * Очищает панель и рисует все шестиугольные поля в соответствии с координатами.
     */
    public void createBoard() {
        boardPane.getChildren().clear();
        hexMap.clear();
        pieceViews.clear();

        for (int q = -5; q <= 5; q++) {
            for (int r = -5; r <= 5; r++) {
                int s = -q - r;
                if (Math.abs(s) <= 5) {
                    Hex hex = new Hex(q, r, s);
                    Polygon hexagon = createHexagon(hex);
                    hexMap.put(hex, hexagon);
                    boardPane.getChildren().add(hexagon);
                }
            }
        }

        centerBoard();
    }

    /**
     * Создает шестиугольник для указанного поля.
     */
    private Polygon createHexagon(Hex hex) {
        double centerX = calculateCenterX(hex);
        double centerY = calculateCenterY(hex);

        Polygon hexagon = new Polygon();

        for (int i = 0; i < 6; i++) {
            double angle = 2.0 * Math.PI / 6 * i + Math.PI / 6; // Поворот на 30 градусов
            double x = centerX + HEX_SIZE * Math.cos(angle);
            double y = centerY + HEX_SIZE * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }

        Color baseColor = switch (hex.getColor()) {
            case LIGHT -> Color.web("#F0D9B5");
            case MEDIUM -> Color.web("#B58863");
            case DARK -> Color.web("#8B4513");
        };

        hexagon.setFill(baseColor);
        hexagon.setStroke(Color.BLACK);
        hexagon.setStrokeWidth(1.0);

        hexagon.setOnMouseClicked(event -> handleHexClick(hex, event));
        hexagon.setOnMouseEntered(event -> highlightHex(hexagon, true));
        hexagon.setOnMouseExited(event -> highlightHex(hexagon, false));
        hexagon.setCursor(Cursor.HAND);

        return hexagon;
    }

    /**
     * Вычисляет координату X центра шестиугольника.
     */
    private double calculateCenterX(Hex hex) {
        return boardPane.getWidth() / 2 + HEX_HEIGHT * 0.75 * hex.getR();
    }

    /**
     * Вычисляет координату Y центра шестиугольника.
     */
    private double calculateCenterY(Hex hex) {
        return boardPane.getHeight() / 2 + HEX_WIDTH * (hex.getQ() + hex.getR() / 2.0);
    }

    /**
     * Центрирует доску на панели.
     * Выравнивает все шестиугольные поля относительно центра панели.
     */
    private void centerBoard() {
        double boardWidth = 11 * HEX_HEIGHT * 0.75;
        double boardHeight = 11 * HEX_WIDTH;

        double offsetX = (boardPane.getWidth() - boardWidth) / 2;
        double offsetY = (boardPane.getHeight() - boardHeight) / 2;

        for (Map.Entry<Hex, Polygon> entry : hexMap.entrySet()) {
            Hex hex = entry.getKey();
            Polygon polygon = entry.getValue();

            double centerX = offsetX + HEX_HEIGHT * 0.75 * hex.getR() + boardWidth / 2;
            double centerY = offsetY + HEX_WIDTH * (hex.getQ() + hex.getR() / 2.0) + boardHeight / 2;

            polygon.getPoints().clear();
            for (int i = 0; i < 6; i++) {
                double angle = 2.0 * Math.PI / 6 * i + Math.PI / 6;
                double x = centerX + HEX_SIZE * Math.cos(angle);
                double y = centerY + HEX_SIZE * Math.sin(angle);
                polygon.getPoints().addAll(x, y);
            }
        }
    }

    /**
     * Обрабатывает клик по шестиугольному полю.
     */
    private void handleHexClick(Hex hex, MouseEvent event) {
        if (controller != null) {
            controller.handleHexClick(hex);
        }
    }

    /**
     * Подсвечивает шестиугольник при наведении курсора.
     */
    private void highlightHex(Polygon hexagon, boolean highlight) {
        if (highlight) {
            hexagon.setStroke(Color.YELLOW);
            hexagon.setStrokeWidth(3);
        } else {
            hexagon.setStroke(Color.BLACK);
            hexagon.setStrokeWidth(1);
        }
    }

    /**
     * Отображает фигуру на указанном поле.
     */
    public void drawPiece(Piece piece, Hex hex) {
        removePiece(hex);

        PieceView pieceView = new PieceView(piece);

        Polygon hexagon = hexMap.get(hex);
        if (hexagon != null) {
            // Вычисляем центр шестиугольника
            double centerX = getHexagonCenterX(hexagon);
            double centerY = getHexagonCenterY(hexagon);

            // Устанавливаем позицию фигуры точно по центру шестиугольника
            pieceView.setPosition(centerX, centerY);
        }

        pieceViews.put(hex, pieceView);

        boardPane.getChildren().add(pieceView.getView());

        pieceView.bringToFront();
    }

    /**
     * Вычисляет координату X геометрического центра шестиугольника.
     */
    private double getHexagonCenterX(Polygon hexagon) {
        if (hexagon.getPoints().size() < 6) return 0;

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        for (int i = 0; i < hexagon.getPoints().size(); i += 2) {
            double x = hexagon.getPoints().get(i);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
        }

        return (minX + maxX) / 2;
    }

    /**
     * Вычисляет координату Y геометрического центра шестиугольника.
     */
    private double getHexagonCenterY(Polygon hexagon) {
        if (hexagon.getPoints().size() < 6) return 0;

        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (int i = 1; i < hexagon.getPoints().size(); i += 2) {
            double y = hexagon.getPoints().get(i);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        return (minY + maxY) / 2;
    }

    /**
     * Удаляет фигуру с указанного поля.
     */
    public void removePiece(Hex hex) {
        PieceView pieceView = pieceViews.remove(hex);
        if (pieceView != null && pieceView.getView() != null) {
            boardPane.getChildren().remove(pieceView.getView());
        }
    }

    /**
     * Перемещает фигуру с одного поля на другое.
     */
    public void movePiece(Hex from, Hex to) {
        System.out.println("Перемещение фигуры с " + from + " на " + to);

        // Получаем представление фигуры
        PieceView pieceView = pieceViews.get(from);
        if (pieceView == null) {
            System.err.println("Ошибка: фигура не найдена на поле " + from);
            return;
        }

        // Удаляем старую запись
        pieceViews.remove(from);

        // Обновляем позицию фигуры в модели
        pieceView.getPiece().setPosition(to);

        // Добавляем новую запись с новой позицией
        pieceViews.put(to, pieceView);

        // Получаем целевой шестиугольник
        Polygon targetHex = hexMap.get(to);
        if (targetHex != null) {
            // Вычисляем новый центр
            double centerX = getHexagonCenterX(targetHex);
            double centerY = getHexagonCenterY(targetHex);

            // Обновляем позицию фигуры на экране
            pieceView.setPosition(centerX, centerY);

            // Поднимаем фигуру на передний план
            pieceView.bringToFront();
        }

        boardPane.requestLayout();
    }

    /**
     * Подсвечивает выбранное поле.
     */
    public void highlightSelectedHex(Hex hex, boolean selected) {
        if (selectedHex != null) {
            Polygon oldHex = hexMap.get(selectedHex);
            if (oldHex != null) {
                resetHexColor(oldHex, selectedHex);
            }
        }

        if (selected && hex != null) {
            Polygon newHex = hexMap.get(hex);
            if (newHex != null) {
                newHex.setStroke(Color.RED);
                newHex.setStrokeWidth(3);
                selectedHex = hex;
            }
        } else {
            selectedHex = null;
        }
    }

    /**
     * Восстанавливает исходный цвет шестиугольника.
     */
    private void resetHexColor(Polygon hexagon, Hex hex) {
        Color baseColor = switch (hex.getColor()) {
            case LIGHT -> Color.web("#F0D9B5");
            case MEDIUM -> Color.web("#B58863");
            case DARK -> Color.web("#8B4513");
        };
        hexagon.setFill(baseColor);
        hexagon.setStroke(Color.BLACK);
        hexagon.setStrokeWidth(1);
    }

    /**
     * Подсвечивает доступные ходы.
     */
    public void highlightValidMoves(List<Hex> validMoves) {
        clearHighlights();

        if (validMoves != null) {
            for (Hex move : validMoves) {
                Polygon hexagon = hexMap.get(move);
                if (hexagon != null) {
                    if (controller != null && controller.isSquareOccupiedByOpponent(move)) {
                        hexagon.setFill(Color.rgb(255, 100, 100, 0.7));
                    } else {
                        hexagon.setStroke(Color.GREEN);
                        hexagon.setStrokeWidth(2);
                    }
                }
            }
        }
    }

    /**
     * Очищает все подсветки на доске.
     * Восстанавливает исходные цвета всех полей.
     */
    public void clearHighlights() {
        for (Map.Entry<Hex, Polygon> entry : hexMap.entrySet()) {
            Hex hex = entry.getKey();
            Polygon hexagon = entry.getValue();
            resetHexColor(hexagon, hex);
        }
    }

    /**
     * Очищает доску.
     * Удаляет все графические элементы с панели.
     */
    public void clearBoard() {
        boardPane.getChildren().clear();
        hexMap.clear();
        pieceViews.clear();
    }

    /**
     * Обновляет размеры доски.
     * Пересчитывает позиции всех элементов при изменении размера окна.ф
     */
    public void updateSize(double width, double height) {
        boardPane.setPrefSize(width, height);
        centerBoard();

        for (Map.Entry<Hex, PieceView> entry : pieceViews.entrySet()) {
            Hex hex = entry.getKey();
            PieceView pieceView = entry.getValue();

            Polygon hexagon = hexMap.get(hex);
            if (hexagon != null && pieceView != null) {
                double centerX = getHexagonCenterX(hexagon);
                double centerY = getHexagonCenterY(hexagon);
                pieceView.setPosition(centerX, centerY);
            }
        }

        boardPane.requestLayout();
    }

    public Pane getBoardPane() {
        return boardPane;
    }

}