package amazons;
import static amazons.Piece.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import static amazons.Utils.*;

/** The state of an Amazons Game.
 *  @author alberthan
 */
class Board {

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 10;

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        this.board = new Piece[10][10];
        this._turn = model._turn;
        this._winner = model._winner;
        this.movelist = model.movelist;
        for (int c = 0; c < SIZE; c++) {
            for (int r = 0; r < SIZE; r++) {
                this.board[c][r] = model.board[c][r];
            }
        }
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        board = new Piece[SIZE][SIZE];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                this.put(EMPTY, i, j);
            }
        }
        this.put(WHITE, 3, 0);
        this.put(WHITE, 6, 0);
        this.put(WHITE, 0, 3);
        this.put(WHITE, 9, 3);
        this.put(BLACK, 0, 6);
        this.put(BLACK, 9, 6);
        this.put(BLACK, 3, 9);
        this.put(BLACK, 6, 9);
        _turn = WHITE;
        _winner = EMPTY;
    }

    /**
     * Return the Piece whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the number of moves (that have not been undone) for this
     * board.
     */
    int numMoves() {
        return movelist.size();
    }

    /**
     * Return the winner in the current position, or null if the game is
     * not yet finished.
     */
    Piece winner() {
        if (_winner != EMPTY) {
            return _winner;
        } else {
            return null;
        }
    }

    /**
     * return the board.
     */
    public Piece[][] getboard() {
        return this.board;
    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return board[s.col()][s.row()];
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        return board[col][row];
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /**
     * Set square (COL, ROW) to P.
     */
    final void put(Piece p, int col, int row) {
        board[col][row] = p;
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /**
     * Return true iff FROM - TO is an unblocked queen move on the current
     * board, ignoring the contents of ASEMPTY, if it is encountered.
     * For this to be true, FROM-TO must be a queen move and the
     * squares along it, other than FROM and ASEMPTY, must be
     * empty. ASEMPTY may be null, in which case it has no effect.
     */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (to == null) {
            return false;
        }
        isLegal(from);
        if (to.col() > 9 || to.col() < 0 || to.row() > 9 || to.row() < 0) {
            return false;
        }
        int direction = from.direction(to);
        int verticallength = to.row() - from.row();
        int horizontallength = to.col() - from.col();
        int length = 0;
        if (from.isQueenMove(to)) {
            if (verticallength == 0 && to.col() > from.col()) {
                length = horizontallength;
            }
            if (verticallength == 0 && to.col() < from.col()) {
                length = horizontallength * -1;
            }
            if (horizontallength == 0 && to.row() > from.row()) {
                length = verticallength;
            }
            if (horizontallength == 0 && to.row() < from.row()) {
                length = verticallength * -1;
            }
            if (horizontallength == verticallength && to.col() > from.col()) {
                length = horizontallength;
            }
            if (horizontallength == verticallength && to.col() < from.col()) {
                length = horizontallength * -1;
            }
            if (horizontallength == verticallength * -1
                    && to.col() > from.col()) {
                length = horizontallength;
            }
            if (horizontallength == verticallength * -1
                    && to.col() < from.col()) {
                length = horizontallength * -1;
            }
            for (int i = 1; i <= length; i++) {
                int col = from.queenMove(direction, i).col();
                int row = from.queenMove(direction, i).row();
                Square a = from.queenMove(direction, i);
                if (asEmpty == null) {
                    if (board[col][row] != EMPTY) {
                        return false;
                    }
                } else if (asEmpty != null) {
                    if (board[col][row] != EMPTY
                            && a.index() != asEmpty.index()) {
                        return false;
                    }
                }
            }
            return true;

        } else {
            return false;
        }
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        return get(from) == turn();
    }

    /**
     * Return true iff FROM-TO is a valid first part of move, ignoring
     * spear throwing.
     */
    boolean isLegal(Square from, Square to) {
        if (!isLegal(from) || !isUnblockedMove(from, to, null)) {
            return false;
        }
        return true;
    }

    /**
     * Return true iff FROM-TO(SPEAR) is a legal move in the current
     * position.
     */
    boolean isLegal(Square from, Square to, Square spear) {
        if (!isLegal(from, to)) {
            return false;
        }
        return isUnblockedMove(to, spear, from);
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        if (move == null) {
            return false;
        }
        Square from = move.from();
        Square to = move.to();
        Square spearto = move.spear();
        return isLegal(from, to, spearto);
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a legal move.
     */
    void makeMove(Square from, Square to, Square spear) {
        movelist.add(Move.mv(from, to, spear));
        put(get(from), to);
        put(EMPTY, from);
        put(SPEAR, spear);
        _turn = _turn.opponent();
        if (!legalMoves(_turn).hasNext()) {
            _winner = _turn.opponent();
        }
    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        if (movelist.size() > 0) {
            Move lastmove = movelist.get(movelist.size() - 1);
            put(EMPTY, lastmove.spear());
            put(get(lastmove.to()), lastmove.from());
            put(EMPTY, lastmove.to());
            _turn = _turn.opponent();
            movelist.remove(movelist.size() - 1);
        }
    }

    /**
     * Return an Iterator over the Squares that are reachable by an
     * unblocked queen move from FROM. Does not pay attention to what
     * piece (if any) is on FROM, nor to whether the game is finished.
     * Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     * feature is useful when looking for Moves, because after moving a
     * piece, one wants to treat the Square it came from as empty for
     * purposes of spear throwing.)
     */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /**
     * Return an Iterator over all legal moves on the current board.
     */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /**
     * Return an Iterator over all legal moves on the current board for
     * SIDE (regardless of whose turn it is).
     */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /**
     * An iterator used by reachableFrom.
     */
    private class ReachableFromIterator implements Iterator<Square> {

        /**
         * Iterator of all squares reachable by queen move from FROM,
         * treating ASEMPTY as empty.
         */
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
            if (hasNext()) {
                Square result = _from.queenMove(_dir, _steps);
                toNext();
                return result;
            } else {
                throw error("No next value.");
            }

        }

        /**
         * Advance _dir and _steps, so that the next valid Square is
         * _steps steps in direction _dir from _from.
         */
        private void toNext() {
            if (_dir == -1) {
                _dir += 1;
            }
            if (isUnblockedMove(_from,
                    _from.queenMove(_dir, _steps + 1), _asEmpty)) {
                _steps += 1;
            } else {
                _dir += 1;
                _steps = 1;
                while (!isUnblockedMove(_from,
                        _from.queenMove(_dir, _steps), _asEmpty)) {
                    _dir += 1;
                    if (_dir >= 8) {
                        break;
                    }
                }
            }
        }

        /**
         * Starting square.
         */
        private Square _from;
        /**
         * Current direction.
         */
        private int _dir;
        /**
         * Current distance.
         */
        private int _steps;
        /**
         * Square treated as empty.
         */
        private Square _asEmpty;
    }


    /** An iterator used by legalMoves. */
    /**
     * An iterator used by legalMoves.
     */
    private class LegalMoveIterator implements Iterator<Move> {
        /**
         * All legal moves for SIDE (WHITE or BLACK).
         */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            toNext();
        }


        @Override
        public boolean hasNext() {
            return _startingSquares.hasNext() || _spearThrows.hasNext();
        }

        @Override
        public Move next() {
            if (hasNext()) {
                Move movement = Move.mv(_start,
                        _nextSquare, _spearThrows.next());
                toNext();
                return movement;
            } else {
                throw error("no more next value");
            }

        }

        /**
         * Advance so that the next valid Move is
         * _start-_nextSquare(sp), where sp is the next value of
         * _spearThrows.
         */
        private void toNext() {
            if (_spearThrows.hasNext()) {
                return;
            } else if (_pieceMoves.hasNext()) {
                _nextSquare = _pieceMoves.next();
                _spearThrows = reachableFrom(_nextSquare, _start);
            } else {
                while (_startingSquares.hasNext()) {
                    _start = _startingSquares.next();
                    Piece now = get(_start);
                    if (now == _fromPiece) {
                        _pieceMoves = reachableFrom(_start, null);
                        break;
                    }
                }
                if (!_pieceMoves.hasNext()) {
                    if (_startingSquares.hasNext()) {
                        toNext();
                    }
                    return;
                }
                _nextSquare = _pieceMoves.next();
                _spearThrows = reachableFrom(_nextSquare, _start);
            }
        }
        /**
         * Color of side whose moves we are iterating.
         */
        private Piece _fromPiece;
        /**
         * Current starting square.
         */
        private Square _start;
        /**
         * Remaining starting squares to consider.
         */
        private Iterator<Square> _startingSquares;
        /**
         * Current piece's new position.
         */
        private Square _nextSquare;
        /**
         * Remaining moves from _start to consider.
         */
        private Iterator<Square> _pieceMoves;
        /**
         * Remaining spear throws from _piece to consider.
         */
        private Iterator<Square> _spearThrows;
    }


    @Override
    public String toString() {
        String stringboard = "   ";
        for (int i = 9; i > 0; i--) {
            for (int j = 0; j < 10; j++) {
                if (i == 0) {
                    stringboard += board[j][i] + "\n";
                } else if (j == 9) {
                    stringboard += board[j][i] + "\n   ";
                    break;
                } else {
                    stringboard += board[j][i] + " ";
                }
            }
        }
        for (int j = 0; j < 9; j++) {
            stringboard += board[j][0] + " ";
        }
        stringboard += board[9][0];
        stringboard += "\n";
        return stringboard;
    }


    /**
     * An empty iterator for initialization.
     */
    private static final Iterator<Square> NO_SQUARES =
            Collections.emptyIterator();

    /**
     * Piece whose turn it is (BLACK or WHITE).
     */
    private Piece _turn;
    /**
     * Cached value of winner on this board, or EMPTY if it has not been
     * computed.
     */
    private Piece _winner;
    /**
     * generate the board.
     */
    private Piece[][] board;
    /**
     * lastmove.*/
    private ArrayList<Move> movelist = new ArrayList<>();

}
