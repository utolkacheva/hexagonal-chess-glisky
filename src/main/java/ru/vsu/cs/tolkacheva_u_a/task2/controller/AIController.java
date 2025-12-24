package ru.vsu.cs.tolkacheva_u_a.task2.controller;

import ru.vsu.cs.tolkacheva_u_a.task2.model.*;
import ru.vsu.cs.tolkacheva_u_a.task2.utils.MoveValidator;
import ru.vsu.cs.tolkacheva_u_a.task2.Main.PlayerColor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Контроллер бота для игры в шестиугольные шахматы.
 * Делает случайные допустимые ходы для любого игрока.
 */
public class AIController {
    private final Board board;
    private final MoveValidator moveValidator;
    private final Random random;

    public AIController(Board board) {
        this.board = board;
        this.moveValidator = new MoveValidator(board);
        this.random = new Random();
    }

    /**
     * Возвращает случайный допустимый ход для указанного цвета.
     */
    public Move getRandomMoveForPlayer(PlayerColor playerColor) {
        List<Move> allMoves = getAllPossibleMoves(playerColor);

        if (allMoves.isEmpty()) {
            System.out.println("Бот не нашел допустимых ходов для " + playerColor);
            return null;
        }

        Move selectedMove = allMoves.get(random.nextInt(allMoves.size()));
        System.out.println("Бот выбрал ход: " + selectedMove);
        return selectedMove;
    }

    /**
     * Возвращает случайный ход для текущего игрока на доске.
     */
    public Move getRandomMoveForCurrentPlayer() {
        return getRandomMoveForPlayer(board.getCurrentPlayer());
    }

    /**
     * Возвращает все возможные ходы для указанного цвета.
     */
    private List<Move> getAllPossibleMoves(PlayerColor color) {
        List<Move> possibleMoves = new ArrayList<>();

        // Проходим по всем фигурам указанного цвета
        for (Piece piece : board.getPieces()) {
            if (piece.getColor() == color && !piece.isCaptured()) {
                // Для каждой фигуры проверяем все поля на доске
                for (int q = -5; q <= 5; q++) {
                    for (int r = -5; r <= 5; r++) {
                        int s = -q - r;
                        if (Math.abs(s) <= 5) {
                            Hex destination = new Hex(q, r, s);

                            // Проверяем, является ли ход допустимым
                            if (moveValidator.isValidMove(piece, destination)) {
                                Piece targetPiece = board.getPieceAt(destination);
                                possibleMoves.add(new Move(piece, destination, targetPiece));
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Найдено ходов для " + color + ": " + possibleMoves.size());
        return possibleMoves;
    }
}