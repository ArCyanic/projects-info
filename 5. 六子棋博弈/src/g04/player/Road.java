package g04.player;

import core.board.PieceColor;

import java.util.ArrayList;
import java.util.Arrays;

public class Road {
    private int num;//����·�����ӵĸ���
    private int index;//��ά����
    private PieceColor color;//������ɫ
    private boolean isValid;// �ж�����·�ǲ��ǿ���·
    private int[] road;//·������
    private Direction direction;//����
    private int value;//·����ֵ
    int value_offset = 3;


    public Road(int index,int[] road,PieceColor color ,Direction direction) {
       this.index=index;
       this.color=color;
       this.road=road;
       this.direction=direction;
       //KEY:���·�����յ㳬���߽磬����Ϊ������·
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
     * ����·����ֵ
     * @return
     */
    public void calValue(){
        this.value = (int) Math.pow(num, value_offset);
    }


    /**
     * ע���������Ĳ���index_offset��ʾ����������·�ĵڼ���λ�ã���Χ��1~6
     * ����ֵ��
     * 1. ������ǰΪ������·ʱ����0
     * 2. �����Ӻ��ɲ�����·ʱ����num * -1
     * 3. �����Ӻ��·Ϊ����·�����num��������������
     */
    public int update(int index_offset, PieceColor color, BackdateInformation information){
        //�жϵ�ǰ·�Ƿ�Ϊ����·, ����������������к�������
        //�����ǰ·����ͬɫ���ӣ�ͬ����Ϊ������·
        if(isValid == false || num == 6){
            //�������ʱ�Ѿ��ǲ�����·�ˣ���ôԭ�����ǲ�����·
            return 0;
        }

        //�жϵ�ǰ·����color�Ƿ�����ӵ�color�෴, ����Ǿͽ���·��Ϊ������·����������������
        if(this.color == color.opposite()) {
            isValid = false;
            return -1 * num;
        }
        //����·��
        //�������ִ�е������ô˵��Ŀǰ����·�ǿ���·����·��ֻ��һ����ɫ������
        //1. ��ô������·����Ϊ����ɫ��·
        this.color = color;

        //2. ���¸�·��ÿһ��λ�õ���Ϣ
        information.setIndex_offset(index_offset);
        this.road[index_offset] = 1;

        //3. ���¸�·����������
        this.num++;
        if(num < 0 || num > 6){
            System.out.println("Wrong");
        }

        //4. ���¹�ֵ
        information.setValue(value);
        this.calValue();
        return this.num;
    }

    /**
     * update() �����棬����¼��Ϣ
     * @param index_offset
     * @param color
     * @return
     */
    public int update_pure(int index_offset, PieceColor color){
        //�жϵ�ǰ·�Ƿ�Ϊ����·, ����������������к�������
        if(isValid == false){
            //�������ʱ�Ѿ��ǲ�����·�ˣ���ôԭ�����ǲ�����·
            return 0;
        }

        //�жϵ�ǰ·����color�Ƿ�����ӵ�color�෴, ����Ǿͽ���·��Ϊ������·����������������
        if(this.color == color.opposite()) {
            isValid = false;
            return -1 * num;
        }
        //����·��
        //�������ִ�е������ô˵��Ŀǰ����·�ǿ���·����·��ֻ��һ����ɫ������
        //1. ��ô������·����Ϊ����ɫ��·
        this.color = color;

        //2. ���¸�·��ÿһ��λ�õ���Ϣ
        this.road[index_offset] = 1;

        //3. ���¸�·����������
        this.num++;

        //4. ���¹�ֵ
        this.calValue();
        return this.num;
    }

    /**
     * ���㲢��������·�����п�λ���������ʼλ�õ�ƫ����
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
     * ���ݶ�ά����index�ͷ��������ά����
     * @return
     */
    public int hashCode() {
        return index * 4 + Direction.index(direction);
    }

    /**
     * ��index_offset����ֵ�޸�Ϊvalue
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
