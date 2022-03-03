package g04.player;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.*;

public class AI extends core.player.AI {

    private RoadTable roadTable;
    private ExpandingSquare expandingSquare;

    //设置搜索变量
    int w = 3;
    int MAX = 999999;
    int MIN = -999999;
    int depth = 0;//搜索深度，深度为奇数时，叶子节点处在MIN层，返回值小于0；偶数层时，也叶子节点处在MAX层，适用simulate函数，因此深度应该为偶数
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
        //判断是否存在胜着
        if (result.size() > 0) {
            //如果找到了4子路, 那么result就是所求坐标
            //如果找到了5子路，那么需要额外生成一个随机落子
            if (result.size() == 1) {
                int temp = findOneRandom();
                while (result.get(0) == temp) {
                    temp = findOneRandom();
                }
                result.add(temp);
            }
        } else {//如果我方不存在胜着，则进入Step 3和Step 4
            //Step 3 判断对方是否存在胜着
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
                //取估值最大的w个节点进行组合并搜索，返回更为精确的估值。最后选择精确估值最高的走步
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
     * 1. 判断是否是第一步，是则结束
     * 2. 判断是否存在胜着，有则结束
     * 3. 判断对方是否存在胜着，有则结束
     * 4. 检索expandingCircle范围内所有点的估值
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
        //取估值最大的w个节点进行组合并搜索，返回更为精确的估值。最后选择精确估值最高的走步
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
     * 1. 判断是否到达叶节点，还没到达则进入2，到达后则进行一次模拟并返回估值
     * 2. makeMove(move)
     * 3. 生成子节点并递归
     * 4. undo(move)，消除第二步造成的影响
     * 5. 结束递归后恢复恢复现场
     * 注：初始递归深度建议设置为偶数
     *
     * @param alpha 下限
     * @param beta  上限
     * @param move  当前要执行的走步
     * @param depth 递归深度
     * @return 节点估值
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

        //Step 3.1 生成子节点, 参考findMove2中的注释
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
                    //子节点生成
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
     * 更新路表，扩圈
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
     * 恢复路表，扩圈
     *
     * @param move
     */
    public void backdate(Move move) {
        roadTable.backdate();
        roadTable.backdate();
        expandingSquare.update(move.index1(), move.index2(), 'a', roadTable.summary);
    }

    /**
     * makeMove的反操作
     *
     * @param move
     */
    public void undo(Move move) {
        backdate(move);
        this.board.undo();
    }

    /**
     * 更新并下棋，主要用于搜索，为了提高效率删除了扩圈判断
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
     * makeMove纯净版，主要用于确定的走步
     *
     * @param move
     * @return
     */
    public void makeMove_pure(Move move) {
        update_pure(move);
        this.board.makeMove(move);
        if(expandingSquare.getQ().size() < expandingSquare.getLength() * expandingSquare.getLength() / 3) expandingSquare.expand(board, roadTable.summary);
    }

    //返回坐标，如果找到了四个子的路则返回两个坐标，如果只找到了五个子的路则返回一个坐标
    public ArrayList<Integer> findWin(PieceColor color){
        //定义返回对象
        ArrayList<Integer> result = new ArrayList<>();
        //动态获取不同颜色对应的表格
        ArrayList<HashMap<Integer, Road>> table;
        if(color == PieceColor.WHITE) table = this.roadTable.whiteTable;
        else table = this.roadTable.blackTable;

        //查找棋子数量为4或5的Road
        for(int i = 4;i <= 5;i++){
            Iterator itr = table.get(i).keySet().iterator();
            //一旦找到一个胜着，计算后立刻返回
            if(itr.hasNext()) {

                //获取Road
                int hashCode = (int) itr.next();
                Road road = roadTable.summary[hashCode];

                //该路内空位置相对于起始位置的偏移量
                ArrayList<Integer> indexes = road.emptyIndexes();
                for(Integer index : indexes){
                    result.add(util.transform(road.getIndex(), road.getDirection(), index));
                }
                break;
            }
        }

        return result;
    }

    //对于对方每一条4子或5子路，只堵一个位置。
    public ArrayList<Integer> findBlock(PieceColor color) {
        //定义返回对象
        ArrayList<Integer> result = new ArrayList<>();
        //动态获取不同颜色对应的表格
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = this.roadTable.whiteTable;
        else table = this.roadTable.blackTable;

        //查找棋子数量为4或5的Road
        boolean breakIndicator = false;
        for (int i = 4; i < 5; i++) {
            Iterator itr = table.get(i).keySet().iterator();
            while(itr.hasNext()){
                //获取Road
                int hashCode = (int) itr.next();
                Road road = roadTable.summary[hashCode];
                //该路内空位置相对于起始位置的偏移量
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

    //获取某一方所有5子路中的空位置坐标
    public ArrayList<Integer> getAll5(PieceColor color) {
        ArrayList<Integer> result = new ArrayList<>();
        //动态获取不同颜色对应的表格
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = this.roadTable.whiteTable;
        else table = this.roadTable.blackTable;

        Iterator itr = table.get(5).keySet().iterator();
        Road road;
        int hashCode;
        int index_offset;
        int temp;
        while (itr.hasNext()) {
            //获取Road
            hashCode = (int) itr.next();
            road = roadTable.summary[hashCode];
            //该路内空位置相对于起始位置的偏移量,
            //因为已经明确是5子路，所以一定只返回一个index
            index_offset = road.emptyIndexes().get(0);
            //计算
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
     * 从天元位置开始，一圈一圈地扫描空位置
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
        //从index1的周围，一圈一圈地找空位
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
