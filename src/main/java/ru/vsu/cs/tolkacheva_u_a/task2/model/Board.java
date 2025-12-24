package ru.vsu.cs.tolkacheva_u_a.task2.model;

import ru.vsu.cs.tolkacheva_u_a.task2.Main.PlayerColor;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий игровую доску
 */
public class Board {
    private static final int BOARD_RADIUS = 5;
    private final List<Piece> pieces;
    private PlayerColor currentPlayer;

    /**
     * Конструктор доски. Создает пустую доску и расставляет фигуры в начальную позицию.
     */
    public Board() {
        this.pieces = new ArrayList<>();
        this.currentPlayer = PlayerColor.WHITE;
        setupInitialPosition();
    }

    /**
     * Расставляет фигуры в начальную позицию согласно правилам шахмат Глинского.
     */
    private void setupInitialPosition() {
        pieces.clear();

        // Белые фигуры (внизу доски)
        setupWhitePieces();

        // Черные фигуры (вверху доски)
        setupBlackPieces();
    }

    /**
     * Расстановка белых фигур согласно описанию.
     */
    private void setupWhitePieces() {
        pieces.add(new Piece(PieceType.BISHOP, PlayerColor.WHITE, new Hex(0, 5, -5)));

        pieces.add(new Piece(PieceType.KING, PlayerColor.WHITE, new Hex(1, 4, -5)));
        pieces.add(new Piece(PieceType.KNIGHT, PlayerColor.WHITE, new Hex(2, 3, -5)));
        pieces.add(new Piece(PieceType.ROOK, PlayerColor.WHITE, new Hex(3, 2, -5)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.WHITE, new Hex(4, 1, -5)));

        pieces.add(new Piece(PieceType.QUEEN, PlayerColor.WHITE, new Hex(-1, 5, -4)));
        pieces.add(new Piece(PieceType.KNIGHT, PlayerColor.WHITE, new Hex(-2, 5, -3)));
        pieces.add(new Piece(PieceType.ROOK, PlayerColor.WHITE, new Hex(-3, 5, -2)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.WHITE, new Hex(-4, 5, -1)));

        pieces.add(new Piece(PieceType.BISHOP, PlayerColor.WHITE, new Hex(0, 4, -4)));
        pieces.add(new Piece(PieceType.BISHOP, PlayerColor.WHITE, new Hex(0, 3, -3)));

        pieces.add(new Piece(PieceType.PAWN, PlayerColor.WHITE, new Hex(0, 1, -1)));

        pieces.add(new Piece(PieceType.PAWN, PlayerColor.WHITE, new Hex(3, 1, -4)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.WHITE, new Hex(2, 1, -3)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.WHITE, new Hex(1, 1, -2)));

        pieces.add(new Piece(PieceType.PAWN, PlayerColor.WHITE, new Hex(-3, 4, -1)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.WHITE, new Hex(-2, 3, -1)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.WHITE, new Hex(-1, 2, -1)));

    }

    /**
     * Расстановка черных фигур (симметрично белым).
     */
    private void setupBlackPieces() {
        pieces.add(new Piece(PieceType.BISHOP, PlayerColor.BLACK, new Hex(0, -5, 5)));

        pieces.add(new Piece(PieceType.KING, PlayerColor.BLACK, new Hex(-1, -4, 5)));
        pieces.add(new Piece(PieceType.KNIGHT, PlayerColor.BLACK, new Hex(-2, -3, 5)));
        pieces.add(new Piece(PieceType.ROOK, PlayerColor.BLACK, new Hex(-3, -2, 5)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.BLACK, new Hex(-4, -1, 5)));

        pieces.add(new Piece(PieceType.QUEEN, PlayerColor.BLACK, new Hex(1, -5, 4)));
        pieces.add(new Piece(PieceType.KNIGHT, PlayerColor.BLACK, new Hex(2, -5, 3)));
        pieces.add(new Piece(PieceType.ROOK, PlayerColor.BLACK, new Hex(3, -5, 2)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.BLACK, new Hex(4, -5, 1)));

        pieces.add(new Piece(PieceType.BISHOP, PlayerColor.BLACK, new Hex(0, -4, 4)));
        pieces.add(new Piece(PieceType.BISHOP, PlayerColor.BLACK, new Hex(0, -3, 3)));

        pieces.add(new Piece(PieceType.PAWN, PlayerColor.BLACK, new Hex(0, -1, 1)));

        pieces.add(new Piece(PieceType.PAWN, PlayerColor.BLACK, new Hex(-3, -1, 4)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.BLACK, new Hex(-2, -1, 3)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.BLACK, new Hex(-1, -1, 2)));

        pieces.add(new Piece(PieceType.PAWN, PlayerColor.BLACK, new Hex(3, -4, 1)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.BLACK, new Hex(2, -3, 1)));
        pieces.add(new Piece(PieceType.PAWN, PlayerColor.BLACK, new Hex(1, -2, 1)));

    }

