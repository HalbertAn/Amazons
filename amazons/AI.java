package amazons;
import java.util.Iterator;
import static amazons.Piece.*;

/** A Player that automatically generates moves.
 *  @author alberthan
 */
class AI extends Player {

    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
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

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }

        if (sense == 1) {
            int score = Integer.MIN_VALUE;
            Iterator<Move> wmoves = board.legalMoves(WHITE);
            while (wmoves.hasNext()) {
                Move wmove = wmoves.next();
                board.makeMove(wmove);
                int curscore = findMove(board,
                        depth - 1, false, -1, alpha, beta);
                score = Math.max(score, curscore);
                if (alpha >= beta) {
                    break;
                }
                alpha = Math.max(alpha, curscore);
                board.undo();
                if (alpha <= curscore && saveMove) {
                    _lastFoundMove = wmove;
                }
            }
            return score;
        } else if (sense == -1) {
            int score = Integer.MAX_VALUE;
            Iterator<Move> bmoves = board.legalMoves(BLACK);
            while (bmoves.hasNext()) {
                Move bmove = bmoves.next();
                board.makeMove(bmove);
                int curscore = findMove(board,
                        depth - 1, false, 1, alpha, beta);
                score = Math.min(score, curscore);
                if (alpha >= beta) {
                    break;
                }
                beta = Math.min(beta, curscore);
                board.undo();
                if (beta >= curscore && saveMove) {
                    _lastFoundMove = bmove;
                }
                return score;
            }
        }
        return 0;
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private int maxDepth(Board board) {
        int N = board.numMoves();
        if (N < 3 * 10) {
            return 1;
        } else if (N < 5 * 10 + 5) {
            return 2;
        } else if (N < 7 * 10 + 5) {
            return 4;
        } else {
            return 5;
        }
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        }
        Iterator<Move> whiteIterator = board.legalMoves(WHITE);
        int wmoves = 0;
        while (whiteIterator.hasNext()) {
            whiteIterator.next();
            wmoves++;
        }
        Iterator<Move> blackIterator = board.legalMoves(BLACK);
        int blackmoves = 0;
        while (blackIterator.hasNext()) {
            blackIterator.next();
            blackmoves++;
        }
        return wmoves -  blackmoves;
    }

}

