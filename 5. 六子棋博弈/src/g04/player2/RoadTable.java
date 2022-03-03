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
        //下标0弃用，下标1~6，即i，分别表示有i个棋子的路
        for (int i = 0; i < 7; i++) {
            whiteTable.add(new HashMap<>());
            blackTable.add(new HashMap<>());
        }
    }

    //传进来的index是row * 19 + col, 是二维坐标
    public void update(int index, PieceColor color) {
        //对于四个相反的方向
        for (Direction direction : Direction.NEGATIVE_FOUR_DIRECTIONS) {
            //对于每个方向，位置在1~6的六种情况都要考虑
            for (int i = 0; i < 6; i++) {
                //Key:更新路表
                if (utils.validPosition(index, direction, i)) {
                    //这里的hashCode是row * 19 * 4 + col * 4 + dir, 是三维坐标
                    int hashCode = utils.transform(index, direction, i) * 4 + Direction.index(direction);
                    //1. 更新总表
                    /*返回值：
                     * 1. 若落子前为不可行路时返回0
                     * 2. 若落子后变成不可行路时返回num * -1
                     * 3. 若落子后该路为可行路，则对num进行自增并返回
                     * */
                    Road road = hashMap.get(hashCode);
                    int num = road.update(i, color);
                    //2. 更新双方路表
                    //当num == 0时，不予考虑
                    if (num > 0) {
                        //当num > 0时，说明落子后，road为本颜色的可行路，而不影响到对方的路表，因此更新本颜色的路表即可
                        updatePlayerTable(color, num, hashCode);
                    } else if (num < 0) {
                        //当num < 0时，说明落子后，road"才"变为不可行路，说明对方颜色的路被截断，因此只更新对方颜色的路表
                        updatePlayerTable(color.opposite(), num, hashCode);
                    }
                } else {
                    break;
                }


            }
        }
    }

    public void updatePlayerTable(PieceColor color, int num, int hashCode) {
        //根据颜色动态获取路表
        ArrayList<HashMap<Integer, Road>> table;
        if (color == PieceColor.WHITE) table = whiteTable;
        else table = blackTable;

        //更新
        if (num > 0) {
            //如果num大于1，则说明路径更新后该路径的棋子数量大于1，需要先删除对应的棋子数量为num - 1的路径，再添加到棋子数量为num的映射中
            if (num > 1) table.get(num - 1).remove(hashCode);
            table.get(num).put(hashCode, hashMap.get(hashCode));
        } else {//否则小于0
            //如果num小于0，则说明该路径在落子后才变成不可行路，将该路从whiteTable中删除
            table.get(-1 * num).remove(hashCode);
        }
    }
}
