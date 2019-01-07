package amazons;

import org.junit.Test;
import static org.junit.Assert.*;
import ucb.junit.textui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** The suite of all JUnit tests for the enigma package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** A dummy test as a placeholder for real ones. */
    @Test
    public void dummyTest() {
        assertTrue("There are unit tests!", true);
    }

    @Test
    public void isUnblockedMoveTest() {
        Board a = new Board();
        buildBoard(a, TESTBOARD);
        assertTrue(a.isUnblockedMove(Square.sq(5, 4),
                Square.sq(8, 7), null));
        assertTrue(a.isUnblockedMove(Square.sq(8, 4),
                Square.sq(4, 4), Square.sq(5, 4)));
        assertFalse(a.isUnblockedMove(Square.sq(5, 4),
                Square.sq(2, 7), null));
        assertTrue(a.isUnblockedMove(Square.sq(5, 4),
                Square.sq(2, 4), Square.sq(3, 4)));
    }

    @Test
    public void isLegalTest() {
        Board a = new Board();
        buildBoard(a, TESTBOARD);
        assertTrue(a.isLegal(Square.sq(5, 4),
                Square.sq(7, 4), Square.sq(7, 6)));
        assertTrue(a.isLegal(Square.sq(5, 4),
                Square.sq(7, 4), Square.sq(5, 4)));
        assertFalse(a.isLegal(Square.sq(5, 4),
                Square.sq(5, 7), Square.sq(6, 7)));
        assertFalse(a.isLegal(Square.sq(5, 4),
                Square.sq(7, 6), Square.sq(7, 8)));
        assertFalse(a.isLegal(Square.sq(5, 4),
                Square.sq(7, 6), Square.sq(6, 6)));
    }

    @Test
    public void queenMoveTest() {
        Board a = new Board();
        buildBoard(a, TESTBOARD);
        Square b = Square.sq(5, 4);
        assertEquals(Square.sq(8, 4).index(),
                b.queenMove(2, 3).index());
        assertEquals(Square.sq(9, 8).index(),
                b.queenMove(1, 4).index());
    }

    @Test
    public void isQueenMoveTest() {
        Board a = new Board();
        buildBoard(a, TESTBOARD);
        Square b = Square.sq(5, 4);
        assertTrue(b.isQueenMove(Square.sq(3, 6)));
        assertFalse(b.isQueenMove(Square.sq(7, 3)));
    }

    @Test
    public void directionTest() {
        Board a = new Board();
        buildBoard(a, TESTBOARD);
        Square b = Square.sq(5, 4);
        assertEquals(b.direction(Square.sq(2, 1)), 5);
        assertEquals(b.direction(Square.sq(5, 6)), 0);
    }

    @Test
    public void testLegalMoves() {
        Board b = new Board();
        buildBoard(b, LEGALMOVESTESTBOARD);
        int numMoves = 0;
        Set<Move> moves = new HashSet<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.BLACK);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            numMoves += 1;
            moves.add(m);
        }
        assertEquals(4, numMoves);
        assertEquals(4, moves.size());
    }

    @Test
    public void testReachableFrom() {
        Board b = new Board();
        buildBoard(b, TESTBOARD);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom =
                b.reachableFrom(Square.sq(5, 4), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(REACHABLEFROMTESTSQUARES.contains
                    (Square.sq(s.col(), s.row())));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(REACHABLEFROMTESTSQUARES.size(), numSquares);
        assertEquals(REACHABLEFROMTESTSQUARES.size(), squares.size());
    }

    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - row - 1][col];
                b.put(piece, Square.sq(col, row));
            }
        }
        System.out.println(b);
    }
    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;

    static final Piece[][] TESTBOARD =
        {
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, W, W },
            { E, E, E, E, E, E, E, S, E, S },
            { E, E, E, S, S, S, S, E, E, S },
            { E, E, E, S, E, E, E, E, B, E },
            { E, E, E, S, E, W, E, E, B, E },
            { E, E, E, S, S, S, B, W, B, E },
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, E, E },
        };

    static final Piece[][] LEGALMOVESTESTBOARD =
        {
            { S, S, S, S, E, E, E, W, S, S },
            { S, S, S, E, E, E, E, S, S, B },
            { B, S, S, E, S, S, S, S, S, S },
            { B, S, S, E, S, W, S, E, S, S },
            { E, S, S, E, E, E, S, S, S, S },
            { E, S, E, S, E, S, E, S, S, W },
            { W, S, E, E, E, E, E, S, S, B },
            { E, E, E, E, E, E, E, S, S, S },
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, E, E },
        };

    static final Set<Square> REACHABLEFROMTESTSQUARES =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 5),
                    Square.sq(4, 5),
                    Square.sq(4, 4),
                    Square.sq(6, 4),
                    Square.sq(7, 4),
                    Square.sq(6, 5),
                    Square.sq(7, 6),
                    Square.sq(8, 7)));

}


