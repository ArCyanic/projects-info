package g04.player;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.*;

public class AI extends core.player.AI {

    private RoadTable roadTable;
    private ExpandingSquare expandingSquare;

    //������������
    int w = 3;
    int MAX = 999999;
    int MIN = -999999;
    int depth = 0;//������ȣ����Ϊ����ʱ��Ҷ�ӽڵ㴦��MIN�㣬����ֵС��0��ż����ʱ��ҲҶ�ӽڵ㴦��MAX�㣬����simulate������������Ӧ��Ϊż��
    private int boardValue = 0;

    public Move findMove(Move opponentMove) {
        Move retult = findMove_1(opponentMove);
        return retult;
    }

    public Move findMove_1(Move opponentMove) {
        //Step 1
        if (opponentMove == null) {
            Move move = this.firstMove();
            makeMove_pure(move);
            return move;
        }
        makeMove_pure(opponentMove);
        //Step 2
        ArrayList<Integer> result = findWin(this.board.whoseMove());
        //�ж��Ƿ����ʤ��
        if (result.size() > 0) {
            //����ҵ���4��·, ��ôresult������������
            //����ҵ���5��·����ô��Ҫ��������һ���������
            if (result.size() == 1) {
                int temp = findOneRandom();
                while (result.get(0) == temp) {
                    temp = findOneRandom();
                }
                result.add(temp);
            }
        } else {//����ҷ�������ʤ�ţ������Step 3��Step 4
            //Step 3 �ж϶Է��Ƿ����ʤ��
            result = findBlock(this.board.whoseMove().opposite());
            if(result.size() == 1){
                int temp = findOneRandom();
                while (result.get(0) == temp) {
                    temp = findOneRandom();
                }
                result.add(temp);
            }
            else if(result.size() > 1){

            }
            else{
                ArrayList<Node> topNodes = new ArrayList<>();
                Iterator itr = expandingSquare.getQ().iterator();
                for (int i = 0; i < w; i++) {
                    if (itr.hasNext()) {
                        topNodes.add(((Node) itr.next()));
                    }
                }

                boardValue = roadTable.calPlayerValue(this.board.whoseMove()) - roadTable.calPlayerValue(this.board.whoseMove().opposite());
                //ȡ��ֵ����w���ڵ������ϲ����������ظ�Ϊ��ȷ�Ĺ�ֵ�����ѡ��ȷ��ֵ��ߵ��߲�
                PriorityQueue<MoveNode> moveNodes = new PriorityQueue<>();
                for(int i = 0;i < topNodes.size();i++){
                    for(int j = i;j < topNodes.size();j++){
                        if(i != j){
                            Move move = new Move(topNodes.get(i).getIndex(), topNodes.get(j).getIndex());
                            int value = alpha_beta(MIN, MAX, move, depth) - boardValue;
                            moveNodes.offer(new MoveNode(move, value));
                            undo(move);
                        }
                    }
                }
                Move result_move = moveNodes.poll().getMove();
                ArrayList<Integer> temp = new ArrayList<>();
                temp.add(result_move.index1());
                temp.add(result_move.index2());
                result = temp;
            }
        }

        if(result.get(0) == 180){
            int temp = findOneRandom();
            while (result.get(1) == temp) {
                temp = findOneRandom();
            }
            result.set(0, temp);
        }
        if(result.get(1) == 180){
            int temp = findOneRandom();
            while (result.get(0) == temp) {
                temp = findOneRandom();
            }
            result.set(1, temp);
        }
//        if(result.get(1) == result.get(0)){
//            int temp = findOneRandom();
//            while (result.get(0) == temp) {
//                temp = findOneRandom();
//            }
//            result.set(1, temp);
//        }

        Move move = new Move(result.get(0), result.get(1));
        makeMove(move);
        return move;
    }


