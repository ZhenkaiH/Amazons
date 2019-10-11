package amazons;

import ucb.gui2.Pad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.IOException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import static amazons.Piece.*;
import static amazons.Square.sq;
import static amazons.Move.mv;


/** A widget that displays an Amazons game.
 *  @author Zhenkai Han
 */
class BoardWidget extends Pad {

    /* Parameters controlling sizes, speeds, colors, and fonts. */

    /** Colors of empty squares and grid lines. */
    static final Color
        SPEAR_COLOR = new Color(64, 64, 64),
        LIGHT_SQUARE_COLOR = new Color(238, 207, 161),
        DARK_SQUARE_COLOR = new Color(205, 133, 63),
        WHITE_COLOR = new Color(255, 255, 255);

    /** Locations of images of white and black queens. */
    private static final String
        WHITE_QUEEN_IMAGE = "wq4.png",
        BLACK_QUEEN_IMAGE = "bq4.png",
        SPEAR_IMAGE = "sp.png";

    /** Size parameters. */
    private static final int
        SQUARE_SIDE = 30,
        BOARD_SIDE = SQUARE_SIDE * 10,
        BAR_SIDE = 20,
        SATAUS = 14;

    /** A graphical representation of an Amazons board that sends commands
     *  derived from mouse clicks to COMMANDS.  */
    BoardWidget(ArrayBlockingQueue<String> commands) {
        _mode = "Human vs. Human";
        _commands = commands;
        setMouseHandler("click", this::mouseClicked);
        setPreferredSize(BOARD_SIDE, BOARD_SIDE + BAR_SIDE);
        _clicks = new ArrayList<>();
        try {
            _whiteQueen = ImageIO.read(Utils.getResource(WHITE_QUEEN_IMAGE));
            _blackQueen = ImageIO.read(Utils.getResource(BLACK_QUEEN_IMAGE));
            _spear = ImageIO.read(Utils.getResource(SPEAR_IMAGE));
        } catch (IOException excp) {
            System.err.println("Could not read queen images.");
            System.exit(1);
        }
        _acceptingMoves = false;
    }

