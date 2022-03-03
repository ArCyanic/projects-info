package Method2;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.ArrayList;
import java.util.Random;

import g04.player.Direction;
import g04.player.util;

// Method 2
public class AI extends core.player.AI {
    private int steps = 0;
    public Move findMove(Move opponentMove) {
        //����ǵ�һ�����ӿ��ֿ��н���firstMove
        if (opponentMove == null) {
            Move move = this.firstMove();
            this.board.makeMove(move);
            return move;
        }
        else {
            this.board.makeMove(opponentMove);
            //��һ���������ѡȡ
            Random rand = new Random();
            int index1;
            int index2;
            do {
                index1 = rand.nextInt(361);
            } while(this.board.get(index1) != PieceColor.EMPTY );

            //�ڶ�������
            ArrayList<Integer> legalPositions = new ArrayList<>();
            for(Direction direction : Direction.EIGHT_DIRECTIONS){
                if(util.validPosition(index1, direction, 1)){
                    legalPositions.add(util.transform(index1, direction, 1));
                }
            }

            if(legalPositions.isEmpty()) {
                //û�п��ߵ�λ�ã����ѡȡ
                do {
                    index2 = rand.nextInt(361);
                } while(this.board.get(index2) != PieceColor.EMPTY );
            }
            else{
                //�ڿ���λ�������ѡȡ
                Random random = new Random();
                int n = random.nextInt(legalPositions.size());
                index2 = legalPositions.get(n);
            }

            Move move = new Move(index1, index2);
            this.board.makeMove(move);
            return move;
        }
    }

    public String name() {
        return "G02";
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
