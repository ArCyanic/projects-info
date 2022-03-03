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
     * 1. �ж��Ƿ��ǵ�һ����
     * 2. ����������Ѱ�Լ���ʤ��
     * 3. ���Ҳ����Լ���ʤ�����ҶԷ���ʤ��
     * 4. ������������Ϊ�����������
     * TODO:ΪʲôҪ��makeMove(opponentMove)?????
     * ��ΪҪ�����Լ�������
     */

    //���Ա�����������ҵ��˶Է���ʤ�ţ���ô��Ϊtrue
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
            //�ж��Ƿ����ʤ��
            if(result.size() > 0){
                //����ҵ���4��·, ��ôresult������������
                //����ҵ���5��·����ô��Ҫ��������һ���������
                if(result.size() == 1) {
                    int temp = findOneRandom();
                    while (result.get(0) == temp) {
                        temp = findOneRandom();
                    }
                    result.add(temp);
                }
            }else{//����ҷ�������ʤ�ţ������Step 3��Step 4
                //Step 3 �ж϶Է��Ƿ����ʤ��
                result = findWin(this.board.whoseMove().opposite());
                //����Է�����ʤ��
                if(result.size() > 0){
                    //TEST
                    blockIndicator = true;

                    //ע�⣬findWin���ص�������һ��ʤ�ŷ���
                    //����Է�������һ��4��·����result.size() == 2������ô�������ܶ�һ��������ֱ��ȡresult�е�����, �����������
                    //����Է�������һ��5��·����ô�ж��������
                    //1. �Է�ֻ��һ��5��·����ô��Ҫ�ٶ�������һ��������ꡣ
                    //2. �Է���2������5��·����ѡ����5��·�������ء�
                    if(result.size() == 1){//���size == 1����˵���Է�������һ��5��·
                        //��ô���Ȼ�ȡ���Է�����5��·�еĿ�λ������
                        ArrayList<Integer> emptyIndexes = getAll5(this.board.whoseMove().opposite());
                        //�����СΪ1����ô����Ҫ��������һ���������
                        if(emptyIndexes.size() == 1){
                            int temp = findOneRandom();
                            while (result.get(0) == temp) {
                                temp = findOneRandom();
                            }
                            result.add(temp);
                        }else if(emptyIndexes.size() > 1){
                            //�����С����1����ô��ѡ����λ�ö�ס��
                            result = (ArrayList<Integer>) emptyIndexes.subList(0,1);
                        }
                    }

                }else{//Step 4 ֱ�������������������
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

    //�������꣬����ҵ����ĸ��ӵ�·�򷵻��������꣬���ֻ�ҵ�������ӵ�·�򷵻�һ������
    public ArrayList<Integer> findWin(PieceColor color){
        //���巵�ض���
        ArrayList<Integer> result = new ArrayList<>();
        //��̬��ȡ��ͬ��ɫ��Ӧ�ı��
        ArrayList<HashMap<Integer, Road>> table;
        if(color == PieceColor.WHITE) table = this.roadTable.whiteTable;
        else table = this.roadTable.blackTable;

        //������������Ϊ4��5��Road
        for(int i = 4;i < 5;i++){
            Iterator itr = table.get(i).keySet().iterator();
            //һ���ҵ�һ��ʤ�ţ���������̷���
            if(itr.hasNext()) {

                //��ȡRoad
                int hashCode = (int) itr.next();
                Road road = roadTable.hashMap.get(hashCode);

                //��·�ڿ�λ���������ʼλ�õ�ƫ����
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

    //��ȡĳһ������5��·�еĿ�λ������
    public ArrayList<Integer> getAll5(PieceColor color){
        ArrayList<Integer> result = new ArrayList<>();
        //��̬��ȡ��ͬ��ɫ��Ӧ�ı��
        ArrayList<HashMap<Integer, Road>> table;
        if(color == PieceColor.WHITE) table = this.roadTable.whiteTable;
        else table = this.roadTable.blackTable;

        Iterator itr = table.get(5).keySet().iterator();
        Road road;
        int hashCode;
        int index_offset;
        int temp;
        while (itr.hasNext()){
            //��ȡRoad
            hashCode = (int) itr.next();
            road = roadTable.hashMap.get(hashCode);
            //��·�ڿ�λ���������ʼλ�õ�ƫ����,
            //��Ϊ�Ѿ���ȷ��5��·������һ��ֻ����һ��index
            index_offset = road.emptyIndexes().get(0);
            //����
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
     * ����Ԫλ�ÿ�ʼ��һȦһȦ��ɨ���λ��
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
        //��index1����Χ��һȦһȦ���ҿ�λ
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