    /**
     * 1. �ж��Ƿ��ǵ�һ�����������
     * 2. �ж��Ƿ����ʤ�ţ��������
     * 3. �ж϶Է��Ƿ����ʤ�ţ��������
     * 4. ����expandingCircle��Χ�����е�Ĺ�ֵ
     */
    public Move findMove_2(Move opponentMove) {
        if (opponentMove == null) {
            Move move = this.firstMove();
            makeMove_pure(move);
            return move;
        }
        makeMove_pure(opponentMove);

        ArrayList<Node> topNodes = new ArrayList<>();
        Iterator itr = expandingSquare.getQ().iterator();
        for (int i = 0; i < w; i++) {
            if (itr.hasNext()) {
                topNodes.add(((Node) itr.next()));
            }
        }

        boardValue = roadTable.calPlayerValue(this.board.whoseMove()) - roadTable.calPlayerValue(this.board.whoseMove().opposite());
        //ȡ��ֵ����w���ڵ������ϲ����������ظ�Ϊ��ȷ�Ĺ�ֵ�����ѡ��ȷ��ֵ��ߵ��߲�
        PriorityQueue<MoveNode> moveNodes = new PriorityQueue<>();
        for (int i = 0; i < topNodes.size(); i++) {
            for (int j = 0; j < topNodes.size(); j++) {
                if (i != j) {
                    Move move = new Move(topNodes.get(i).getIndex(), topNodes.get(j).getIndex());
                    int value = boardValue - alpha_beta(MIN, MAX, move, depth);
                    moveNodes.offer(new MoveNode(move, value));
                    undo(move);
                }
            }
        }

        Move move = moveNodes.poll().getMove();
        makeMove(move);
        return move;
    }

    /**
     * 1. �ж��Ƿ񵽴�Ҷ�ڵ㣬��û���������2������������һ��ģ�Ⲣ���ع�ֵ
     * 2. makeMove(move)
     * 3. �����ӽڵ㲢�ݹ�
     * 4. undo(move)�������ڶ�����ɵ�Ӱ��
     * 5. �����ݹ��ָ��ָ��ֳ�
     * ע����ʼ�ݹ���Ƚ�������Ϊż��
     *
     * @param alpha ����
     * @param beta  ����
     * @param move  ��ǰҪִ�е��߲�
     * @param depth �ݹ����
     * @return �ڵ��ֵ
     */
    public int alpha_beta(int alpha, int beta, Move move, int depth) {
        //Step 1
        if (depth == 0) {
            makeMove_noValue(move);
            return roadTable.V[this.board.whoseMove().opposite().ordinal()] - roadTable.V[this.board.whoseMove().ordinal()];
        }

        //Step 2
        makeMove_noValue(move);

        int bestValue = MIN;

        //Step 3.1 �����ӽڵ�, �ο�findMove2�е�ע��
        ArrayList<Node> topNodes = new ArrayList<>();
        Iterator itr = expandingSquare.getQ().iterator();
        for (int i = 0; i < w; i++) {
            if (itr.hasNext()) {
                topNodes.add(((Node) itr.next()));
            }
        }
        boolean breakIndicator = false;
        for (int i = 0; i < topNodes.size(); i++) {
            for (int j = i; j < topNodes.size(); j++) {
                if (i != j) {
                    //�ӽڵ�����
                    Move newMove = new Move(topNodes.get(i).getIndex(), topNodes.get(j).getIndex());
                    //Step 3.2
                    int value = alpha_beta(-beta, -alpha, newMove, depth - 1);
                    bestValue = Math.max(bestValue, -value);
                    alpha = Math.max(alpha, bestValue);
                    undo(newMove);
                    if (beta <= alpha) {
                        breakIndicator = true;
                        break;
                    }
                }
            }
            if (breakIndicator == true) break;
        }
        return bestValue;
    }

    /**
     * ����·����Ȧ
     *
     * @param move
     * @return
     */
    public int update(Move move) {
        int result = 0;
        result += roadTable.update(move.index1(), this.board.whoseMove());
        result += roadTable.update(move.index2(), this.board.whoseMove());
        expandingSquare.update(move.index1(), move.index2(), 'd', roadTable.summary);
        return result;
    }

