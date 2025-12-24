package ru.vsu.cs.tolkacheva_u_a.task2.utils;

import ru.vsu.cs.tolkacheva_u_a.task2.model.*;
import ru.vsu.cs.tolkacheva_u_a.task2.Main.PlayerColor;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для проверки допустимости ходов согласно правилам шестиугольных шахмат Глинского.
 */
public class MoveValidator {
    private final Board board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    public boolean isValidMove(Piece piece, Hex destination) {
        if (piece == null || destination == null) {
            return false;
        }

        if (piece.isCaptured()) {
            return false;
        }

        if (!board.isValidHex(destination)) {
            return false;
        }

        if (piece.getColor() != board.getCurrentPlayer()) {
            return false;
        }

        Piece targetPiece = board.getPieceAt(destination);
        if (targetPiece != null && targetPiece.getColor() == piece.getColor()) {
            return false;
        }

        boolean isValid = switch (piece.getType()) {
            case PAWN -> isValidPawnMove(piece, destination);
            case ROOK -> isValidRookMove(piece, destination);
            case KNIGHT -> isValidKnightMove(piece, destination);
            case BISHOP -> isValidBishopMove(piece, destination);
            case QUEEN -> isValidQueenMove(piece, destination);
            case KING -> isValidKingMove(piece, destination);
        };

        if (isValid) {
            isValid = !wouldMoveCauseCheck(piece, destination);
        }

        return isValid;
    }

    /**
     * Проверяет допустимость хода пешки.
     */
    private boolean isValidPawnMove(Piece piece, Hex destination) {
        Hex current = piece.getPosition();
        PlayerColor color = piece.getColor();
        boolean isWhite = (color == PlayerColor.WHITE);

        Hex forwardDir = HexDirection.getForwardDirection(isWhite);
        Hex forwardOne = current.add(forwardDir);

        if (forwardOne.equals(destination) && board.isEmpty(destination)) {
            return true;
        }

        // Двойной ход с начальной позиции
        int startRank = isWhite ? 5 : -5;
        if (current.getR() == startRank && !piece.hasMoved()) {
            Hex forwardTwo = forwardOne.add(forwardDir);
            if (forwardTwo.equals(destination) &&
                    board.isEmpty(forwardOne) &&
                    board.isEmpty(destination)) {
                return true;
            }
        }

        // Взятие по диагонали
        List<Hex> captureMoves = getPawnCaptureMoves(piece);
        for (Hex captureMove : captureMoves) {
            if (captureMove.equals(destination)) {
                Piece target = board.getPieceAt(destination);
                return target != null && target.getColor() != color;
            }
        }

        return false;
    }

    /**
     * Возвращает возможные поля для взятия пешкой.
     */
    private List<Hex> getPawnCaptureMoves(Piece piece) {
        List<Hex> captures = new ArrayList<>();
        Hex current = piece.getPosition();
        boolean isWhite = (piece.getColor() == PlayerColor.WHITE);

        // Получаем направления взятия для данного цвета
        List<Hex> captureDirs = HexDirection.getPawnCaptureDirections(isWhite);
        for (Hex dir : captureDirs) {
            Hex capture = current.add(dir);
            if (board.isValidHex(capture)) {
                captures.add(capture);
            }
        }

        return captures;
    }

    /**
     * Проверяет допустимость хода ладьи.
     */
    private boolean isValidRookMove(Piece piece, Hex destination) {
        boolean isWhite = (piece.getColor() == PlayerColor.WHITE);
        List<Hex> directions = HexDirection.getAllDirections(isWhite);
        return isLinearMove(piece, destination, directions);
    }

