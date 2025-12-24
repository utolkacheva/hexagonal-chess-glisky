package ru.vsu.cs.tolkacheva_u_a.task2.model;

/**
 * Класс, представляющий ход в игре.
 * Содержит информацию о перемещаемой фигуре, целевом поле и возможном взятии.
 */
public class Move {
    private final Piece piece;
    private final Hex destination;
    private final Piece capturedPiece;
    private final boolean isPromotion;
    private final PieceType promotionType;
    private final boolean isEnPassant;
    private final boolean isCastling;
    private final Hex enPassantTarget;

    /**
     * Конструктор хода для обычного перемещения.
     */
    public Move(Piece piece, Hex destination, Piece capturedPiece) {
        this(piece, destination, capturedPiece, false, null, false, false, null);
    }

    /**
     * Полный конструктор хода.
     */
    public Move(Piece piece, Hex destination, Piece capturedPiece,
                boolean isPromotion, PieceType promotionType,
                boolean isEnPassant, boolean isCastling,
                Hex enPassantTarget) {
        this.piece = piece;
        this.destination = destination;
        this.capturedPiece = capturedPiece;
        this.isPromotion = isPromotion;
        this.promotionType = promotionType;
        this.isEnPassant = isEnPassant;
        this.isCastling = isCastling;
        this.enPassantTarget = enPassantTarget;
    }

    public Piece getPiece() {
        return piece;
    }

    public Hex getStart() {
        return piece.getPosition();
    }

    public Hex getDestination() {
        return destination;
    }

    /**
     * Проверяет, является ли ход взятием.
     */
    public boolean isCapture() {
        return capturedPiece != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(piece.getType()).append(" ");
        sb.append(getStart()).append(" -> ").append(destination);

        if (isCapture()) {
            sb.append(" (x ").append(capturedPiece.getType()).append(")");
        }

        if (isPromotion) {
            sb.append(" (= ").append(promotionType).append(")");
        }

        if (isEnPassant) {
            sb.append(" e.p.");
        }

        if (isCastling) {
            sb.append(" O-O");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Move move = (Move) obj;
        return piece.equals(move.piece) &&
                destination.equals(move.destination) &&
                ((capturedPiece == null && move.capturedPiece == null) ||
                        (capturedPiece != null && capturedPiece.equals(move.capturedPiece)));
    }

    @Override
    public int hashCode() {
        int result = piece.hashCode();
        result = 31 * result + destination.hashCode();
        result = 31 * result + (capturedPiece != null ? capturedPiece.hashCode() : 0);
        return result;
    }

}