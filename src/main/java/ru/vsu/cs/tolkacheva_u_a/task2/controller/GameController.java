package ru.vsu.cs.tolkacheva_u_a.task2.controller;

import ru.vsu.cs.tolkacheva_u_a.task2.view.HexBoardView;
import ru.vsu.cs.tolkacheva_u_a.task2.view.GameUI;
import ru.vsu.cs.tolkacheva_u_a.task2.Main;
import ru.vsu.cs.tolkacheva_u_a.task2.model.*;
import ru.vsu.cs.tolkacheva_u_a.task2.utils.MoveValidator;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Главный контроллер игры.
 * Управляет игровой логикой, обработкой пользовательского ввода,
 * взаимодействием между компонентами модели, представления и интерфейса.
 * Реализует два режима игры: интерактивный (человек против бота) и
 * неинтерактивный (бот против бота, режим наблюдения).
 */
public class GameController {
    private final HexBoardView boardView;
    private final GameUI gameUI;
    private Board board;
    private MoveValidator moveValidator;
    private AIController aiController;
    private Main.GameMode gameMode;
    private Main.PlayerColor humanPlayerColor;
    private Hex selectedHex;
    private Piece selectedPiece;
    private boolean isGameActive;
    private List<Move> moveHistory;
    private Random random;
    private Thread observerThread;
    private int observerMoveCount = 0;

    public GameController(HexBoardView boardView, GameUI gameUI) {
        this.boardView = boardView;
        this.gameUI = gameUI;
        this.moveHistory = new ArrayList<>();
        this.random = new Random();
        this.isGameActive = false;
        this.selectedHex = null;
        this.selectedPiece = null;
        this.observerThread = null;
    }

    /**
     * Инициализирует игру в указанном режиме.
     * Настраивает параметры игры в соответствии с переданными аргументами.
     */
    public void initializeGame(Main.GameMode gameMode, Main.PlayerColor playerColor) {

        this.gameMode = gameMode;

        if (gameMode == Main.GameMode.NON_INTERACTIVE) {
            this.humanPlayerColor = null;
        } else {
            if (playerColor == Main.PlayerColor.RANDOM) {
                this.humanPlayerColor = random.nextBoolean() ? Main.PlayerColor.WHITE : Main.PlayerColor.BLACK;
            } else {
                this.humanPlayerColor = playerColor;
            }
        }

        startNewGame();

        if (gameMode == Main.GameMode.NON_INTERACTIVE) {
            startObserverMode();
        }

        updateUI();
    }

    /**
     * Начинает новую партию.
     */
    public void startNewGame() {

        if (isGameActive && gameUI.getGameTimer() != null) {
            gameUI.getGameTimer().stop();
        }

        if (observerThread != null && observerThread.isAlive()) {
            observerThread.interrupt();
            try {
                observerThread.join(1000);
            } catch (InterruptedException e) {
            }
        }

        board = new Board();
        moveValidator = new MoveValidator(board);
        aiController = new AIController(board);

        moveHistory.clear();
        selectedHex = null;
        selectedPiece = null;
        isGameActive = true;
        observerMoveCount = 0;

        boardView.clearBoard();
        boardView.createBoard();

        for (Piece piece : board.getPieces()) {
            boardView.drawPiece(piece, piece.getPosition());
        }

        updateUI();

        if (gameUI.getGameTimer() != null) {
            gameUI.getGameTimer().reset();
            gameUI.getGameTimer().start();
        }

        if (gameMode == Main.GameMode.NON_INTERACTIVE) {
            Platform.runLater(() -> makeAIMove());
        } else if (gameMode == Main.GameMode.INTERACTIVE &&
                humanPlayerColor != null &&
                board.getCurrentPlayer() != humanPlayerColor) {
            Platform.runLater(() -> makeAIMove());
        }
    }

