package ru.vsu.cs.tolkacheva_u_a.task2.model;

import ru.vsu.cs.tolkacheva_u_a.task2.Main.PlayerColor;

/**
 * Класс, представляющий шахматную фигуру.
 * Содержит информацию о типе, цвете, положении и состоянии фигуры.
 */
public class Piece {
    private final PieceType type;
    private final PlayerColor color;
    private Hex position;
    private boolean hasMoved;
    private boolean isCaptured;

    /**
     * Конструктор фигуры.
     */
    public Piece(PieceType type, PlayerColor color, Hex position) {
        this.type = type;
        this.color = color;
        this.position = position;
        this.hasMoved = false;
        this.isCaptured = false;
    }

    public PieceType getType() {
        return type;
    }

    public PlayerColor getColor() {
        return color;
    }

    public Hex getPosition() {
        return position;
    }

    public void setPosition(Hex position) {
        if (position == null) {
            return;
        }
        this.position = position;
        this.hasMoved = true;
    }

    /**
     * Проверяет, делала ли фигура ход.
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Помечает фигуру как взятую.
     */
    public void capture() {
        this.isCaptured = true;
        this.position = null;
    }

    /**
     * Проверяет, была ли фигура взята.
     */
    public boolean isCaptured() {
        return isCaptured;
    }

    @Override
    public String toString() {
        return String.format("%s %s at %s", color, type, position);
    }
}