    public void update_pure(Move move) {
        roadTable.update_pure(move.index1(), this.board.whoseMove());
        roadTable.update_pure(move.index2(), this.board.whoseMove());
        expandingSquare.update(move.index1(), move.index2(), 'd', roadTable.summary);
    }

    public void update_noValue(Move move) {
        roadTable.update_noValue(move.index1(), this.board.whoseMove());
        roadTable.update_noValue(move.index2(), this.board.whoseMove());
        expandingSquare.update(move.index1(), move.index2(), 'd', roadTable.summary);
    }

    /**
     * �ָ�·����Ȧ
     *
     * @param move
     */
    public void backdate(Move move) {
        roadTable.backdate();
        roadTable.backdate();
        expandingSquare.update(move.index1(), move.index2(), 'a', roadTable.summary);
    }

    /**
     * makeMove�ķ�����
     *
     * @param move
     */
    public void undo(Move move) {
        backdate(move);
        this.board.undo();
    }

    /**
     * ���²����壬��Ҫ����������Ϊ�����Ч��ɾ������Ȧ�ж�
     *
     * @param move
     */
    public void makeMove(Move move) {
        update(move);
        this.board.makeMove(move);
    }

    public void makeMove_noValue(Move move) {
        update_noValue(move);
        if(!this.board.legalMove(move)){
            System.out.println("error makeMove_noValue");
        }
        this.board.makeMove(move);
    }

    /**
     * makeMove�����棬��Ҫ����ȷ�����߲�
     *
     * @param move
     * @return
     */
    public void makeMove_pure(Move move) {
        update_pure(move);
        this.board.makeMove(move);
        if(expandingSquare.getQ().size() < expandingSquare.getLength() * expandingSquare.getLength() / 3) expandingSquare.expand(board, roadTable.summary);
    }

    //�������꣬����ҵ����ĸ��ӵ�·�򷵻��������꣬���ֻ�ҵ�������ӵ�·�򷵻�һ������
    public ArrayList<Integer> findWin(PieceColor color){
        //���巵�ض���
        ArrayList<Integer> result = new ArrayList<>();
        //��̬��ȡ��ͬ��ɫ��Ӧ�ı��
        ArrayList<HashMap<Integer, Road>> table;
        if(color == PieceColor.WHITE) table = this.roadTable.whiteTable;
        else table = this.roadTable.blackTable;

        //������������Ϊ4��5��Road
        for(int i = 4;i <= 5;i++){
            Iterator itr = table.get(i).keySet().iterator();
            //һ���ҵ�һ��ʤ�ţ���������̷���
            if(itr.hasNext()) {

                //��ȡRoad
                int hashCode = (int) itr.next();
                Road road = roadTable.summary[hashCode];

                //��·�ڿ�λ���������ʼλ�õ�ƫ����
                ArrayList<Integer> indexes = road.emptyIndexes();
                for(Integer index : indexes){
                    result.add(util.transform(road.getIndex(), road.getDirection(), index));
                }
                break;
            }
        }

        return result;
    }

    //���ڶԷ�ÿһ��4�ӻ�5��·��ֻ��һ��λ�á�
    public ArrayList<Integer> findBlock(PieceColor color) {
        //���巵�ض���
        ArrayList<Integer> result = new ArrayList<>();
        //��̬��ȡ��ͬ��ɫ��Ӧ�ı��
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = this.roadTable.whiteTable;
        else table = this.roadTable.blackTable;

        //������������Ϊ4��5��Road
        boolean breakIndicator = false;
        for (int i = 4; i < 5; i++) {
            Iterator itr = table.get(i).keySet().iterator();
            while(itr.hasNext()){
                //��ȡRoad
                int hashCode = (int) itr.next();
                Road road = roadTable.summary[hashCode];
                //��·�ڿ�λ���������ʼλ�õ�ƫ����
                ArrayList<Integer> indexes = road.emptyIndexes();

                int index;
                if(i == 4){
                    index = util.transform(road.getIndex(), road.getDirection(), indexes.get(1));
                }else{
                    index = util.transform(road.getIndex(), road.getDirection(), indexes.get(0));
                }

                if(!result.contains(index)) result.add(index);

                if(result.size() == 2) {
                    breakIndicator = true;
                    break;
                }
            }
            if(breakIndicator) break;
        }

        return result;
    }

