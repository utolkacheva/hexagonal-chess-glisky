package ru.vsu.cs.tolkacheva_u_a.task2.model;

/**
 * Перечисление типов шахматных фигур.
 * Определяет все возможные типы фигур в шестиугольных шахматах Глинского.
 */
public enum PieceType {
    /** Пешка - самая слабая фигура, имеет особые правила движения */
    PAWN("Pawn", 'P'),

    /** Ладья - ходит по прямым линиям на любое расстояние */
    ROOK("Rook", 'R'),

    /** Конь - ходит "буквой Г", перепрыгивает через другие фигуры */
    KNIGHT("Knight", 'N'),

    /** Слон - ходит по диагоналям своего цвета */
    BISHOP("Bishop", 'B'),

    /** Ферзь - самая сильная фигура, объединяет возможности ладьи и слона */
    QUEEN("Queen", 'Q'),

    /** Король - самая важная фигура, ходит на одно поле в любом направлении */
    KING("King", 'K');

    private final String name;
    private final char symbol;

    /**
     * Конструктор типа фигуры.
     */
    PieceType(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return name;
    }
}