    /**
     * Возвращает список всех фигур на доске.
     */
    public List<Piece> getPieces() {
        List<Piece> validPieces = new ArrayList<>();
        for (Piece piece : this.pieces) {
            if (piece.getPosition() != null && isValidHex(piece.getPosition())) {
                validPieces.add(piece);
            }
        }
        return validPieces;
    }

    /**
     * Находит фигуру на указанном поле.

     */
    public Piece getPieceAt(Hex hex) {
        if (hex == null || !isValidHex(hex)) {
            return null;
        }

        return pieces.stream()
                .filter(piece -> !piece.isCaptured())
                .filter(piece -> piece.getPosition() != null)
                .filter(piece -> piece.getPosition().equals(hex))
                .findFirst()
                .orElse(null);
    }

    /**
     * Проверяет, является ли поле пустым.
     */
    public boolean isEmpty(Hex hex) {
        return getPieceAt(hex) == null;
    }

    /**
     * Проверяет, находится ли поле в пределах игровой доски.
     */
    public boolean isValidHex(Hex hex) {
        if (hex == null) return false;

        int q = hex.getQ();
        int r = hex.getR();
        int s = hex.getS();

        // Проверка: q + r + s = 0
        if (q + r + s != 0) {
            return false;
        }

        // Проверка в пределах радиуса
        return Math.abs(q) <= BOARD_RADIUS &&
                Math.abs(r) <= BOARD_RADIUS &&
                Math.abs(s) <= BOARD_RADIUS;
    }

    /**
     * Перемещает фигуру на новое поле.
     */
    public boolean movePiece(Piece piece, Hex destination) {
        if (piece == null || destination == null) {
            return false;
        }

        // Проверяем, что поле существует в пределах доски
        if (!isValidHex(destination)) {
            return false;
        }

        Piece targetPiece = getPieceAt(destination);

        if (targetPiece != null && targetPiece.getColor() != piece.getColor()) {
            targetPiece.capture();
        }

        piece.setPosition(destination);

        checkPawnPromotion(piece, destination);

        switchPlayer();

        return true;
    }

    /**
     * Проверяет возможность превращения пешки при достижении последней горизонтали.
     */
    private void checkPawnPromotion(Piece piece, Hex position) {
        if (piece.getType() == PieceType.PAWN) {
            int promotionRank = (piece.getColor() == PlayerColor.WHITE) ? -5 : 5;

            if (position.getR() == promotionRank) {
                int index = pieces.indexOf(piece);
                if (index != -1) {
                    pieces.set(index, new Piece(PieceType.QUEEN, piece.getColor(), position));
                }
            }
        }
    }

    /**
     * Меняет текущего игрока.
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == PlayerColor.WHITE) ? PlayerColor.BLACK : PlayerColor.WHITE;
    }

    /**
     * Возвращает текущего игрока.
     */
    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Создает глубокую копию доски для проверок ходов.
     */
    public Board copy() {
        Board copy = new Board();
        copy.pieces.clear();

        for (Piece piece : this.pieces) {
            // Создаем копию фигуры
            Piece pieceCopy = new Piece(piece.getType(), piece.getColor(), piece.getPosition());
            if (piece.hasMoved()) {
                pieceCopy.setPosition(piece.getPosition()); // Это установит hasMoved = true
            }
            if (piece.isCaptured()) {
                pieceCopy.capture();
            }
            copy.pieces.add(pieceCopy);
        }

        copy.currentPlayer = this.currentPlayer;

        return copy;
    }

}