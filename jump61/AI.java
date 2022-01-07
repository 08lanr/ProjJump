package jump61;
import java.util.Random;

import static jump61.Side.*;

/** An automated Player.
 *  @author P. N. Hilfinger
 */
class AI extends Player {

    /**
     * A new player of GAME initially COLOR that chooses moves automatically.
     * SEED provides a random-number seed used for choosing moves.
     */
    AI(Game game, Side color, long seed) {
        super(game, color);
        _random = new Random(seed);
    }

    @Override
    String getMove() {
        Board board = getGame().getBoard();
        assert getSide() == board.whoseMove();
        int choice = searchForMove();
        getGame().reportMove(board.row(choice), board.col(choice));
        return String.format("%d %d", board.row(choice), board.col(choice));
    }

    /**
     * Return a move after searching the game tree to DEPTH>0 moves
     * from the current position. Assumes the game is not over.
     */
    private int searchForMove() {
        Board work = new Board(getBoard());
        assert getSide() == work.whoseMove();
        _foundMove = -1;
        if (getSide() == RED) {
            minMax(work, 3, true, 1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
        } else {
            minMax(work, 3, true, -1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        return _foundMove;
    }


    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _foundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _foundMove. If the game is over
     * on BOARD, does not set _foundMove.
     */
    private int minMax(Board board, int depth, boolean saveMove,
                       int sense, int alpha, int beta) {
        int n = 0;
        int best = alpha;
        Side side;
        if (sense == 1) {
            side = RED;
        } else {
            side = BLUE;
            best = beta;
        }
        if (depth == 0 | board.getWinner() != null) {
            int val = staticEval(board, Integer.MAX_VALUE);
            return val;
        } else {
            for (int i = 0; i < board.size() * board.size(); i++) {
                if (board.isLegal(side, i)) {
                    board.addSpot(side, i);
                } else {
                    continue;
                }
                Board b = new Board(board);
                board.undo();
                if (_foundMove == -1) {
                    _foundMove = i;
                }
                int bluemove = minMax(b, depth - 1,
                        false, -1 * sense, alpha, beta);
                if (bluemove > best && sense == 1
                        || bluemove < best && sense == -1) {
                    if (saveMove) {
                        _foundMove = i;
                    }
                    best = bluemove;
                }
                if (sense == 1) {
                    alpha = Math.max(alpha, best);
                } else if (sense == -1) {
                    beta = Math.min(beta, best);
                }
                if (alpha >= beta) {
                    return best;
                }
            }
        }
        return best;
    }

    /**
     * Return a heuristic estimate of the value of board position B.
     * Use WINNINGVALUE to indicate a win for Red and -WINNINGVALUE to
     * indicate a win for Blue.
     */
    private int staticEval(Board b, int winningValue) {
        int value = 0;
        if (b.getWinner() == BLUE) {
            return -1 * winningValue;
        } else if (b.getWinner() == (RED)) {
            return winningValue;
        } else {
            for (int i = 0; i < (b.size() * b.size()); i++) {
                if (b.get(i).getSide().equals(RED)) {
                    if (b.get(i).getSpots() >= b.neighbors(i)) {
                        value += 10;
                    }
                    value += 5;
                } else if (b.get(i).getSide().equals(BLUE)) {
                    if (b.get(i).getSpots() >= b.neighbors(i)) {
                        value += 10;
                    }
                    value += 5;
                }
            }
            if (b.getWinner() == BLUE) {
                value *= -1;
            }
            return value;
        }
    }

    /** A random-number generator used for move selection. */
    private Random _random;

    /** Used to convey moves discovered by minMax. */
    private int _foundMove;
}
