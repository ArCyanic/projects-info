package g04.player2;

import core.board.PieceColor;

import java.util.ArrayList;
import java.util.Arrays;

public class Road {
    private int num;
    private int index;//横纵坐标
    PieceColor color;

    private boolean isValid;// 判断这条路是不是可行路
    private int[] road;
    private Direction direction;

    public Road(int index, int[] road, PieceColor color , Direction direction) {
       this.index=index;
       this.color=color;
       this.road=road;
       this.direction=direction;
       //KEY:如果路径的终点超过边界，则设为不可行路
       this.isValid = utils.validPosition(index, direction, 5);
    }



    //注：本函数的参数index表示：新落子在改路的第几个位置，范围：1~6
    /*返回值：
     * 1. 若落子前为不可行路时返回0
     * 2. 若落子后变成不可行路时返回num * -1
     * 3. 若落子后该路为可行路，则对num进行自增并返回
    * */
    public int update(int index, PieceColor color){
        test_counter++;
        //判断当前路是否为可行路, 如果不是则跳过所有后续操作
        if(isValid == false) return 0;
        //判断当前路径的color是否和棋子的color相反, 如果是就将本路设为不可行路，跳过后续操作。
        if(this.color == color.opposite()) {
            isValid = false;
            return -1 * num;
        }
        //更新路径
        //如果可以执行到这里，那么说明目前这条路是可行路，该路径只有一种颜色的棋子
        //1. 那么将这条路设置为该颜色的路
        this.color = color;
        //2. 更新该路径每一个位置的信息
        this.road[index] = 1;
        //3. 更新该路径棋子数量
        this.num++;
        return this.num;
    }

    public ArrayList<Integer> emptyIndexes(){
        ArrayList<Integer> result = new ArrayList<>();
        for(int i = 0;i < 6;i++){
            if(road[i] == 0) result.add(i);
        }
        return result;
    }

    public int hashCode() {
        return index * 4 + Direction.index(direction);
    }

    public String toString(){
        return "Road : " + Arrays.toString(road) + "\n" + "index : " + index + ", direction : " + direction.toString();
    }

    private int test_counter = 0;

    public int getNum() {
        return num;
    }
    public void setNum(int num) { this.num = num; }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index =index;
    }
    public PieceColor getColor() {
        return color;
    }
    public void setColor(PieceColor color) {
        this.color = color;
    }

    public Direction getDirection() {
        return direction;
    }
}
