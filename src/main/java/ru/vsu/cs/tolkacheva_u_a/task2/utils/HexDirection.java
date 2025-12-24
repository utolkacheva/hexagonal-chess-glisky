package ru.vsu.cs.tolkacheva_u_a.task2.utils;

import ru.vsu.cs.tolkacheva_u_a.task2.model.Hex;
import java.util.Arrays;
import java.util.List;

/**
 * Утилитный класс для работы с направлениями на шестиугольной сетке.
 * Предоставляет направления для различных типов ходов фигур.
 */
public class HexDirection {
    private static final Hex[] WHITE_DIRECTIONS = {
            new Hex(1, 0, -1),   // Вправо
            new Hex(1, -1, 0),   // Вправо-вниз
            new Hex(0, -1, 1),   // Вниз-влево
            new Hex(-1, 0, 1),   // Влево
            new Hex(-1, 1, 0),   // Влево-вверх
            new Hex(0, 1, -1)    // Вверх-вправо
    };

    private static final Hex[] WHITE_KNIGHT_DIRECTIONS = {
            new Hex(2, -1, -1),  new Hex(1, -2, 1),   new Hex(-1, -1, 2),
            new Hex(-2, 1, 1),   new Hex(-1, 2, -1),  new Hex(1, 1, -2),
            new Hex(2, 0, -2),   new Hex(0, -2, 2),   new Hex(-2, 0, 2),
            new Hex(0, 2, -2),   new Hex(2, -2, 0),   new Hex(-2, 2, 0)
    };

    private static final Hex[] WHITE_BISHOP_DIRECTIONS = {
            new Hex(2, -1, -1),  new Hex(1, -2, 1),
            new Hex(-1, -1, 2),  new Hex(-2, 1, 1),
            new Hex(-1, 2, -1),  new Hex(1, 1, -2)
    };

    private static final Hex[] BLACK_DIRECTIONS = {
            new Hex(-1, 0, 1),
            new Hex(-1, 1, 0),
            new Hex(0, 1, -1),
            new Hex(1, 0, -1),
            new Hex(1, -1, 0),
            new Hex(0, -1, 1)
    };

    private static final Hex[] BLACK_KNIGHT_DIRECTIONS = {
            new Hex(-2, 1, 1),    new Hex(-1, 2, -1),   new Hex(1, 1, -2),
            new Hex(2, -1, -1),   new Hex(1, -2, 1),    new Hex(-1, -1, 2),
            new Hex(-2, 0, 2),    new Hex(0, 2, -2),    new Hex(2, 0, -2),
            new Hex(0, -2, 2),    new Hex(-2, 2, 0),    new Hex(2, -2, 0)
    };

    private static final Hex[] BLACK_BISHOP_DIRECTIONS = {
            new Hex(-2, 1, 1),    new Hex(-1, 2, -1),
            new Hex(1, 1, -2),    new Hex(2, -1, -1),
            new Hex(1, -2, 1),    new Hex(-1, -1, 2)
    };

    /**
     * Возвращает список всех базовых направлений для белых фигур.
     */
    public static List<Hex> getAllWhiteDirections() {
        return Arrays.asList(WHITE_DIRECTIONS);
    }

    /**
     * Возвращает список направлений для диагональных ходов белого слона.
     */
    public static List<Hex> getWhiteBishopDirections() {
        return Arrays.asList(WHITE_BISHOP_DIRECTIONS);
    }

    /**
     * Возвращает список направлений для ходов белого коня.
     */
    public static List<Hex> getWhiteKnightDirections() {
        return Arrays.asList(WHITE_KNIGHT_DIRECTIONS);
    }

    /**
     * Возвращает список всех базовых направлений для чёрных фигур.
     */
    public static List<Hex> getAllBlackDirections() {
        return Arrays.asList(BLACK_DIRECTIONS);
    }

    /**
     * Возвращает список направлений для диагональных ходов чёрного слона.
     */
    public static List<Hex> getBlackBishopDirections() {
        return Arrays.asList(BLACK_BISHOP_DIRECTIONS);
    }

    /**
     * Возвращает список направлений для ходов чёрного коня.
     */
    public static List<Hex> getBlackKnightDirections() {
        return Arrays.asList(BLACK_KNIGHT_DIRECTIONS);
    }

    /**
     * Возвращает список направлений для фигуры указанного цвета.
     */
    public static List<Hex> getAllDirections(boolean isWhite) {
        return isWhite ? getAllWhiteDirections() : getAllBlackDirections();
    }

    /**
     * Возвращает список направлений для слона указанного цвета.
     */
    public static List<Hex> getBishopDirections(boolean isWhite) {
        return isWhite ? getWhiteBishopDirections() : getBlackBishopDirections();
    }

    /**
     * Возвращает список направлений для коня указанного цвета.
     */
    public static List<Hex> getKnightDirections(boolean isWhite) {
        return isWhite ? getWhiteKnightDirections() : getBlackKnightDirections();
    }

    /**
     * Возвращает направление "вперед" для указанного цвета.
     */
    public static Hex getForwardDirection(boolean isWhite) {
        return isWhite ? new Hex(0, -1, 1) : new Hex(0, 1, -1);
    }

    /**
     * Возвращает направления взятия для пешки указанного цвета.
     */
    public static List<Hex> getPawnCaptureDirections(boolean isWhite) {
        if (isWhite) {
            return Arrays.asList(
                    new Hex(1, -1, 0),
                    new Hex(-1, 0, 1)
            );
        } else {
            return Arrays.asList(
                    new Hex(1, 0, -1),
                    new Hex(-1, 1, 0)
            );
        }
    }

    /**
     * Возвращает направление от одного поля к другому.
     */
    public static Hex getDirectionBetween(Hex from, Hex to) {
        Hex diff = new Hex(
                to.getQ() - from.getQ(),
                to.getR() - from.getR(),
                to.getS() - from.getS()
        );

        int gcd = gcd3(Math.abs(diff.getQ()), Math.abs(diff.getR()), Math.abs(diff.getS()));

        if (gcd == 0) {
            return null;
        }

        return new Hex(diff.getQ() / gcd, diff.getR() / gcd, diff.getS() / gcd);
    }

    /**
     * Вычисляет наибольший общий делитель трех чисел.
     */
    private static int gcd3(int a, int b, int c) {
        return gcd(gcd(a, b), c);
    }

    /**
     * Вычисляет наибольший общий делитель двух чисел.
     */
    private static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}