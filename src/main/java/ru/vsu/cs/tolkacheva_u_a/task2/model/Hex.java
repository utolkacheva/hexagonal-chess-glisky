package ru.vsu.cs.tolkacheva_u_a.task2.model;

import java.util.Objects;

/**
 * Класс, представляющий шестиугольное поле на доске.
 * Использует систему осевых координат (q, r, s) для представления положения.
 */
public class Hex {
    private final int q;
    private final int r;
    private final int s;
    private HexColor color;

    /**
     * Конструктор шестиугольного поля.
     */
    public Hex(int q, int r, int s) {
        if (q + r + s != 0) {
            throw new IllegalArgumentException("Сумма координат должна быть равна 0: q + r + s = 0");
        }
        this.q = q;
        this.r = r;
        this.s = s;
        this.color = calculateColor();
    }

    /**
     * Вычисляет цвет поля для трехцветной доски Глинского.
     */
    private HexColor calculateColor() {
        int colorIndex = Math.floorMod(q + r, 3);
        return HexColor.values()[colorIndex];
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public int getS() {
        return s;
    }

    /**
     * Возвращает цвет поля.
     */
    public HexColor getColor() {
        return color;
    }

    /**
     * Вычисляет расстояние между двумя полями на шестиугольной сетке.
     */
    public int distanceTo(Hex other) {
        return (Math.abs(q - other.q) + Math.abs(r - other.r) + Math.abs(s - other.s)) / 2;
    }

    /**
     * Добавляет вектор направления к текущему полю.
     */
    public Hex add(Hex direction) {
        return new Hex(q + direction.q, r + direction.r, s + direction.s);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Hex hex = (Hex) obj;
        return q == hex.q && r == hex.r && s == hex.s;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r, s);
    }

    @Override
    public String toString() {
        return String.format("Hex(q=%d, r=%d, s=%d)", q, r, s);
    }

    /**
     * Перечисление цветов полей на доске Глинского.
     */
    public enum HexColor {
        /** Светлое поле */
        LIGHT("#F0D9B5"),
        /** Среднее поле */
        MEDIUM("#B58863"),
        /** Темное поле */
        DARK("#8B4513");

        private final String colorCode;

        HexColor(String colorCode) {
            this.colorCode = colorCode;
        }

    }
}