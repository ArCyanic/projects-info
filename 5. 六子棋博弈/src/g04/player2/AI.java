package g04.player2;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class AI extends core.player.AI {

    private RoadTable roadTable;

    private String source;

    public String getSource() {
        return source;
    }

    /**
     * 1. 判断是否是第一步棋
     * 2. 若不是则找寻自己的胜着
     * 3. 若找不到自己的胜着则找对方的胜着
     * 4. 若上述条件皆为否，则随机落子
     * TODO:为什么要先makeMove(opponentMove)?????
     * 因为要更新自己的棋盘
     */

    //测试变量，如果我找到了对方的胜着，那么变为true
    private boolean blockIndicator = false;

    public Move findMove(Move opponentMove) {
        //Step 1
        if (opponentMove == null) {
            Move move = this.firstMove();
            makeMove(move);
            return move;
        } else {
            makeMove(opponentMove);
            //Step 2
            ArrayList<Integer> result = findWin(this.board.whoseMove());
            //判断是否存在胜着
            if(result.size() > 0){
                //如果找到了4子路, 那么result就是所求坐标
                //如果找到了5子路，那么需要额外生成一个随机落子
                if(result.size() == 1) {
                    int temp = findOneRandom();
                    while (result.get(0) == temp) {
                        temp = findOneRandom();
                    }
                    result.add(temp);
                }
            }else{//如果我方不存在胜着，则进入Step 3和Step 4
                //Step 3 判断对方是否存在胜着
                result = findWin(this.board.whoseMove().opposite());
                //如果对方存在胜着
                if(result.size() > 0){
                    //TEST
                    blockIndicator = true;

                    //注意，findWin返回的是任意一个胜着方案
                    //如果对方至少有一条4子路（即result.size() == 2），那么我至多能堵一条，所以直接取result中的坐标, 不做多余操作
                    //如果对方至少有一条5子路，那么有多种情况：
                    //1. 对方只有一条5子路，那么需要再额外生成一个随机坐标。
                    //2. 对方有2条以上5子路，任选两条5子路进行拦截。
                    if(result.size() == 1){//如果size == 1，则说明对方至少有一条5子路
                        //那么首先获取到对方所有5子路中的空位置坐标
                        ArrayList<Integer> emptyIndexes = getAll5(this.board.whoseMove().opposite());
                        //如果大小为1，那么还需要额外生成一个随机坐标
                        if(emptyIndexes.size() == 1){
                            int temp = findOneRandom();
                            while (result.get(0) == temp) {
                                temp = findOneRandom();
                            }
                            result.add(temp);
                        }else if(emptyIndexes.size() > 1){
                            //如果大小大于1，那么任选两个位置堵住。
                            result = (ArrayList<Integer>) emptyIndexes.subList(0,1);
                        }
                    }

                }else{//Step 4 直接生成了两个随机落子
                    result = Method2C3();
                }
            }
            if(result.get(0) == 180 || result.get(1) == 180){
                System.out.println("");
            }
            if(result.get(0) == 180){
                result.set(0, findOneRandom());
            }
            if(result.get(1) == 180){
                result.set(1, findOneRandom());
            }

            Move move = new Move(result.get(0), result.get(1));
            makeMove(move);
            return move;
        }
    }

    public void makeMove(Move move){
        roadTable.update(move.index1(), this.board.whoseMove());
        roadTable.update(move.index2(), this.board.whoseMove());
        this.board.makeMove(move);
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
        for(int i = 4;i < 5;i++){
            Iterator itr = table.get(i).keySet().iterator();
            //一旦找到一个胜着，计算后立刻返回
            if(itr.hasNext()) {

                //获取Road
                int hashCode = (int) itr.next();
                Road road = roadTable.hashMap.get(hashCode);

                //该路内空位置相对于起始位置的偏移量
                ArrayList<Integer> indexes = road.emptyIndexes();

                this.source = "findWin : " + color + ", " + road.toString() + " ";

                for(Integer index : indexes){
                    result.add(utils.transform(road.getIndex(), road.getDirection(), index));
                    this.source += index.toString() + " ";
                }
                break;
            }
        }

        return result;
    }

    //获取某一方所有5子路中的空位置坐标
    public ArrayList<Integer> getAll5(PieceColor color){
        ArrayList<Integer> result = new ArrayList<>();
        //动态获取不同颜色对应的表格
        ArrayList<HashMap<Integer, Road>> table;
        if(color == PieceColor.WHITE) table = this.roadTable.whiteTable;
        else table = this.roadTable.blackTable;

        Iterator itr = table.get(5).keySet().iterator();
        Road road;
        int hashCode;
        int index_offset;
        int temp;
        while (itr.hasNext()){
            //获取Road
            hashCode = (int) itr.next();
            road = roadTable.hashMap.get(hashCode);
            //该路内空位置相对于起始位置的偏移量,
            //因为已经明确是5子路，所以一定只返回一个index
            index_offset = road.emptyIndexes().get(0);
            //计算
            temp = utils.transform(road.getIndex(), road.getDirection(), index_offset);
            result.add(temp);
        }
        this.source = "getAll5";
        return result;
    }

    public int findOneRandom(){
        Random rand = new Random();
        while(true){
            int index = rand.nextInt(361);
            if(this.board.get(index) == PieceColor.EMPTY){
                this.source = "findOneRandom";
                return index;
            }
        }
    }

    /**
     * 从天元位置开始，一圈一圈地扫描空位置
     * @return
     */
    public int findOneRandom_Scan(){
        int index = 0;
        int meta = 180;
        for(int i = 1;i < 19;i++){
            for(Direction direction : Direction.EIGHT_DIRECTIONS){
                index = utils.transform(meta, direction, i);
                if(utils.validPosition(meta, direction, i) && this.board.get(index) == PieceColor.EMPTY){
                    return index;
                }
            }
        }
        return index;
    }

    public ArrayList<Integer> findTwoRandom(){
        Random rand = new Random();
        int index1;
        int index2;
        while (true){
            index1 = rand.nextInt(361) ;
            index2 = rand.nextInt(361) ;
            if(index1 != index2 && this.board.get(index1) == PieceColor.EMPTY && this.board.get(index2) == PieceColor.EMPTY){
                ArrayList<Integer> result = new ArrayList<>();
                result.add(index1);
                result.add(index2);
                return result;
            }
        }
    }

    //Method 2 combine Method 3
    public ArrayList<Integer> Method2C3(){
        this.source = "Method2C3";

        ArrayList<Integer> result = new ArrayList<>();
        int index1;

        Random rand = new Random();
        do {
            index1 = (rand.nextInt(13)+3)*19+(rand.nextInt(13)+3);
        } while(this.board.get(index1) != PieceColor.EMPTY );

        int[] offset;
        //从index1的周围，一圈一圈地找空位
        for(int i = 1;i < 18;i++){
            for(Direction direction : Direction.EIGHT_DIRECTIONS){
                int new_index = utils.transform(index1, direction, i);
                if(utils.validPosition(index1, direction, i) && this.board.get(new_index) == PieceColor.EMPTY){
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
    }
}
