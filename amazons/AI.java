package amazons;

import java.util.ArrayList;

import static java.lang.Math.*;
import java.util.Iterator;
import static amazons.Piece.*;

/** A Player that automatically generates moves.
 *  @author Zhenkai Han
 */
class AI extends Player {

    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * A magnitude greater than a normal value.
     */
    private static final int FINAL = 35;

    /**
     * A magnitude greater than a normal value.
     */
    private static final int FINAL_TURN = 5;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        myStrategy();
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (sense == 1) {
            return  findMaxMove(board, depth, saveMove, sense, alpha, beta);
        } else {
            return  findMinMove(board, depth, saveMove, sense, alpha, beta);
        }
    }


    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1
     * and minimal value or value < ALPHA if SENSE==-1.
     * Searches up to DEPTH levels.
     */
    private int findMaxMove(Board board, int depth,
                             boolean saveMove, int sense, int alpha, int beta) {
        ArrayList<Move> myMvs = myPossibleMoves();
        int bestSoFar = -INFTY * sense;
        if ((depth == 0) || (board.winner() != null)) {
            bestSoFar = simpleFindMove(board, -1, alpha, beta);
        } else {
            for (Move m : myMvs) {
                board.makeAove(m);
                int response = findMove(board, depth - 1,
                        false, -1, alpha, beta);
                if (response >= bestSoFar) {
                    bestSoFar = response;
                    if (saveMove) {
                        _lastFoundMove = m;
                    }
                    alpha = max(alpha, bestSoFar);
                    if (beta <= alpha) {
                        board.undo();
                        break;
                    }
                }
                board.undo();
            }
        }
        return bestSoFar;
    }

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have minimal value or have value > BETA if SENSE==1
     * and minimal value or value < ALPHA if SENSE==-1.
     * Searches up to DEPTH levels.
     */
    private int findMinMove(Board board, int depth, boolean saveMove, int sense,
                            int alpha, int beta) {
        ArrayList<Move> myMvs = myPossibleMoves();
        int bestSoFar = -INFTY * sense;
        if ((depth == 0) || (board.winner() != null)) {
            bestSoFar = simpleFindMove(board, 1, alpha, beta);
        } else {
            for (Move m : myMvs) {
                board.makeAove(m);
                int response = findMove(board, depth - 1,
                        false, 1, alpha, beta);
                if (response <= bestSoFar) {
                    bestSoFar = response;
                    if (saveMove) {
                        _lastFoundMove = m;
                    }
                    beta = min(beta, bestSoFar);
                    if (beta <= alpha) {
                        board.undo();
                        break;
                    }
                }
                board.undo();
            }
        }
        return bestSoFar;
    }


    /** Searching at level 0 simply returns a static estimate
     * of the BOARD value. The move should have maximal value
     * or have value > BETA if SENSE ==1. BETA <= ALPHA*/
    private int simpleFindMove(Board board, int sense, int alpha, int beta) {
        if (sense == 1) {
            return simpleFindMaxMove(board, alpha, beta);
        } else {
            return simpleFindMinMove(board, alpha, beta);
        }
    }

    /** Searching at level 0 simply returns a static estimate
     * of the BOARD value. The move should have maximal value
     * or have value > BETA if SENSE ==1. BETA <= ALPHA*/
    private int simpleFindMaxMove(Board board, int alpha, int beta) {
        Piece winner = board.turn().opponent();
        ArrayList<Move> myMvs = myPossibleMoves();
        int bestSoFar;
        int nextValue;
        bestSoFar = -INFTY;
        if ((board.winner() != null) && (winner == WHITE)) {
            return INFTY;
        } else if ((board.winner() != null) && (winner == BLACK)) {
            return -INFTY;
        }
        for (Move m : myMvs) {
            board.makeAove(m);
            nextValue = staticScore(board);
            if (nextValue > bestSoFar) {
                bestSoFar = nextValue;
                alpha = max(alpha, nextValue);
                _lastFoundMove = m;
                if (beta <= alpha) {
                    board.undo();
                    break;
                }
            }
            board.undo();
        }
        return bestSoFar;
    }

    /** Searching at level 0 simply returns a static estimate
     * of the BOARD value. The move should have maximal value
     * or have value < ALPHA if SENSE == -1. BETA <= ALPHA*/
    private int simpleFindMinMove(Board board, int alpha, int beta) {
        Piece winner = board.turn().opponent();
        ArrayList<Move> myMvs = myPossibleMoves();
        int bestSoFar;
        int nextValue;
        bestSoFar = INFTY;
        if ((board.winner() != null) && (winner == WHITE)) {
            return INFTY;
        } else if ((board.winner() != null) && (winner == BLACK)) {
            return -INFTY;
        }
        for (Move m : myMvs) {
            board.makeAove(m);
            nextValue = staticScore(board);
            if (nextValue < bestSoFar) {
                bestSoFar = nextValue;
                beta = min(beta, nextValue);
                _lastFoundMove = m;
                if (beta <= alpha) {
                    board.undo();
                    break;
                }
            }
            board.undo();
        }
        return bestSoFar;
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private int maxDepth(Board board) {
        return _depth;
    }


    /** Return a heuristic value for BOARD.*/
    private int staticScore(Board board) {
        int myScore = 0;
        for (Square q : board.queens()) {
            if (board.get(q) == myPiece().opponent()) {
                Directions:
                for (int d = 0; d < 8; d++) {
                    for (int s = 1; s <= 5; s++) {
                        if (!q.existsMove(d, s)
                                || board.get(q.queenMove(d, s)) != EMPTY) {
                            continue Directions;
                        } else {
                            myScore++;
                        }
                    }
                }
            }
        }
        if (myPiece() == WHITE) {
            return myScore;
        } else {
            return -myScore;
        }
    }

    /** Return my possible moves list. */
    private ArrayList<Move> myPossibleMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        Iterator mvi = board().legalMoves(myPiece());
        while (mvi.hasNext()) {
            moves.add((Move) mvi.next());
        }
        if (moves.size() > FINAL) {
            _depth = 0;
        } else {
            _depth = FINAL_TURN;
        }
        return moves;
    }

    /** Return my possible moves list. */
    private void myStrategy() {
        int m = 0;
        Iterator mvi = board().legalMoves(myPiece());
        while (mvi.hasNext()) {
            mvi.next();
            m++;
            if (m > FINAL) {
                _depth = 0;
                return;
            }
        }
        _depth = FINAL_TURN;
    }

    /** Return my possible moves list. */
    private int _depth = 0;


}
