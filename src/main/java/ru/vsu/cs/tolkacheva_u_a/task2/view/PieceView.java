package ru.vsu.cs.tolkacheva_u_a.task2.view;

import ru.vsu.cs.tolkacheva_u_a.task2.model.Piece;
import ru.vsu.cs.tolkacheva_u_a.task2.Main.PlayerColor;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Класс для графического представления шахматной фигуры.
 */
public class PieceView {
    private static final double PIECE_SIZE = 45.0;
    private final Piece piece;
    private Node pieceNode;

    /**
     * Конструктор представления фигуры.
     */
    public PieceView(Piece piece) {
        this.piece = piece;
        this.pieceNode = createSimplePiece();
        setupPieceStyle();
    }

    /**
     * Создает простое графическое представление фигуры.
     */
    private Node createSimplePiece() {
        Color pieceColor = (piece.getColor() == PlayerColor.WHITE) ?
                Color.WHITE : Color.BLACK;
        Color borderColor = (piece.getColor() == PlayerColor.WHITE) ?
                Color.BLACK : Color.WHITE;

        Circle base = new Circle(PIECE_SIZE / 2);
        base.setFill(pieceColor);
        base.setStroke(borderColor);
        base.setStrokeWidth(2.0);

        String symbol = getPieceSymbol();
        Text symbolText = new Text(symbol);
        symbolText.setFont(Font.font("Arial", PIECE_SIZE * 0.85));
        symbolText.setFill(borderColor);

        javafx.scene.Group group = new javafx.scene.Group();
        group.getChildren().addAll(base, symbolText);

        double textWidth = symbolText.getLayoutBounds().getWidth();
        double textHeight = symbolText.getLayoutBounds().getHeight();

        symbolText.setX(-textWidth / 2);
        symbolText.setY(textHeight / 3);
        return group;
    }

    /**
     * Возвращает символ Unicode для фигуры.
     */
    private String getPieceSymbol() {
        if (piece.getColor() == PlayerColor.WHITE) {
            return switch (piece.getType()) {
                case PAWN -> "♙";
                case ROOK -> "♖";
                case KNIGHT -> "♘";
                case BISHOP -> "♗";
                case QUEEN -> "♕";
                case KING -> "♔";
            };
        } else {
            return switch (piece.getType()) {
                case PAWN -> "♟";
                case ROOK -> "♜";
                case KNIGHT -> "♞";
                case BISHOP -> "♝";
                case QUEEN -> "♛";
                case KING -> "♚";
            };
        }
    }

    /**
     * Настраивает стиль фигуры.
     */
    private void setupPieceStyle() {
        pieceNode.setEffect(new javafx.scene.effect.DropShadow(3, Color.GRAY));
        pieceNode.setOnMousePressed(event -> {
            pieceNode.toFront();
            event.consume();
        });
    }

    public Node getView() { return pieceNode; }

    public Piece getPiece() { return piece; }

    /**
     * Устанавливает позицию фигуры по центру шестиугольника.
     */
    public void setPosition(double centerX, double centerY) {
        // Центрируем фигуру относительно центра шестиугольника
        pieceNode.setLayoutX(centerX - PIECE_SIZE / 2 + 20);
        pieceNode.setLayoutY(centerY - PIECE_SIZE / 2 + 20);
    }

    public void bringToFront() {
        pieceNode.toFront();
    }

    public static double getPieceSize() {
        return PIECE_SIZE;
    }
}