    //��ȡĳһ������5��·�еĿ�λ������
    public ArrayList<Integer> getAll5(PieceColor color) {
        ArrayList<Integer> result = new ArrayList<>();
        //��̬��ȡ��ͬ��ɫ��Ӧ�ı��
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = this.roadTable.whiteTable;
        else table = this.roadTable.blackTable;

        Iterator itr = table.get(5).keySet().iterator();
        Road road;
        int hashCode;
        int index_offset;
        int temp;
        while (itr.hasNext()) {
            //��ȡRoad
            hashCode = (int) itr.next();
            road = roadTable.summary[hashCode];
            //��·�ڿ�λ���������ʼλ�õ�ƫ����,
            //��Ϊ�Ѿ���ȷ��5��·������һ��ֻ����һ��index
            index_offset = road.emptyIndexes().get(0);
            //����
            temp = util.transform(road.getIndex(), road.getDirection(), index_offset);
            result.add(temp);
        }
        return result;
    }

    public int findOneRandom() {
        Random rand = new Random();
        while (true) {
            int index = rand.nextInt(361);
            if (this.board.get(index) == PieceColor.EMPTY) {
                return index;
            }
        }
    }

    /**
     * ����Ԫλ�ÿ�ʼ��һȦһȦ��ɨ���λ��
     *
     * @return
     */
    public int findOneRandom_Scan() {
        int index = 0;
        int meta = 180;
        for (int i = 1; i < 19; i++) {
            for (Direction direction : Direction.EIGHT_DIRECTIONS) {
                index = util.transform(meta, direction, i);
                if (util.validPosition(meta, direction, i) && this.board.get(index) == PieceColor.EMPTY) {
                    return index;
                }
            }
        }
        return index;
    }

    public ArrayList<Integer> findTwoRandom() {
        Random rand = new Random();
        int index1;
        int index2;
        while (true) {
            index1 = rand.nextInt(361);
            index2 = rand.nextInt(361);
            if (index1 != index2 && this.board.get(index1) == PieceColor.EMPTY && this.board.get(index2) == PieceColor.EMPTY) {
                ArrayList<Integer> result = new ArrayList<>();
                result.add(index1);
                result.add(index2);
                return result;
            }
        }
    }

    //Method 2 combine Method 3
    public ArrayList<Integer> Method2C3() {

        ArrayList<Integer> result = new ArrayList<>();
        int index1;

        Random rand = new Random();
        do {
            index1 = (rand.nextInt(13) + 3) * 19 + (rand.nextInt(13) + 3);
        } while (this.board.get(index1) != PieceColor.EMPTY);

        int[] offset;
        //��index1����Χ��һȦһȦ���ҿ�λ
        for (int i = 1; i < 18; i++) {
            for (Direction direction : Direction.EIGHT_DIRECTIONS) {
                int new_index = util.transform(index1, direction, i);
                if (util.validPosition(index1, direction, i) && this.board.get(new_index) == PieceColor.EMPTY) {
                    result.add(index1);
                    result.add(new_index);

                    return result;
                }

            }
        }
        return result;
    }

    public String name() {
        return "G04";
    }

    Board board = new Board();

    public Board getBoard() {
        return null;
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new Board();
        roadTable = new RoadTable();
        expandingSquare = new ExpandingSquare(roadTable.summary);
    }
}
