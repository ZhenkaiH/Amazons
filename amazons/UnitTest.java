package amazons;

import org.junit.Test;
import static org.junit.Assert.*;
import ucb.junit.textui;

import java.util.Iterator;

/** The suite of all JUnit tests for the enigma package.
 *  @author Zhenkai Han
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
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(Piece.BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), Piece.BLACK);
        b.put(Piece.WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), Piece.WHITE);
        b.put(Piece.EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), Piece.EMPTY);
    }

    /** Tests proper identification of legal/illegal queen moves. */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(3, 8).isQueenMove(Square.sq(2, 6)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /** Tests toString for initial board state and a smiling board state. :) */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }


    private void makeSmile(Board b) {
        b.put(Piece.EMPTY, Square.sq(0, 3));
        b.put(Piece.EMPTY, Square.sq(0, 6));
        b.put(Piece.EMPTY, Square.sq(9, 3));
        b.put(Piece.EMPTY, Square.sq(9, 6));
        b.put(Piece.EMPTY, Square.sq(3, 0));
        b.put(Piece.EMPTY, Square.sq(3, 9));
        b.put(Piece.EMPTY, Square.sq(6, 0));
        b.put(Piece.EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(Piece.SPEAR, Square.sq(col, row));
            }
        }
        b.put(Piece.EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(Piece.SPEAR, Square.sq(col, row));
            }
        }
        b.put(Piece.EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(Piece.WHITE, Square.sq(lip, 2));
        }
        b.put(Piece.WHITE, Square.sq(2, 3));
        b.put(Piece.WHITE, Square.sq(7, 3));
    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";

    @Test
    public void squareTest() {
        assertEquals("Wrong square index", 0, Square.sq(0, 0).index());
        assertEquals("Wrong square index", 1, Square.sq(1, 0).index());
        assertEquals("Wrong square index", 9, Square.sq(9, 0).index());
        assertEquals("Wrong square index", 10, Square.sq(0, 1).index());
        assertEquals("Wrong square index", 18, Square.sq(8, 1).index());
        assertEquals("Wrong square index", 99, Square.sq(9, 9).index());
        assertFalse("Exists test failed!", Square.exists(10, 10));
        assertEquals("str!", "a1", Square.sq(0, 0).toString());
        assertEquals("str!", "b1", Square.sq(1, 0).toString());
        assertEquals("str!", "a6", Square.sq(0, 5).toString());
        assertEquals("str!", "c2", Square.sq(2, 1).toString());
        assertEquals("str!", "j10", Square.sq(9, 9).toString());
        assertEquals("str!", "a10", Square.sq(0, 9).toString());
        Square a = Square.sq(2, 2);
        assertFalse("isQueenMove test failed!", a.isQueenMove(Square.sq(3, 4)));
        assertFalse("isQueenMove test failed!", a.isQueenMove(Square.sq(3, 8)));
        assertFalse("isQueenMove test failed!", a.isQueenMove(Square.sq(1, 0)));
        assertFalse("isQueenMove test failed!", a.isQueenMove(Square.sq(8, 9)));
        assertFalse("isQueenMove test failed!", a.isQueenMove(Square.sq(2, 2)));
        assertTrue("isQueenMove test failed!", a.isQueenMove(Square.sq(9, 9)));
        assertTrue("isQueenMove test failed!", a.isQueenMove(Square.sq(1, 1)));
        assertTrue("isQueenMove test failed!", a.isQueenMove(Square.sq(2, 3)));
        assertTrue("isQueenMove test failed!", a.isQueenMove(Square.sq(3, 2)));
        assertTrue("isQueenMove test failed!", a.isQueenMove(Square.sq(0, 2)));
        assertEquals("queenMove!", Square.sq(9, 9), a.queenMove(1, 7));
        assertEquals("queenMove!", Square.sq(1, 1), a.queenMove(5, 1));
        assertEquals("queenMove!", null, a.queenMove(9, 1));
        assertEquals("direction!", 0, a.direction(Square.sq(2, 9)));
        assertEquals("direction!", 1, a.direction(Square.sq(9, 9)));
        assertEquals("direction!", 2, a.direction(Square.sq(9, 2)));
        assertEquals("direction!", 3, a.direction(Square.sq(4, 0)));
        assertEquals("sq one str", Square.sq(0, 0), Square.sq("a1"));
        assertEquals("sq one str", Square.sq(0, 9), Square.sq("a10"));
        assertEquals("sq one str", Square.sq(2, 1), Square.sq("c2"));
        assertEquals("sq one str", Square.sq(9, 6), Square.sq("j7"));
        assertEquals("sq one str", Square.sq(9, 9), Square.sq("j10"));
    }

    @Test
    public void boardWinnerTest() {
        Board b = new Board();
        Move m1 = Move.mv("a4-b3(b10)");
        Move m2 = Move.mv("a7-a8(a9)");
        Move m3 = Move.mv("b3-b4(b9)");
        b.makeMove(m1);
        b.makeMove(m2);
        b.makeMove(m3);
        assertEquals("move!", null, b.winner());

    }

    @Test
    public void boardInitMoveTest() {
        Board b = new Board();
        assertEquals("init!", Piece.WHITE, b.get(0, 3));
        assertEquals("init!", Piece.BLACK, b.get('a', '7'));
        assertEquals("init!", Piece.EMPTY, b.get(Square.sq("j10")));
        assertEquals("init!", Piece.EMPTY, b.get(0, 2));
        assertEquals("init!", Piece.WHITE, b.turn());

        Move m1 = Move.mv("d1 b1 d1");
        Move m2 = Move.mv("a7-a6(a7)");
        b.makeMove(m1);
        b.makeMove(m2);
        assertEquals("move!", Piece.SPEAR, b.get(Square.sq("a7")));
        assertEquals("move!", Piece.BLACK, b.get(Square.sq("a6")));
        assertEquals("move!", Piece.SPEAR, b.get(Square.sq("d1")));
        assertEquals("move!", Piece.WHITE, b.get(Square.sq("b1")));
        assertEquals("move!", 2, b.numMoves());
        assertEquals("move!", Piece.WHITE, b.turn());
    }

    @Test
    public void boardUndoTest() {
        Board b = new Board();
        Move m1 = Move.mv("d1 b1 d1");
        Move m2 = Move.mv("a7-a6(a7)");
        b.makeMove(m1);
        b.makeMove(m2);
        b.undo();
        assertEquals("move!", Piece.SPEAR, b.get(Square.sq("d1")));
        assertEquals("move!", Piece.WHITE, b.get(Square.sq("b1")));
        assertEquals("undo!", Piece.BLACK, b.get(Square.sq("a7")));
        assertEquals("undo!", Piece.EMPTY, b.get(Square.sq("a6")));
    }

    @Test
    public void boardCopyTest() {
        Board b = new Board();
        Board b2 = new Board(b);
        Move m1 = Move.mv("d1 b1 d1");
        Move m2 = Move.mv("a7-a6(a7)");
        b.makeMove(m1);
        b.makeMove(m2);
        Move m3 = Move.mv("d1-a1(b2)");
        b2.makeMove(m3);
        assertEquals("move!", Piece.SPEAR, b.get(Square.sq("a7")));
        assertEquals("move!", Piece.BLACK, b.get(Square.sq("a6")));
        assertEquals("move!", Piece.SPEAR, b.get(Square.sq("d1")));
        assertEquals("move!", Piece.WHITE, b.get(Square.sq("b1")));
        assertEquals("move!", Piece.EMPTY, b2.get(Square.sq("d1")));
        assertEquals("move!", Piece.WHITE, b2.get(Square.sq("a1")));
        assertEquals("move!", Piece.SPEAR, b2.get(Square.sq("b2")));
    }

    /** Warning:Line separator in Windows is \r\n, in Unix is \n **/
    private static final String INIT_BOARD =
            "   - - - B - - B - - -\n"
            + "   - - - - - - - - - -\n"
            + "   - - - - - - - - - -\n"
            + "   B - - - - - - - - B\n"
            + "   - - - - - - - - - -\n"
            + "   - - - - - - - - - -\n"
            + "   W - - - - - - - - W\n"
            + "   - - - - - - - - - -\n"
            + "   - - - - - - - - - -\n"
            + "   - - - W - - W - - -\n";

    private static final String M1M2_BOARD =
            "   - - - B - - B - - -\n"
            + "   - - - - - - - - - -\n"
            + "   - - - - - - - - - -\n"
            + "   S - - - - - - - - B\n"
            + "   B - - - - - - - - -\n"
            + "   - - - - - - - - - -\n"
            + "   W - - - - - - - - W\n"
            + "   - - - - - - - - - -\n"
            + "   - - - - - - - - - -\n"
            + "   - W - S - - W - - -\n";

    @Test
    public void boardToStringTest() {
        Board b = new Board();
        assertEquals("ToString!", INIT_BOARD, b.toString());
        Move m1 = Move.mv("d1 b1 d1");
        Move m2 = Move.mv("a7-a6(a7)");
        b.makeMove(m1);
        b.makeMove(m2);
        assertEquals("ToString!", M1M2_BOARD, b.toString());
    }

    @Test
    public void boardSingleSquareIteratorTest() {
        Board b = new Board();
        b.getPiece().put(Square.sq("e1"), Piece.SPEAR);
        b.getPiece().put(Square.sq("b1"), Piece.BLACK);
        Iterator d1 = b.reachableFrom(Square.sq("d1"), null);
        assertEquals("Reachable iterator!", Square.sq("d2"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("d3"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("d4"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("d5"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("d6"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("d7"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("d8"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("d9"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("e2"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("f3"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("g4"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("h5"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("i6"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("c1"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("c2"), d1.next());
        assertEquals("Reachable iterator!", Square.sq("b3"), d1.next());
        assertFalse("Reachable iterator!", d1.hasNext());
    }

    @Test
    public void boardSideSquaresIteratorTest() {
        Board b = new Board();
        b.getPiece().remove(Square.sq("a10"));
        b.getPiece().put(Square.sq("a10"), Piece.SPEAR);
        b.getPiece().put(Square.sq("b10"), Piece.SPEAR);
        b.getPiece().put(Square.sq("b9"), Piece.SPEAR);
        b.getPiece().put(Square.sq("b8"), Piece.SPEAR);
        b.getPiece().put(Square.sq("b7"), Piece.SPEAR);
        b.getPiece().put(Square.sq("b6"), Piece.SPEAR);
        b.getPiece().put(Square.sq("a6"), Piece.SPEAR);
        b.getPiece().put(Square.sq("j10"), Piece.SPEAR);
        b.getPiece().put(Square.sq("i10"), Piece.SPEAR);
        b.getPiece().put(Square.sq("i9"), Piece.SPEAR);
        b.getPiece().put(Square.sq("i8"), Piece.SPEAR);
        b.getPiece().put(Square.sq("i7"), Piece.SPEAR);
        b.getPiece().put(Square.sq("i6"), Piece.SPEAR);
        b.getPiece().put(Square.sq("j6"), Piece.SPEAR);
        assertEquals("fffffffff!", Piece.SPEAR, b.get(Square.sq(0, 9)));
        Iterator black = b.legalMoves(Piece.BLACK);
        assertFalse("Unblocked", b.isUnblockedMove(Square.sq("a8"),
                Square.sq("a10"), Square.sq("a7")));
        assertEquals("SS iterator!", Move.mv("a7-a8(a9)"), black.next());
        assertEquals("SS iterator!", Move.mv("a7-a8(a7)"), black.next());
        assertEquals("SS iterator!", Move.mv("a7-a9(a8)"), black.next());
        assertEquals("SS iterator!", Move.mv("a7-a9(a7)"), black.next());
        assertEquals("SS iterator!", Move.mv("j7-j8(j9)"), black.next());
        assertEquals("SS iterator!", Move.mv("j7-j8(j7)"), black.next());
        assertEquals("SS iterator!", Move.mv("j7-j9(j8)"), black.next());
        assertEquals("SS iterator!", Move.mv("j7-j9(j7)"), black.next());
        assertEquals("SS iterator!", Move.mv("d10-e10(f10)"), black.next());
    }

    @Test
    public void boardLegalMoveTest() {
        Board b = new Board();
        assertEquals("init!", Piece.WHITE, b.get(0, 3));
        assertEquals("init!", Piece.BLACK, b.get('a', '7'));
        assertEquals("init!", Piece.EMPTY, b.get(Square.sq("j10")));
        assertEquals("init!", Piece.EMPTY, b.get(0, 2));
        assertEquals("init!", Piece.WHITE, b.turn());

        Move m1 = Move.mv("d1 b1 d1");
        Move m2 = Move.mv("a7-a6(a7)");
        b.makeMove(m1);
        b.makeMove(m2);
        assertEquals("move!", null, b.winner());
        assertFalse("move!", b.isLegal(Move.mv(Square.sq("g1"),
                Square.sq("g9"), Square.sq("f7"))));

    }

    @Test
    public void superLegalMoveTest() {
        Board b = new Board();
        b.makeMove(Move.mv("d1-d9(g9)"));
        b.makeMove(Move.mv("g10-b5(b4)"));
        b.makeMove(Move.mv("d9-b7(d9)"));
        b.makeMove(Move.mv("a7-a5(c7)"));
        b.makeMove(Move.mv("b7-a8(a6)"));
        b.makeMove(Move.mv("d10-b8(b9)"));
        b.makeMove(Move.mv("g1-b6(b7)"));
        b.makeMove(Move.mv("j7-e2(e10)"));
        b.makeMove(Move.mv("j4-e4(e9)"));
        b.makeMove(Move.mv("b8-a9(b8)"));
        b.makeMove(Move.mv("e4-h4(e4)"));
        b.makeMove(Move.mv("b5-c6(c5)"));
        b.makeMove(Move.mv("h4-h6(d6)"));
        b.makeMove(Move.mv("c6-b5(c6)"));
        b.makeMove(Move.mv("h6-g5(b10)"));
        b.makeMove(Move.mv("a9-a10(a9)"));
        b.makeMove(Move.mv("a8-a7(a8)"));
        b.makeMove(Move.mv("e2-e3(b3)"));
        b.makeMove(Move.mv("g5-g8(c4)"));
        b.makeMove(Move.mv("e3-c1(a3)"));
        b.makeMove(Move.mv("g8-g4(f5)"));
        b.makeMove(Move.mv("c1-i1(e5)"));
        b.makeMove(Move.mv("g4-g2(g5)"));
        b.makeMove(Move.mv("i1-i4(h5)"));
        b.makeMove(Move.mv("g2-f2(i5)"));
        b.makeMove(Move.mv("i4-h3(e3)"));
        b.makeMove(Move.mv("f2-j2(j5)"));
        b.makeMove(Move.mv("h3-g2(e2)"));
        b.makeMove(Move.mv("j2-i1(e1)"));
        b.makeMove(Move.mv("g2-g3(i3)"));
        b.makeMove(Move.mv("i1-h2(h3)"));
        b.makeMove(Move.mv("g3-g1(g4)"));
        b.makeMove(Move.mv("h2-f4(i1)"));
        b.makeMove(Move.mv("g1-g3(h4)"));
        b.makeMove(Move.mv("f4-f2(i2)"));
        b.makeMove(Move.mv("g3-h2(f4)"));
        b.makeMove(Move.mv("f2-f1(h1)"));
        b.makeMove(Move.mv("h2-g1(f2)"));
        b.makeMove(Move.mv("f1-g2(h2)"));
    }

}


