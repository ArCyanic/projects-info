package g04.player;

import core.board.PieceColor;

import java.util.ArrayList;
import java.util.Arrays;

public class Road {
    private int num;//这条路上棋子的个数
    private int index;//二维坐标
    private PieceColor color;//棋子颜色
    private boolean isValid;// 判断这条路是不是可行路
    private int[] road;//路径详情
    private Direction direction;//方向
    private int value;//路径估值
    int value_offset = 3;


    public Road(int index,int[] road,PieceColor color ,Direction direction) {
       this.index=index;
       this.color=color;
       this.road=road;
       this.direction=direction;
       //KEY:如果路径的终点超过边界，则设为不可行路
       this.isValid = util.validPosition(index, direction, 5);
       this.value = 0;
    }

    public Road(Road road){
        this.num = road.num;
        this.index = road.index;
        this.color = road.color;
        this.isValid = road.isValid;
        this.road = new int[6];
        for(int i = 0;i < 6;i++){
            this.road[i] = road.road[i];
        }
        this.direction = road.direction;
        this.value = road.value;
    }

    /**
     * 计算路径估值
     * @return
     */
    public void calValue(){
        this.value = (int) Math.pow(num, value_offset);
    }


    /**
     * 注：本函数的参数index_offset表示：新落子在路的第几个位置，范围：1~6
     * 返回值：
     * 1. 若落子前为不可行路时返回0
     * 2. 若落子后变成不可行路时返回num * -1
     * 3. 若落子后该路为可行路，则对num进行自增并返回
     */
    public int update(int index_offset, PieceColor color, BackdateInformation information){
        //判断当前路是否为可行路, 如果不是则跳过所有后续操作
        //如果当前路填满同色棋子，同样视为不可行路
        if(isValid == false || num == 6){
            //如果更新时已经是不可行路了，那么原本就是不可行路
            return 0;
        }

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
        information.setIndex_offset(index_offset);
        this.road[index_offset] = 1;

        //3. 更新该路径棋子数量
        this.num++;
        if(num < 0 || num > 6){
            System.out.println("Wrong");
        }

        //4. 更新估值
        information.setValue(value);
        this.calValue();
        return this.num;
    }

    /**
     * update() 纯净版，不记录信息
     * @param index_offset
     * @param color
     * @return
     */
    public int update_pure(int index_offset, PieceColor color){
        //判断当前路是否为可行路, 如果不是则跳过所有后续操作
        if(isValid == false){
            //如果更新时已经是不可行路了，那么原本就是不可行路
            return 0;
        }

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
        this.road[index_offset] = 1;

        //3. 更新该路径棋子数量
        this.num++;

        //4. 更新估值
        this.calValue();
        return this.num;
    }

    /**
     * 计算并返回这条路的所有空位置相对于起始位置的偏移量
     * @return
     */
    public ArrayList<Integer> emptyIndexes(){
        ArrayList<Integer> result = new ArrayList<>();
        for(int i = 0;i < 6;i++){
            if(road[i] == 0) result.add(i);
        }
        return result;
    }

    /**
     * 根据二维坐标index和方向计算三维坐标
     * @return
     */
    public int hashCode() {
        return index * 4 + Direction.index(direction);
    }

    /**
     * 将index_offset处的值修改为value
     * @param index_offset
     * @param value
     */
    public void setRoad(int index_offset, int value) {
        road[index_offset] = value;
    }

    public String toString(){
        return "Road : " + Arrays.toString(road) + "\n" + "index : " + index + ", direction : " + direction.toString();
    }

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

    public Direction getDirection() {
        return direction;
    }

    public int getValue() {
        return value;
    }

    public void setColor(PieceColor color) {
        this.color = color;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public int[] getRoad() {
        return road;
    }

    public void setRoad(int[] road) {
        this.road = road;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
