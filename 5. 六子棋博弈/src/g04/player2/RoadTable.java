package g04.player2;

import core.board.PieceColor;

import java.util.ArrayList;
import java.util.HashMap;

public class RoadTable {
    public HashMap<Integer, Road> hashMap = new HashMap<>();
    public ArrayList<HashMap<Integer, Road>> whiteTable = new ArrayList<>();
    public ArrayList<HashMap<Integer, Road>> blackTable = new ArrayList<>();


    public RoadTable() {
        //Update hashMap
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                for (Direction direction : Direction.POSITIVE_FOUR_DIRECTIONS) {
                    hashMap.put(i * 19 * 4 + j * 4 + Direction.index(direction), new Road(i * 19 + j, new int[]{0, 0, 0, 0, 0, 0}, PieceColor.EMPTY, direction));
                }
            }
        }

        //Update RoadTable for white player and black player
        //�±�0���ã��±�1~6����i���ֱ��ʾ��i�����ӵ�·
        for (int i = 0; i < 7; i++) {
            whiteTable.add(new HashMap<>());
            blackTable.add(new HashMap<>());
        }
    }

    //��������index��row * 19 + col, �Ƕ�ά����
    public void update(int index, PieceColor color) {
        //�����ĸ��෴�ķ���
        for (Direction direction : Direction.NEGATIVE_FOUR_DIRECTIONS) {
            //����ÿ������λ����1~6�����������Ҫ����
            for (int i = 0; i < 6; i++) {
                //Key:����·��
                if (utils.validPosition(index, direction, i)) {
                    //�����hashCode��row * 19 * 4 + col * 4 + dir, ����ά����
                    int hashCode = utils.transform(index, direction, i) * 4 + Direction.index(direction);
                    //1. �����ܱ�
                    /*����ֵ��
                     * 1. ������ǰΪ������·ʱ����0
                     * 2. �����Ӻ��ɲ�����·ʱ����num * -1
                     * 3. �����Ӻ��·Ϊ����·�����num��������������
                     * */
                    Road road = hashMap.get(hashCode);
                    int num = road.update(i, color);
                    //2. ����˫��·��
                    //��num == 0ʱ�����迼��
                    if (num > 0) {
                        //��num > 0ʱ��˵�����Ӻ�roadΪ����ɫ�Ŀ���·������Ӱ�쵽�Է���·����˸��±���ɫ��·����
                        updatePlayerTable(color, num, hashCode);
                    } else if (num < 0) {
                        //��num < 0ʱ��˵�����Ӻ�road"��"��Ϊ������·��˵���Է���ɫ��·���ضϣ����ֻ���¶Է���ɫ��·��
                        updatePlayerTable(color.opposite(), num, hashCode);
                    }
                } else {
                    break;
                }


            }
        }
    }

    public void updatePlayerTable(PieceColor color, int num, int hashCode) {
        //������ɫ��̬��ȡ·��
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = whiteTable;
        else table = blackTable;

        //����
        if (num > 0) {
            //���num����1����˵��·�����º��·����������������1����Ҫ��ɾ����Ӧ����������Ϊnum - 1��·��������ӵ���������Ϊnum��ӳ����
            if (num > 1) table.get(num - 1).remove(hashCode);
            table.get(num).put(hashCode, hashMap.get(hashCode));
        } else {//����С��0
            //���numС��0����˵����·�������Ӻ�ű�ɲ�����·������·��whiteTable��ɾ��
            table.get(-1 * num).remove(hashCode);
        }
    }
}
