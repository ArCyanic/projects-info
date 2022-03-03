package g04.player2;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public enum Direction {
    N,  //北
    NE, //东北
    E,  //东
    SE, //东南
    S,  //南
    SW, //西南
    W,  //西
    NW; //西北

    public static final List<Direction> POSITIVE_FOUR_DIRECTIONS = Arrays.asList(Direction.E, Direction.SE, Direction.S, Direction.SW);
    public static final List<Direction> NEGATIVE_FOUR_DIRECTIONS = Arrays.asList(Direction.W, Direction.NW, Direction.N, Direction.NE);
    public static final List<Direction> EIGHT_DIRECTIONS = Arrays.asList(Direction.E, Direction.SE, Direction.S, Direction.SW,Direction.W, Direction.NW, Direction.N, Direction.NE);

    private static final EnumMap<Direction, int[]> DIRECTION_OFFSET = new EnumMap<>(Direction.class);
    static{
        //(row, col)
        DIRECTION_OFFSET.put(N, new int[]{-1, 0});
        DIRECTION_OFFSET.put(NE, new int[]{-1, 1});
        DIRECTION_OFFSET.put(E, new int[]{0, 1});
        DIRECTION_OFFSET.put(SE, new int[]{1, 1});
        DIRECTION_OFFSET.put(S, new int[]{1, 0});
        DIRECTION_OFFSET.put(SW, new int[]{1, -1});
        DIRECTION_OFFSET.put(W, new int[]{0, -1});
        DIRECTION_OFFSET.put(NW, new int[]{-1, -1});
    }

    private static final EnumMap<Direction, Integer> DIRECTION_INDEX = new EnumMap<>(Direction.class);
    static {
        int i = 0;
        for(Direction direction : POSITIVE_FOUR_DIRECTIONS){
            DIRECTION_INDEX.put(direction, i);
            i++;
        }
        i = 0;
        for(Direction direction : NEGATIVE_FOUR_DIRECTIONS){
            DIRECTION_INDEX.put(direction, i);
            i++;
        }
    }
    public static int[] offset(Direction dir){
        return DIRECTION_OFFSET.get(dir);
    }
    public static int index(Direction dir) {
        return DIRECTION_INDEX.get(dir);
    }
}
