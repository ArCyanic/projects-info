package g04.player;

import core.board.PieceColor;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class RoadTable {
    /**
     * summary 因为数量固定，而且可以直接用位置和方向进行定位，所以直接用固定的数组
     * whiteTable 对于每一个维度，首先数量不固定，为了方便和summary进行对应，用了hashMap，其中hashCode为summary中对应路的下标
     * blackTable 同理
     * V 估值数组，value[PieceColor.WHITE.ordinal()] 表示白色的估值，黑色同理。0表示空，不管。
     * retrieveList 检索列表，在模拟落子的时候保存历史信息，每进入更深的一层就增加一个长度，ArrayList<backdateInformation>表示那一层所检索到的所有坐标
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
        //下标0弃用，下标1~6，即i，分别表示有i个棋子的路
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
        //新建检索列表
        retrieveStack.add(informationList);

        //对于四个相反的方向
        for (Direction direction : Direction.NEGATIVE_FOUR_DIRECTIONS) {
            //对于每个方向，位置在1~6的六种情况都要考虑
            for (int i = 0; i < 6; i++) {
                //Key:更新路表
                if (util.validPosition(index, direction, i)) {

                    BackdateInformation information = new BackdateInformation();
                    informationList.add(information);
                    information.setIndex(index);

                    //这里的index3d是row * 19 * 4 + col * 4 + dir, 是三维坐标
                    int index3d = util.transform(index, direction, i) * 4 + Direction.index(direction);
                    information.setIndex3d(index3d);

                    Road road = summary[index3d];
                    //1. 更新总表
                    /*返回值：
                     * 1. 若落子前为不可行路时返回0
                     * 2. 若落子后变成不可行路时返回num * -1
                     * 3. 若落子后该路为可行路，则对num进行自增并返回
                     * */
                    int num = road.update(i, color, information);
                    information.setNum(num);
                    //2. 更新双方路表
                    //当num == 0时，不予考虑
                    if (num > 0) {
                        //当num > 0时，说明落子后，road为本颜色的可行路，而不影响到对方的路表，因此更新本颜色的路表即可
                        updatePlayerTable_noValue(color, num, index3d);
                    } else if (num < 0) {
                        //当num < 0时，说明落子后，road"才"变为不可行路，说明对方颜色的路被截断，因此只更新对方颜色的路表
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
        //新建检索列表
        retrieveStack.add(informationList);

        //对于四个相反的方向
        for (Direction direction : Direction.NEGATIVE_FOUR_DIRECTIONS) {
            //对于每个方向，位置在1~6的六种情况都要考虑
            for (int i = 0; i < 6; i++) {
                //Key:更新路表
                if (util.validPosition(index, direction, i)) {

                    BackdateInformation information = new BackdateInformation();
                    informationList.add(information);
                    information.setIndex(index);

                    //这里的index3d是row * 19 * 4 + col * 4 + dir, 是三维坐标
                    int index3d = util.transform(index, direction, i) * 4 + Direction.index(direction);
                    information.setIndex3d(index3d);

                    Road road = summary[index3d];
                    //1. 更新总表
                    /*返回值：
                     * 1. 若落子前为不可行路时返回0
                     * 2. 若落子后变成不可行路时返回num * -1
                     * 3. 若落子后该路为可行路，则对num进行自增并返回
                     * */
                    int num = road.update(i, color, information);
                    information.setNum(num);
                    //2. 更新双方路表
                    //当num == 0时，不予考虑
                    if (num > 0) {
                        //当num > 0时，说明落子后，road为本颜色的可行路，而不影响到对方的路表，因此更新本颜色的路表即可
                        difference += updatePlayerTable(color, num, index3d);
                    } else if (num < 0) {
                        //当num < 0时，说明落子后，road"才"变为不可行路，说明对方颜色的路被截断，因此只更新对方颜色的路表
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
     * update() 函数的纯净版，单纯只更新路表，不返回信息，不记录retrieveList
     * @param index
     * @param color
     * @return
     */
    public void update_pure(int index, PieceColor color) {
        //对于四个相反的方向
        for (Direction direction : Direction.NEGATIVE_FOUR_DIRECTIONS) {
            //对于每个方向，位置在1~6的六种情况都要考虑
            for (int i = 0; i < 6; i++) {
                //Key:更新路表
                if (util.validPosition(index, direction, i)) {
                    //这里的index3d是row * 19 * 4 + col * 4 + dir, 是三维坐标
                    int index3d = util.transform(index, direction, i) * 4 + Direction.index(direction);

                    Road road = summary[index3d];
                    //1. 更新总表
                    /*返回值：
                     * 1. 若落子前为不可行路时返回0
                     * 2. 若落子后变成不可行路时返回num * -1
                     * 3. 若落子后该路为可行路，则对num进行自增并返回
                     * */
                    int num = road.update_pure(i, color);
                    //2. 更新双方路表
                    //当num == 0时，不予考虑
                    if (num > 0) {
                        //当num > 0时，说明落子后，road为本颜色的可行路，而不影响到对方的路表，因此更新本颜色的路表即可
                        updatePlayerTable(color, num, index3d);
                    } else if (num < 0) {
                        //当num < 0时，说明落子后，road"才"变为不可行路，说明对方颜色的路被截断，因此只更新对方颜色的路表
                        updatePlayerTable(color.opposite(), num, index3d);
                    }
                } else {
                    break;
                }
            }
        }
    }

    /** Method：
     * 每一次执行RoadTable.update(), 都会添加一个ArrayList到retrieveList中
     * 因此，这个函数的作用是还原上一次的现场。即取出最后一个
     * 需要复原的变量：
     * 1. Road.color 仅在Road的颜色从EMPTY转变为其他颜色时发生改变，此时num == 1
     * 2. Road.num 仅在num > 0时改变
     * 3. Road.road 仅在num > 0时改变
     * 4. Road.isValid 仅当num < 0时改变，应该修改为true
     * 5. Road.value 仅当num > 0时改变
     * 6. playerTable, V 发生在backdatePlayerTable()中
     */
    public void backdate(){
        ArrayList<BackdateInformation> informationList = retrieveStack.pop();
        for(BackdateInformation information : informationList){
            Road road = summary[information.getIndex3d()];
            int num = information.getNum();

            backdatePlayerTable(road.getColor(), num, information.getIndex3d());

            //如果num == 0, 则说明该路原本就是不可行路，因此不会改变任何变量
            if(num != 0){
                if(num > 0){
                    road.setNum(num - 1);
                    road.setValue(information.getValue());
                    road.setRoad(information.getIndex_offset(), 0);

                    if(num == 1){
                        road.setColor(PieceColor.EMPTY);
                    }
                }else {
                    //若num小于0，则该路被设置成了isValid == false，现在需要改回来
                    road.setValid(true);
                }
            }


        }
    }

    /**
     * 更新PlayerTable以及对应估值，并返回本次更新前后的估值差
     * @param color 棋手颜色
     * @param num 其绝对值表示更新后该路的棋子数，具体含义见下面的注释
     * @param index3d 三维下标
     * @return 本次更新前后估值差
     */
    public int updatePlayerTable(PieceColor color, int num, int index3d) {
        int difference = 0;

        //根据颜色动态获取路表
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = whiteTable;
        else table = blackTable;

        //更新，牺牲效率提取出有多个参数的函数是为了方便后续调试修改
        if (num > 0) {
            //如果num大于1，则说明路径更新后该路径的棋子数量大于1，需要先删除对应的棋子数量为num - 1的路径，再添加到棋子数量为num的映射中
            if (num > 1){
                //删除了己方的路，估值差是负数
                difference -= updatePlayerHashMap(color, table, num - 1, index3d, 'd');
            }
            //新增了己方的路，估值差为正数
            difference += updatePlayerHashMap(color, table, num, index3d, 'a');
        } else {//否则小于0
            //如果num小于0，则说明该路径在落子后才变成不可行路，将该路删除
            //删除了对方的路，估值差增加
            difference += updatePlayerHashMap(color, table, num, index3d, 'd');
        }

        //更新本次估值差
        V[color.ordinal()] += difference;
        return difference;
    }

    public void updatePlayerTable_noValue(PieceColor color, int num, int index3d) {
        int difference = 0;

        //根据颜色动态获取路表
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = whiteTable;
        else table = blackTable;

        //更新，牺牲效率提取出有多个参数的函数是为了方便后续调试修改
        if (num > 0) {
            //如果num大于1，则说明路径更新后该路径的棋子数量大于1，需要先删除对应的棋子数量为num - 1的路径，再添加到棋子数量为num的映射中
            if (num > 1){
                //删除了己方的路，估值差是负数
                difference -= updatePlayerHashMap(color, table, num - 1, index3d, 'd');
            }
            //新增了己方的路，估值差为正数
            difference += updatePlayerHashMap(color, table, num, index3d, 'a');
        } else {//否则小于0
            //如果num小于0，则说明该路径在落子后才变成不可行路，将该路删除
            //删除了对方的路，估值差增加
            difference += updatePlayerHashMap(color, table, num, index3d, 'd');
        }

        //更新本次估值差
        V[color.ordinal()] += difference;
    }


    //执行updatePlayerTable的逆操作
    public void backdatePlayerTable(PieceColor color, int num, int index3d){
        int difference = 0;
        //根据颜色动态获取路表
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
     * 更新某一个playerTable的某一棋子数量的映射表，同时更新估值V，返回此次更新的估值差
     * @param color
     * @param table
     * @param num table索引，数字是多少就代表含有多少子的路
     * @param index 三维坐标
     * @param instruction 所要执行的指令
     * @return 估值差，即删除或新增的路的估值
     */
    public int updatePlayerHashMap(PieceColor color, ArrayList<HashMap<Integer, Road>> table, int num, int index, char instruction){
        try{
            if(instruction == 'd'){
                if(num < 0){
                    //删除对方的路
                    table.get(-num).remove(index);
                }else{
                    //删除己方的路
                    table.get(num).remove(index);
                }
                return summary[index].getValue();
            }else if(instruction == 'a'){
                //增加己方的路
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
