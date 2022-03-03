package g04.player;

import core.board.PieceColor;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class RoadTable {
    /**
     * summary ��Ϊ�����̶������ҿ���ֱ����λ�úͷ�����ж�λ������ֱ���ù̶�������
     * whiteTable ����ÿһ��ά�ȣ������������̶���Ϊ�˷����summary���ж�Ӧ������hashMap������hashCodeΪsummary�ж�Ӧ·���±�
     * blackTable ͬ��
     * V ��ֵ���飬value[PieceColor.WHITE.ordinal()] ��ʾ��ɫ�Ĺ�ֵ����ɫͬ��0��ʾ�գ����ܡ�
     * retrieveList �����б���ģ�����ӵ�ʱ�򱣴���ʷ��Ϣ��ÿ��������һ�������һ�����ȣ�ArrayList<backdateInformation>��ʾ��һ��������������������
     */
    public Road[] summary;
    public ArrayList<HashMap<Integer, Road>> whiteTable;
    public ArrayList<HashMap<Integer, Road>> blackTable;
    public int[] V;
    public Stack<ArrayList<BackdateInformation>> retrieveStack = new Stack<>();

    public RoadTable() {
        //Init summary
        summary = new Road[19 * 19 * 4];
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                for (Direction direction : Direction.POSITIVE_FOUR_DIRECTIONS) {
                    summary[i * 19 * 4 + j * 4 + Direction.index(direction)] = new Road(i * 19 + j, new int[]{0, 0, 0, 0, 0, 0}, PieceColor.EMPTY, direction);
                }
            }
        }

        //Init RoadTable for white player and black player
        //�±�0���ã��±�1~6����i���ֱ��ʾ��i�����ӵ�·
        whiteTable = new ArrayList<>();
        blackTable = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            whiteTable.add(new HashMap<>());
            blackTable.add(new HashMap<>());
        }

        //Init value
        V = new int[3];
        V[PieceColor.EMPTY.ordinal()] = 0;
        V[PieceColor.BLACK.ordinal()] = 0;
        V[PieceColor.WHITE.ordinal()] = 0;
    }

    public RoadTable(RoadTable roadTable){
        summary = new Road[19 * 19 * 4];
        whiteTable = new ArrayList<>();
        blackTable = new ArrayList<>();
        V = new int[3];

        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                for (Direction direction : Direction.POSITIVE_FOUR_DIRECTIONS) {
                    int index = i * 19 * 4 + j * 4 + Direction.index(direction);
                    summary[index] = new Road(roadTable.summary[index]);
                }
            }
        }

        Iterator itr;
        for(int i = 0;i < 7;i++){
            whiteTable.add(new HashMap<>());
            blackTable.add(new HashMap<>());
            itr = roadTable.whiteTable.get(i).keySet().iterator();
            while(itr.hasNext()){
                int key = (int) itr.next();
                whiteTable.get(i).put(key, roadTable.whiteTable.get(i).get(key));
            }
            itr = roadTable.blackTable.get(i).keySet().iterator();
            while(itr.hasNext()){
                int key = (int) itr.next();
                blackTable.get(i).put(key, roadTable.blackTable.get(i).get(key));
            }
        }

        for(int i = 0;i < 3;i++){
            V[i] = roadTable.V[i];
        }

    }


    public int calPlayerValue(PieceColor color){
        int result = 0;
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = whiteTable;
        else table = blackTable;

        for(int i = 1;i < 7;i++){
            HashMap<Integer, Road> m = table.get(i);
            Iterator itr = m.keySet().iterator();
            while (itr.hasNext()){

                result += m.get((Integer) itr.next()).getValue();
            }
        }
        return result;
    }

    public void update_noValue(int index, PieceColor color) {
        ArrayList<BackdateInformation> informationList = new ArrayList<>();
        //�½������б�
        retrieveStack.add(informationList);

        //�����ĸ��෴�ķ���
        for (Direction direction : Direction.NEGATIVE_FOUR_DIRECTIONS) {
            //����ÿ������λ����1~6�����������Ҫ����
            for (int i = 0; i < 6; i++) {
                //Key:����·��
                if (util.validPosition(index, direction, i)) {

                    BackdateInformation information = new BackdateInformation();
                    informationList.add(information);
                    information.setIndex(index);

                    //�����index3d��row * 19 * 4 + col * 4 + dir, ����ά����
                    int index3d = util.transform(index, direction, i) * 4 + Direction.index(direction);
                    information.setIndex3d(index3d);

                    Road road = summary[index3d];
                    //1. �����ܱ�
                    /*����ֵ��
                     * 1. ������ǰΪ������·ʱ����0
                     * 2. �����Ӻ��ɲ�����·ʱ����num * -1
                     * 3. �����Ӻ��·Ϊ����·�����num��������������
                     * */
                    int num = road.update(i, color, information);
                    information.setNum(num);
                    //2. ����˫��·��
                    //��num == 0ʱ�����迼��
                    if (num > 0) {
                        //��num > 0ʱ��˵�����Ӻ�roadΪ����ɫ�Ŀ���·������Ӱ�쵽�Է���·����˸��±���ɫ��·����
                        updatePlayerTable_noValue(color, num, index3d);
                    } else if (num < 0) {
                        //��num < 0ʱ��˵�����Ӻ�road"��"��Ϊ������·��˵���Է���ɫ��·���ضϣ����ֻ���¶Է���ɫ��·��
                        updatePlayerTable_noValue(color.opposite(), num, index3d);
                    }
                } else {
                    break;
                }
            }
        }
    }

    public int update(int index, PieceColor color) {
        int difference = 0;

        ArrayList<BackdateInformation> informationList = new ArrayList<>();
        //�½������б�
        retrieveStack.add(informationList);

        //�����ĸ��෴�ķ���
        for (Direction direction : Direction.NEGATIVE_FOUR_DIRECTIONS) {
            //����ÿ������λ����1~6�����������Ҫ����
            for (int i = 0; i < 6; i++) {
                //Key:����·��
                if (util.validPosition(index, direction, i)) {

                    BackdateInformation information = new BackdateInformation();
                    informationList.add(information);
                    information.setIndex(index);

                    //�����index3d��row * 19 * 4 + col * 4 + dir, ����ά����
                    int index3d = util.transform(index, direction, i) * 4 + Direction.index(direction);
                    information.setIndex3d(index3d);

                    Road road = summary[index3d];
                    //1. �����ܱ�
                    /*����ֵ��
                     * 1. ������ǰΪ������·ʱ����0
                     * 2. �����Ӻ��ɲ�����·ʱ����num * -1
                     * 3. �����Ӻ��·Ϊ����·�����num��������������
                     * */
                    int num = road.update(i, color, information);
                    information.setNum(num);
                    //2. ����˫��·��
                    //��num == 0ʱ�����迼��
                    if (num > 0) {
                        //��num > 0ʱ��˵�����Ӻ�roadΪ����ɫ�Ŀ���·������Ӱ�쵽�Է���·����˸��±���ɫ��·����
                        difference += updatePlayerTable(color, num, index3d);
                    } else if (num < 0) {
                        //��num < 0ʱ��˵�����Ӻ�road"��"��Ϊ������·��˵���Է���ɫ��·���ضϣ����ֻ���¶Է���ɫ��·��
                        difference += updatePlayerTable(color.opposite(), num, index3d);
                    }
                } else {
                    break;
                }
            }
        }
        return difference;
    }

    /**
     * update() �����Ĵ����棬����ֻ����·����������Ϣ������¼retrieveList
     * @param index
     * @param color
     * @return
     */
    public void update_pure(int index, PieceColor color) {
        //�����ĸ��෴�ķ���
        for (Direction direction : Direction.NEGATIVE_FOUR_DIRECTIONS) {
            //����ÿ������λ����1~6�����������Ҫ����
            for (int i = 0; i < 6; i++) {
                //Key:����·��
                if (util.validPosition(index, direction, i)) {
                    //�����index3d��row * 19 * 4 + col * 4 + dir, ����ά����
                    int index3d = util.transform(index, direction, i) * 4 + Direction.index(direction);

                    Road road = summary[index3d];
                    //1. �����ܱ�
                    /*����ֵ��
                     * 1. ������ǰΪ������·ʱ����0
                     * 2. �����Ӻ��ɲ�����·ʱ����num * -1
                     * 3. �����Ӻ��·Ϊ����·�����num��������������
                     * */
                    int num = road.update_pure(i, color);
                    //2. ����˫��·��
                    //��num == 0ʱ�����迼��
                    if (num > 0) {
                        //��num > 0ʱ��˵�����Ӻ�roadΪ����ɫ�Ŀ���·������Ӱ�쵽�Է���·����˸��±���ɫ��·����
                        updatePlayerTable(color, num, index3d);
                    } else if (num < 0) {
                        //��num < 0ʱ��˵�����Ӻ�road"��"��Ϊ������·��˵���Է���ɫ��·���ضϣ����ֻ���¶Է���ɫ��·��
                        updatePlayerTable(color.opposite(), num, index3d);
                    }
                } else {
                    break;
                }
            }
        }
    }

    /** Method��
     * ÿһ��ִ��RoadTable.update(), �������һ��ArrayList��retrieveList��
     * ��ˣ���������������ǻ�ԭ��һ�ε��ֳ�����ȡ�����һ��
     * ��Ҫ��ԭ�ı�����
     * 1. Road.color ����Road����ɫ��EMPTYת��Ϊ������ɫʱ�����ı䣬��ʱnum == 1
     * 2. Road.num ����num > 0ʱ�ı�
     * 3. Road.road ����num > 0ʱ�ı�
     * 4. Road.isValid ����num < 0ʱ�ı䣬Ӧ���޸�Ϊtrue
     * 5. Road.value ����num > 0ʱ�ı�
     * 6. playerTable, V ������backdatePlayerTable()��
     */
    public void backdate(){
        ArrayList<BackdateInformation> informationList = retrieveStack.pop();
        for(BackdateInformation information : informationList){
            Road road = summary[information.getIndex3d()];
            int num = information.getNum();

            backdatePlayerTable(road.getColor(), num, information.getIndex3d());

            //���num == 0, ��˵����·ԭ�����ǲ�����·����˲���ı��κα���
            if(num != 0){
                if(num > 0){
                    road.setNum(num - 1);
                    road.setValue(information.getValue());
                    road.setRoad(information.getIndex_offset(), 0);

                    if(num == 1){
                        road.setColor(PieceColor.EMPTY);
                    }
                }else {
                    //��numС��0�����·�����ó���isValid == false��������Ҫ�Ļ���
                    road.setValid(true);
                }
            }


        }
    }

    /**
     * ����PlayerTable�Լ���Ӧ��ֵ�������ر��θ���ǰ��Ĺ�ֵ��
     * @param color ������ɫ
     * @param num �����ֵ��ʾ���º��·�������������庬��������ע��
     * @param index3d ��ά�±�
     * @return ���θ���ǰ���ֵ��
     */
    public int updatePlayerTable(PieceColor color, int num, int index3d) {
        int difference = 0;

        //������ɫ��̬��ȡ·��
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = whiteTable;
        else table = blackTable;

        //���£�����Ч����ȡ���ж�������ĺ�����Ϊ�˷�����������޸�
        if (num > 0) {
            //���num����1����˵��·�����º��·����������������1����Ҫ��ɾ����Ӧ����������Ϊnum - 1��·��������ӵ���������Ϊnum��ӳ����
            if (num > 1){
                //ɾ���˼�����·����ֵ���Ǹ���
                difference -= updatePlayerHashMap(color, table, num - 1, index3d, 'd');
            }
            //�����˼�����·����ֵ��Ϊ����
            difference += updatePlayerHashMap(color, table, num, index3d, 'a');
        } else {//����С��0
            //���numС��0����˵����·�������Ӻ�ű�ɲ�����·������·ɾ��
            //ɾ���˶Է���·����ֵ������
            difference += updatePlayerHashMap(color, table, num, index3d, 'd');
        }

        //���±��ι�ֵ��
        V[color.ordinal()] += difference;
        return difference;
    }

    public void updatePlayerTable_noValue(PieceColor color, int num, int index3d) {
        int difference = 0;

        //������ɫ��̬��ȡ·��
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = whiteTable;
        else table = blackTable;

        //���£�����Ч����ȡ���ж�������ĺ�����Ϊ�˷�����������޸�
        if (num > 0) {
            //���num����1����˵��·�����º��·����������������1����Ҫ��ɾ����Ӧ����������Ϊnum - 1��·��������ӵ���������Ϊnum��ӳ����
            if (num > 1){
                //ɾ���˼�����·����ֵ���Ǹ���
                difference -= updatePlayerHashMap(color, table, num - 1, index3d, 'd');
            }
            //�����˼�����·����ֵ��Ϊ����
            difference += updatePlayerHashMap(color, table, num, index3d, 'a');
        } else {//����С��0
            //���numС��0����˵����·�������Ӻ�ű�ɲ�����·������·ɾ��
            //ɾ���˶Է���·����ֵ������
            difference += updatePlayerHashMap(color, table, num, index3d, 'd');
        }

        //���±��ι�ֵ��
        V[color.ordinal()] += difference;
    }


    //ִ��updatePlayerTable�������
    public void backdatePlayerTable(PieceColor color, int num, int index3d){
        int difference = 0;
        //������ɫ��̬��ȡ·��
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = whiteTable;
        else table = blackTable;

        if(num > 0){
            if(num > 1){
                difference += updatePlayerHashMap(color, table, num - 1, index3d, 'a');
            }
            difference -= updatePlayerHashMap(color, table, num, index3d, 'd');
        }else{
            difference -= updatePlayerHashMap(color, table, -1 * num, index3d, 'a');
        }

        V[color.ordinal()] += difference;
    }

    /**
     * ����ĳһ��playerTable��ĳһ����������ӳ���ͬʱ���¹�ֵV�����ش˴θ��µĹ�ֵ��
     * @param color
     * @param table
     * @param num table�����������Ƕ��پʹ����ж����ӵ�·
     * @param index ��ά����
     * @param instruction ��Ҫִ�е�ָ��
     * @return ��ֵ���ɾ����������·�Ĺ�ֵ
     */
    public int updatePlayerHashMap(PieceColor color, ArrayList<HashMap<Integer, Road>> table, int num, int index, char instruction){
        try{
            if(instruction == 'd'){
                if(num < 0){
                    //ɾ���Է���·
                    table.get(-num).remove(index);
                }else{
                    //ɾ��������·
                    table.get(num).remove(index);
                }
                return summary[index].getValue();
            }else if(instruction == 'a'){
                //���Ӽ�����·
                table.get(num).put(index, summary[index]);
                return summary[index].getValue();
            }else{
                System.out.println("error******************");
                return -999999;
            }
        }catch (Exception ex){
            System.out.println(ex);
            return -999999;
        }


    }


}
