package g04.player2;

import core.board.PieceColor;

import java.util.ArrayList;
import java.util.Arrays;

public class Road {
    private int num;
    private int index;//��������
    PieceColor color;

    private boolean isValid;// �ж�����·�ǲ��ǿ���·
    private int[] road;
    private Direction direction;

    public Road(int index, int[] road, PieceColor color , Direction direction) {
       this.index=index;
       this.color=color;
       this.road=road;
       this.direction=direction;
       //KEY:���·�����յ㳬���߽磬����Ϊ������·
       this.isValid = utils.validPosition(index, direction, 5);
    }



    //ע���������Ĳ���index��ʾ���������ڸ�·�ĵڼ���λ�ã���Χ��1~6
    /*����ֵ��
     * 1. ������ǰΪ������·ʱ����0
     * 2. �����Ӻ��ɲ�����·ʱ����num * -1
     * 3. �����Ӻ��·Ϊ����·�����num��������������
    * */
    public int update(int index, PieceColor color){
        test_counter++;
        //�жϵ�ǰ·�Ƿ�Ϊ����·, ����������������к�������
        if(isValid == false) return 0;
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
        this.road[index] = 1;
        //3. ���¸�·����������
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