    /** Draw the bare board G.  */
    private void drawGrid(Graphics2D g) {
        g.setColor(DARK_SQUARE_COLOR);
        g.fillRect(0, 0, BOARD_SIDE, BOARD_SIDE);
        g.setColor(LIGHT_SQUARE_COLOR);
        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                if (_clicks.contains(sq(x, Board.SIZE - 1 - y))) {
                    g.setColor(SPEAR_COLOR);
                    g.fillRect(SQUARE_SIDE * x, SQUARE_SIDE * y,
                            SQUARE_SIDE, SQUARE_SIDE);
                    g.setColor(LIGHT_SQUARE_COLOR);
                } else if ((x + y) % 2 == 0) {
                    g.fillRect(SQUARE_SIDE * x, SQUARE_SIDE * y,
                            SQUARE_SIDE, SQUARE_SIDE);
                }
            }
        }
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        drawGrid(g);
        drawQueens(g);
        drawSpears(g);
        drawStatus(g);
        if (_board.winner() != null) {
            markWin(g);
        }
    }

    /** Indicate that winner G. */
    private void drawStatus(Graphics2D g) {
        g.setColor(WHITE_COLOR);
        g.fillRect(0, BOARD_SIDE, BOARD_SIDE,
                BOARD_SIDE + 5 * BAR_SIDE);
        g.setFont(new Font("Arial", Font.BOLD, SATAUS));
        FontMetrics metrics = g.getFontMetrics();
        g.setColor(SPEAR_COLOR);
        String turn;
        if (_board.turn() == BLACK) {
            turn = "Black Turn";
        } else {
            turn = "White Turn";
        }
        int winX = BOARD_SIDE;
        int winY = BAR_SIDE / 2 + winX;
        String display = turn + "  " + _mode;
        g.drawString(display,
                (winX - metrics.stringWidth(display)) / 2,
                winY + (metrics.getMaxAscent()) / 4);
    }

    /** Indicate that winner G. */
    private void markWin(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics metrics = g.getFontMetrics();
        g.setColor(SPEAR_COLOR);
        String win;
        if (_board.winner() == BLACK) {
            win = "Black Win!";
        } else {
            win = "White Win!";
        }
        int winX = SQUARE_SIDE * 10;
        int winY = SQUARE_SIDE * 5;
        g.drawString(win,
                (winX - metrics.stringWidth(win)) / 2,
                winY + (metrics.getMaxAscent()) / 4);
    }

    /** Draw queens G. */
    private void drawQueens(Graphics2D g) {
        for (Square s : _board.queens()) {
            drawQueen(g, s, _board.get(s));
        }
    }

    /** Draw spears G. */
    private void drawSpears(Graphics2D g) {
        for (Square s : _board.spears()) {
            g.drawImage(_spear,
                    cx(s.col()) + 2, cy(s.row()) + 4, null);
        }
    }

    /** Draw a queen for side PIECE at square S on G.  */
    private void drawQueen(Graphics2D g, Square s, Piece piece) {
        g.drawImage(piece == WHITE ? _whiteQueen : _blackQueen,
                    cx(s.col()) + 2, cy(s.row()) + 4, null);
    }

    /** Handle a click on S. */
    private void click(Square s) {
        if (_board.queens().contains(s)
                && _clicks.size() == 0) {
            _clicks.add(s);
        } else if (_board.get(s) == EMPTY
                && _clicks.size() == 1) {
            _clicks.add(s);
        } else if ((_board.get(s) == EMPTY
                || _clicks.get(0) == s)
                && _clicks.size() == 2) {
            _clicks.add(s);
            Move move = mv(_clicks.get(0), _clicks.get(1), _clicks.get(2));
            _clicks.clear();
            if (_board.isLegal(move)) {
                _board.makeMove(move);
            }
        } else {
            _clicks.clear();
        }
        repaint();
    }

    /** Handle mouse click event E. */
    private synchronized void mouseClicked(String unused, MouseEvent e) {
        int xpos = e.getX(), ypos = e.getY();
        int x = xpos / SQUARE_SIDE,
            y = (BOARD_SIDE - ypos) / SQUARE_SIDE;
        if (_acceptingMoves
            && x >= 0 && x < Board.SIZE && y >= 0 && y < Board.SIZE) {
            click(sq(x, y));
        }
    }

    /** Revise the displayed board according to BOARD. */
    synchronized void update(Board board) {
        _board.copy(board);
        repaint();
    }

    /** Turn on move collection iff COLLECTING, and clear any current
     *  partial selection.   When move collection is off, ignore clicks on
     *  the board. */
    void setMoveCollection(boolean collecting) {
        _acceptingMoves = collecting;
        repaint();
    }

    /** Set MODE display word.*/
    void setModeWord(String mode) {
        _mode = mode;
    }

    /** Return x-pixel coordinate of the left corners of column X
     *  relative to the upper-left corner of the board. */
    private int cx(int x) {
        return x * SQUARE_SIDE;
    }

    /** Return y-pixel coordinate of the upper corners of row Y
     *  relative to the upper-left corner of the board. */
    private int cy(int y) {
        return (Board.SIZE - y - 1) * SQUARE_SIDE;
    }

    /** Return x-pixel coordinate of the left corner of S
     *  relative to the upper-left corner of the board. */
    private int cx(Square s) {
        return cx(s.col());
    }

    /** Return y-pixel coordinate of the upper corner of S
     *  relative to the upper-left corner of the board. */
    private int cy(Square s) {
        return cy(s.row());
    }

    /** Queue on which to post move commands (from mouse clicks). */
    private ArrayBlockingQueue<String> _commands;
    /** Board being displayed. */
    private final Board _board = new Board();

    /** Image of white queen. */
    private BufferedImage _whiteQueen;
    /** Image of black queen. */
    private BufferedImage _blackQueen;
    /** Image of spear. */
    private BufferedImage _spear;

    /** True iff accepting moves from user. */
    private boolean _acceptingMoves;

    /** Valid mouse clicks. */
    private ArrayList<Square> _clicks;

    /** Game Mode. */
    private String _mode;
}