    /**
     * Проверяет допустимость хода коня.
     */
    private boolean isValidKnightMove(Piece piece, Hex destination) {
        Hex current = piece.getPosition();

        // Конь ходит на расстояние ровно 2 шага
        if (current.distanceTo(destination) != 2) {
            return false;
        }

        boolean isWhite = (piece.getColor() == PlayerColor.WHITE);
        List<Hex> knightDirections = HexDirection.getKnightDirections(isWhite);

        for (Hex knightMove : knightDirections) {
            Hex target = current.add(knightMove);
            if (target.equals(destination)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Проверяет допустимость хода слона.
     */
    private boolean isValidBishopMove(Piece piece, Hex destination) {
        // Слон ходит только по полям своего цвета
        if (piece.getPosition().getColor() != destination.getColor()) {
            return false;
        }

        boolean isWhite = (piece.getColor() == PlayerColor.WHITE);
        List<Hex> directions = HexDirection.getBishopDirections(isWhite);

        return isLinearMove(piece, destination, directions);
    }

    /**
     * Проверяет допустимость хода ферзя.
     */
    private boolean isValidQueenMove(Piece piece, Hex destination) {
        boolean isWhite = (piece.getColor() == PlayerColor.WHITE);

        // Проверяем ход как ладья
        List<Hex> rookDirections = HexDirection.getAllDirections(isWhite);
        if (isLinearMove(piece, destination, rookDirections)) {
            return true;
        }

        // Проверяем ход как слон
        if (piece.getPosition().getColor() == destination.getColor()) {
            List<Hex> bishopDirections = HexDirection.getBishopDirections(isWhite);
            if (isLinearMove(piece, destination, bishopDirections)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Проверяет допустимость хода короля.
     */
    private boolean isValidKingMove(Piece piece, Hex destination) {
        Hex current = piece.getPosition();

        // Король ходит на одно поле в любом направлении
        if (current.distanceTo(destination) != 1) {
            return false;
        }

        // Проверяем, не находится ли поле под атакой
        return !isSquareUnderAttack(destination, piece.getColor());
    }

    /**
     * Проверяет линейный ход.
     */
    private boolean isLinearMove(Piece piece, Hex destination, List<Hex> directions) {
        Hex current = piece.getPosition();
        Hex direction = HexDirection.getDirectionBetween(current, destination);

        if (direction == null) {
            return false;
        }

        // Проверяем, что направление допустимо для этой фигуры
        boolean validDirection = directions.stream().anyMatch(d -> d.equals(direction));
        if (!validDirection) {
            return false;
        }

        // Проверяем, нет ли фигур на пути
        Hex check = current.add(direction);
        while (!check.equals(destination)) {
            if (!board.isEmpty(check)) {
                return false;
            }
            check = check.add(direction);
        }

        return true;
    }

    /**
     * Проверяет, находится ли поле под атакой фигур противника.
     */
    public boolean isSquareUnderAttack(Hex square, PlayerColor color) {
        PlayerColor opponentColor = (color == PlayerColor.WHITE) ? PlayerColor.BLACK : PlayerColor.WHITE;

        for (Piece piece : board.getPieces()) {
            if (piece.getColor() == opponentColor && !piece.isCaptured()) {
                if (canAttackSquare(piece, square)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Проверяет, может ли фигура атаковать указанное поле.
     */
    private boolean canAttackSquare(Piece piece, Hex square) {
        // Временная доска для проверки
        Board tempBoard = board.copy();
        Piece tempPiece = tempBoard.getPieceAt(piece.getPosition());

        if (tempPiece == null) {
            return false;
        }

        return new MoveValidator(tempBoard).isValidMove(tempPiece, square);
    }

    /**
     * Проверяет, оставит ли ход короля под шахом.
     */
    private boolean wouldMoveCauseCheck(Piece piece, Hex destination) {
        Board tempBoard = board.copy();
        Piece tempPiece = tempBoard.getPieceAt(piece.getPosition());

        if (tempPiece == null) {
            return true;
        }

        tempBoard.movePiece(tempPiece, destination);

        Piece king = findKing(tempBoard, piece.getColor());
        if (king == null) {
            return true;
        }

        MoveValidator tempValidator = new MoveValidator(tempBoard);
        return tempValidator.isSquareUnderAttack(king.getPosition(), piece.getColor());
    }

    /**
     * Находит короля указанного цвета на доске.
     */
    private Piece findKing(Board board, PlayerColor color) {
        return board.getPieces().stream()
                .filter(p -> !p.isCaptured())
                .filter(p -> p.getColor() == color)
                .filter(p -> p.getType() == PieceType.KING)
                .findFirst()
                .orElse(null);
    }

    /**
     * Проверяет, находится ли король под шахом.
     */
    public boolean isKingInCheck(PlayerColor color) {
        Piece king = findKing(board, color);
        if (king == null) {
            return false;
        }

        return isSquareUnderAttack(king.getPosition(), color);
    }

    /**
     * Проверяет, есть ли у игрока допустимые ходы.
     */
    public boolean hasLegalMoves(PlayerColor color) {
        for (Piece piece : board.getPieces()) {
            if (piece.getColor() == color && !piece.isCaptured()) {
                for (int q = -5; q <= 5; q++) {
                    for (int r = -5; r <= 5; r++) {
                        int s = -q - r;
                        if (Math.abs(s) <= 5) {
                            Hex destination = new Hex(q, r, s);
                            if (isValidMove(piece, destination)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}