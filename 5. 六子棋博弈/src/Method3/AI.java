package Method3;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.Random;

// Method 3
public class AI extends core.player.AI {
    private int steps = 0;
    int id=0;
    private boolean flag = false;

    public Move findMove(Move opponentMove) {
        if (opponentMove == null) {
            Move move = this.firstMove();
            this.board.makeMove(move);
            return move;
        } else {
            this.board.makeMove(opponentMove);
            Random rand = new Random();

            int index1;
            int index2;
            id = 0;
            do {
                flag = false;
                if(id<10) {
                    do {
                        index1 = (rand.nextInt(13)+3)*19+(rand.nextInt(13)+3);
                        index2 = (rand.nextInt(13)+3)*19+(rand.nextInt(13)+3);
                    } while (index1 == index2);
                }
                else{
                    do {
                        index1 = rand.nextInt(361);
                        index2 = rand.nextInt(361);
                    } while(index1 == index2);
                }
                if (this.board.get(index1) != PieceColor.EMPTY || this.board.get(index2) != PieceColor.EMPTY){
                    flag = true;
                    id++;
                }
            } while(flag);

            Move move = new Move(index1, index2);
            this.board.makeMove(move);
            return move;
        }
    }

    public String name() {
        return "G03";
    }

    Board board = new Board();

    public Board setBoard(Board board) {
        return null;
    }

    public Board getBoard() {
        return null;
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new Board();
        steps = 0;
    }
}
