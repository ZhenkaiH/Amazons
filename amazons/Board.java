package amazons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Formatter;
import java.util.Collections;
import static amazons.Piece.*;
import static amazons.Move.mv;


/** The state of an Amazons Game.
 *  @author Zhenkai Han
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        } else {
            this._sq2Ps = new HashMap<>();
            for (Square s : model._sq2Ps.keySet()) {
                this._sq2Ps.put(Square.sq(s.col(), s.row()),
                        model._sq2Ps.get(s));
            }
            this._turn = model._turn;
            this._winner = model._winner;
            this._mvs = new ArrayList<>();
            this._mvs.addAll(model._mvs);
            this._queens = new ArrayList<>();
            this._queens.addAll(model._queens);
            this._spears = new ArrayList<>();
            this._spears.addAll(model._spears);
            this._numMoves = model._numMoves;
        }
    }

    /** Clears the board to the initial position. */
    void init() {
        _sq2Ps = new HashMap<>();
        _queens = new ArrayList<>();
        _spears = new ArrayList<>();
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                put(Piece.EMPTY, x, y);
            }
        }
        _sq2Ps.remove(Square.sq(0, 3));
        _sq2Ps.remove(Square.sq(3, 0));
        _sq2Ps.remove(Square.sq(6, 0));
        _sq2Ps.remove(Square.sq(9, 3));
        _sq2Ps.remove(Square.sq(0, 6));
        _sq2Ps.remove(Square.sq(3, 9));
        _sq2Ps.remove(Square.sq(6, 9));
        _sq2Ps.remove(Square.sq(9, 6));
        put(Piece.WHITE, 0, 3);
        put(Piece.WHITE, 3, 0);
        put(Piece.WHITE, 6, 0);
        put(Piece.WHITE, 9, 3);
        put(Piece.BLACK, 0, 6);
        put(Piece.BLACK, 3, 9);
        put(Piece.BLACK, 6, 9);
        put(Piece.BLACK, 9, 6);
        _turn = WHITE;
        _winner = EMPTY;
        _mvs = new ArrayList<>();
        _numMoves = 0;
    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return _numMoves;
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        for (Square q : queens()) {
            if (get(q) == _turn) {
                for (int d = 0; d <= 7; d++) {
                    if (q.existsMove(d, 1)
                            && _sq2Ps.get(q.queenMove(d, 1)) == EMPTY) {
                        return null;
                    }
                }
            }
        }
        _winner = _turn.opponent();
        return _winner;
    }

    /** Return whether P has no move. */
    boolean hasNoMove(Piece p) {
        for (Square q : queens()) {
            if (get(q) == p) {
                for (int d = 0; d <= 7; d++) {
                    if (q.existsMove(d, 1)
                            && _sq2Ps.get(q.queenMove(d, 1)) == EMPTY) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return _sq2Ps.get(s);
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _sq2Ps.get(Square.sq(col, row));
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Return the squares list of the board. */
    final HashMap<Square, Piece> getPiece() {
        return _sq2Ps;
    }

    /** Return the queen squares list of the board. */
    final List<Square> queens() {
        return _queens;
    }

    /** Return the queen squares list of the board. */
    final List<Square> spears() {
        return _spears;
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        _sq2Ps.put(Square.sq(col, row), p);
        if (p == WHITE || p == BLACK) {
            _queens.add(Square.sq(col, row));
        } else if (p == SPEAR) {
            _spears.add(Square.sq(col, row));
        }
        _winner = EMPTY;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (!from.isQueenMove(to)) {
            return false;
        }
        int d = from.direction(to);
        int dCol = to.col() - from.col();
        int dRow = to.row() - from.row();
        for (int s = 1; s <= Math.max(Math.abs(dCol), Math.abs(dRow)); s++) {
            Square way = from.queenMove(d, s);
            Piece woKao = this._sq2Ps.get(way);
            if (!Square.exists(way.col(), way.row())
                    || (woKao != Piece.EMPTY && way != asEmpty)) {
                return false;
            }
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return Square.exists(from.col(), from.row())
                && _turn == _sq2Ps.get(from);
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        return isLegal(from) && isUnblockedMove(from, to, null);
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        return isLegal(from, to) && isUnblockedMove(to, spear, from);

    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        makeMove(Move.mv(from, to, spear));
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        assert isLegal(move);
        _mvs.add(move);
        _numMoves++;
        _sq2Ps.remove(move.from());
        _sq2Ps.put(move.from(), Piece.EMPTY);
        _queens.remove(move.from());
        _sq2Ps.remove(move.to());
        _sq2Ps.put(move.to(), _turn);
        _queens.add(move.to());
        _sq2Ps.remove(move.spear());
        _sq2Ps.put(move.spear(), Piece.SPEAR);
        _spears.add(move.spear());
        _turn = _turn.opponent();
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeAove(Move move) {
        _mvs.add(move);
        _numMoves++;
        _sq2Ps.remove(move.from());
        _sq2Ps.put(move.from(), Piece.EMPTY);
        _queens.remove(move.from());
        _sq2Ps.remove(move.to());
        _sq2Ps.put(move.to(), _turn);
        _queens.add(move.to());
        _sq2Ps.remove(move.spear());
        _sq2Ps.put(move.spear(), Piece.SPEAR);
        _spears.add(move.spear());
        _turn = _turn.opponent();
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        Move u = _mvs.remove(_numMoves - 1);
        _numMoves--;
        _turn = _turn.opponent();
        _sq2Ps.remove(u.spear());
        _sq2Ps.put(u.spear(), Piece.EMPTY);
        _spears.remove(u.spear());
        _sq2Ps.remove(u.to());
        _sq2Ps.put(u.to(), Piece.EMPTY);
        _queens.remove(u.to());
        _sq2Ps.remove(u.from());
        _sq2Ps.put(u.from(), _turn);
        _queens.add(u.from());
    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = -1;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            Square next = _from.queenMove(_dir, _steps);
            toNext();
            return next;
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            if (_dir < 0) {
                _dir = 0;
            }
            _steps++;
            while (hasNext()) {
                Square to = null;
                if (_from.existsMove(_dir, _steps)) {
                    to = _from.queenMove(_dir, _steps);
                }
                if (to == null
                    || !isUnblockedMove(_from, to, _asEmpty)) {
                    _dir++;
                    _steps = 1;
                } else {
                    break;
                }
            }
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            s = 0;
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _start = null;
            _fromPiece = side;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return s < Square.SQUARES.length;
        }

        @Override
        public Move next() {
            Move next = mv(_start, _nextSquare, _sp);
            toNext();
            return next;
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            if (_start == null) {
                findNextStart();
            } else {
                findNextSp();
            }
        }

        /** Advance to the next starting square. */
        private void findNextStart() {
            while (hasNext()) {
                _start = Square.SQUARES[s];
                s++;
                if (_sq2Ps.get(_start) == _fromPiece) {
                    findNextTo();
                    break;
                }
            }
        }

        /** Advance to the Current piece's next TO position. */
        private void findNextTo() {
            if (_pieceMoves == NO_SQUARES) {
                _pieceMoves = new ReachableFromIterator(_start, null);
            }
            if (_pieceMoves.hasNext()) {
                _nextSquare = _pieceMoves.next();
                findNextSp();
            } else {
                _pieceMoves = NO_SQUARES;
                findNextStart();
            }
        }

        /** Advance to the Current piece's
         * current position's next spear. */
        private void findNextSp() {
            if (_spearThrows == NO_SQUARES) {
                _spearThrows = new ReachableFromIterator(_nextSquare, _start);
            }
            if (_spearThrows.hasNext()) {
                _sp = _spearThrows.next();
            } else {
                _spearThrows = NO_SQUARES;
                findNextTo();
            }
        }

        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Current piece's new position's new spear. */
        private Square _sp;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
        /** Current index of all squares. */
        private int s;
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        for (int y = SIZE - 1; y >= 0; y--) {
            out.format("   ");
            for (int x = 0; x <= SIZE - 2; x++) {
                out.format(_sq2Ps.get(Square.sq(x, y)).toSymbol() + " ");
            }
            out.format(_sq2Ps.get(Square.sq(SIZE - 1, y)).toSymbol());
            out.format("\n");
        }
        return out.toString();
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;

    /** Number of moves. */
    private int _numMoves;

    /** Pieces of this Board. */
    private HashMap<Square, Piece> _sq2Ps;

    /** Store all moves. */
    private ArrayList<Move> _mvs;

    /** Store all queens. */
    private ArrayList<Square> _queens;

    /** Store all spears. */
    private ArrayList<Square> _spears;
}
