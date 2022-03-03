package stud.problem.NPuzzle;

import java.util.EnumMap;
import java.util.List;

/**
 *
 * 地图中可以移动的8个方向，及其箭头符号
 */
public enum NPuzzleDirection {
    E('→'),  //东
    N('↑'),  //北
    S('↓'),  //南
    W('←');  //西

    /**
     * 构造函数
     * @param symbol 枚举项的代表符号--箭头
     */
    NPuzzleDirection(char symbol){
        this.symbol = symbol;
    }

    private final char symbol;
    public char symbol(){
        return symbol;
    }

    @Override
    public String toString() {
        return "" + this.symbol;
    }

    /**
     * 移动方向的两种不同情况（4个方向，8个方向）。
     */
    public static final List<NPuzzleDirection> FOUR_DIRECTIONS = List.of(NPuzzleDirection.E, NPuzzleDirection.N, NPuzzleDirection.S, NPuzzleDirection.W);

    //各个方向移动的坐标位移量
    private static final EnumMap<NPuzzleDirection, int[]> DIRECTION_OFFSET = new EnumMap<>(NPuzzleDirection.class);
    static{
        //(row, col)
        DIRECTION_OFFSET.put(N, new int[]{-1, 0});
        DIRECTION_OFFSET.put(E, new int[]{0, 1});
        DIRECTION_OFFSET.put(S, new int[]{1, 0});
        DIRECTION_OFFSET.put(W, new int[]{0, -1});
    }
    public static int[] offset(NPuzzleDirection dir){
        return DIRECTION_OFFSET.get(dir);
    }
}