    /**
     * Запускает режим наблюдения.
     */
    private void startObserverMode() {

        observerThread = new Thread(() -> {

            while (isGameActive) {
                try {
                    Thread.sleep(1500);

                    if (!isGameActive) {
                        break;
                    }

                    Platform.runLater(() -> {
                        if (isGameActive) {
                            observerMoveCount++;
                            makeAIMove();
                        }
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        });

        observerThread.setDaemon(true);
        observerThread.setName("Observer-Thread");
        observerThread.start();
    }

    /**
     * Обрабатывает клик пользователя по шестиугольному полю на доске.
     * Реализует логику выбора фигуры и выполнения хода в интерактивном режиме.
     */
    public void handleHexClick(Hex hex) {
        if (!isGameActive || gameMode != Main.GameMode.INTERACTIVE) {
            return;
        }

        if (humanPlayerColor == null || board.getCurrentPlayer() != humanPlayerColor) {
            gameUI.updateStatus("Сейчас не ваш ход");
            return;
        }

        Piece clickedPiece = board.getPieceAt(hex);

        if (selectedHex == null) {
            if (clickedPiece != null && clickedPiece.getColor() == humanPlayerColor) {
                selectedHex = hex;
                selectedPiece = clickedPiece;
                highlightValidMoves(selectedPiece);
                gameUI.updateStatus("Выбрана " + selectedPiece.getType() + ". Выберите поле для хода.");
            } else {
                gameUI.updateStatus("Выберите свою фигуру для хода");
            }
        } else {
            if (hex.equals(selectedHex)) {
                clearSelection();
                gameUI.updateStatus("Выбор отменен");
            } else if (clickedPiece != null && clickedPiece.getColor() == humanPlayerColor) {
                clearSelection();
                selectedHex = hex;
                selectedPiece = clickedPiece;
                highlightValidMoves(selectedPiece);
                gameUI.updateStatus("Выбрана " + selectedPiece.getType() + ". Выберите поле для хода.");
            } else {
                if (selectedPiece != null && moveValidator.isValidMove(selectedPiece, hex)) {
                    executeMove(selectedPiece, hex);
                    clearSelection();

                    if (isGameActive && board.getCurrentPlayer() != humanPlayerColor) {
                        Platform.runLater(() -> makeAIMove());
                    }
                } else {
                    gameUI.updateStatus("Недопустимый ход для " + selectedPiece.getType());
                }
            }
        }

        updateUI();
    }

    /**
     * Очищает текущий выбор фигуры.
     * Снимает выделение с поля и скрывает подсветку допустимых ходов.
     */
    private void clearSelection() {
        selectedHex = null;
        selectedPiece = null;
        boardView.clearHighlights();
    }

    /**
     * Выполняет ход за бота.
     * В режиме наблюдения бот делает ходы за обоих игроков,
     * в интерактивном режиме - только за противника человека.
     * Выбирает случайный допустимый ход из доступных для текущего игрока.
     */
    public void makeAIMove() {
        if (!isGameActive) {
            return;
        }

        if (gameMode == Main.GameMode.INTERACTIVE &&
                humanPlayerColor != null &&
                board.getCurrentPlayer() == humanPlayerColor) {
            return;
        }

        Move aiMove = aiController.getRandomMoveForCurrentPlayer();

        if (aiMove != null) {
            executeMove(aiMove.getPiece(), aiMove.getDestination());
        } else {
            checkGameState();
        }
    }

    /**
     * Выполняет ход фигурой на указанное поле.
     * Обновляет модель доски, графическое представление,
     * обрабатывает взятие фигур и проверяет состояние игры после хода.
     */
    private void executeMove(Piece piece, Hex destination) {

        Hex startPosition = piece.getPosition();
        Piece targetPiece = board.getPieceAt(destination);
        Move move = new Move(piece, destination, targetPiece);
        moveHistory.add(move);

        if (targetPiece != null) {
            targetPiece.capture();
            boardView.removePiece(destination);
        }

        boardView.movePiece(startPosition, destination);
        board.movePiece(piece, destination);


        checkGameState();
        updateUI();
    }

    /**
     * Проверяет состояние игры после выполнения хода.
     * Определяет наличие мата, пата или продолжение игры.
     * При отсутствии допустимых ходов завершает игру с соответствующим сообщением.
     */
    private void checkGameState() {
        boolean hasLegalMoves = moveValidator.hasLegalMoves(board.getCurrentPlayer());

        if (!hasLegalMoves) {
            boolean isCheck = moveValidator.isKingInCheck(board.getCurrentPlayer());

            if (isCheck) {
                Main.PlayerColor winner = (board.getCurrentPlayer() == Main.PlayerColor.WHITE) ?
                        Main.PlayerColor.BLACK : Main.PlayerColor.WHITE;
                endGame("Мат! " + getPlayerName(winner) + " побеждают!");
            } else {
                Main.PlayerColor winner = (board.getCurrentPlayer() == Main.PlayerColor.WHITE) ?
                        Main.PlayerColor.BLACK : Main.PlayerColor.WHITE;
                endGame("Пат! " + getPlayerName(winner) + " выигрывают.");
            }
        }
    }

    /**
     * Возвращает имя игрока по его цвету с учетом текущего режима игры.
     */
    private String getPlayerName(Main.PlayerColor color) {
        if (gameMode == Main.GameMode.INTERACTIVE && color == humanPlayerColor) {
            return "Вы";
        } else if (gameMode == Main.GameMode.INTERACTIVE) {
            return "Бот";
        } else {
            return (color == Main.PlayerColor.WHITE) ? "Белые" : "Черные";
        }
    }

    /**
     * Завершает игру с указанным сообщением.
     * Останавливает все активные процессы (таймер, поток наблюдателя),
     * меняет состояние игры и отображает диалоговое окно с результатом.
     */
    private void endGame(String message) {
        isGameActive = false;

        if (observerThread != null && observerThread.isAlive()) {
            observerThread.interrupt();
        }

        if (gameUI.getGameTimer() != null) {
            gameUI.getGameTimer().stop();
        }

        gameUI.showGameDialog("Игра окончена", message);
    }

    /**
     * Подсвечивает допустимые ходы для выбранной фигуры.
     * Определяет все поля, на которые фигура может переместиться согласно правилам,
     * и выделяет их на графическом представлении доски.
     */
    private void highlightValidMoves(Piece piece) {
        List<Hex> validMoves = new ArrayList<>();

        for (int q = -5; q <= 5; q++) {
            for (int r = -5; r <= 5; r++) {
                int s = -q - r;
                if (Math.abs(s) <= 5) {
                    Hex destination = new Hex(q, r, s);
                    if (moveValidator.isValidMove(piece, destination)) {
                        validMoves.add(destination);
                    }
                }
            }
        }

        boardView.highlightSelectedHex(piece.getPosition(), true);
        boardView.highlightValidMoves(validMoves);
    }

    /**
     * Проверяет, занято ли поле фигурой противника.
     */
    public boolean isSquareOccupiedByOpponent(Hex hex) {
        if (board == null || hex == null) {
            return false;
        }

        Piece piece = board.getPieceAt(hex);
        if (piece == null) {
            return false;
        }

        return piece.getColor() != board.getCurrentPlayer();
    }

    /**
     * Обновляет пользовательский интерфейс..
     */
    private void updateUI() {
        if (!isGameActive) {
            return;
        }

        String status;
        if (selectedPiece != null) {
            status = "Выбрана " + selectedPiece.getType() + ". Выберите поле для хода.";
        } else {
            if (gameMode == Main.GameMode.NON_INTERACTIVE) {
                status = "Автоигра: ход " + board.getCurrentPlayer();
            } else {
                status = "Ход " + board.getCurrentPlayer();
            }
        }

        if (moveValidator.isKingInCheck(board.getCurrentPlayer())) {
            status += " (ШАХ!)";
        }
        gameUI.updateStatus(status);

        boolean isHumanTurn = (gameMode == Main.GameMode.INTERACTIVE &&
                humanPlayerColor != null &&
                board.getCurrentPlayer() == humanPlayerColor);
        String playerName = (board.getCurrentPlayer() == Main.PlayerColor.WHITE) ? "Белые" : "Черные";

        if (gameMode == Main.GameMode.NON_INTERACTIVE) {
            playerName += " (бот)";
        }

        gameUI.updatePlayerDisplay(playerName, isHumanTurn);
    }

    /**
     * Обрабатывает событие истечения времени игры.
     * Завершает игру ничьей, останавливает все процессы и отображает сообщение.
     */
    public void handleTimeExpired() {
        if (!isGameActive) {
            return;
        }

        isGameActive = false;

        if (observerThread != null && observerThread.isAlive()) {
            observerThread.interrupt();
        }

        gameUI.showGameDialog("Время вышло!", "Игра завершена ничьей.");
    }

    /**
     * Обрабатывает сдачу игрока.
     * Завершает игру победой противника, останавливает все процессы
     * и отображает соответствующее сообщение.
     */
    public void resign() {
        if (!isGameActive) {
            return;
        }

        Main.PlayerColor winner = (humanPlayerColor == Main.PlayerColor.WHITE) ?
                Main.PlayerColor.BLACK : Main.PlayerColor.WHITE;

        endGame(getPlayerName(humanPlayerColor) + " сдались. " + getPlayerName(winner) + " побеждают!");
    }
}