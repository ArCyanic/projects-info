package g04.player;

public class util {

    /**
     * 对index坐标，沿direction偏移index_offset个位置
     * 注：不提供有效性判断
     * @param index 坐标
     * @param direction 方向
     * @param index_offset 该坐标沿着该方向的偏移量
     * @return 返回二维坐标
     */
    public static int transform(int index, Direction direction, int index_offset){
        int[] offset = Direction.offset(direction);
        return index + 19 * offset[0] * index_offset + offset[1] * index_offset;
    }

    //返回三维坐标
    public static int transform3d(int index, Direction direction, int index_offset){
        int[] offset = Direction.offset(direction);
        return (index + 19 * offset[0] * index_offset + offset[1] * index_offset) * 4;
    }
    /**
     * 判断一个走步是否合法
     * @param index 坐标
     * @param direction 方向
     * @param index_offset 该坐标沿着该方向的偏移量
     */
    public static boolean validPosition(int index, Direction direction, int index_offset){
        int row = index / 19;
        int col = index % 19;
        int[] offset = Direction.offset(direction);
        int endRow = row + offset[0] * index_offset;
        int endCol = col + offset[1] * index_offset;
        if(0 <= endRow && endRow < 19 && 0 <= endCol && endCol < 19){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 基于路的估值计算某一空位置的估值
     * @param index 二维坐标
     * @return
     */
    public static int calValue(int index, Road[] summary){
        int result = 0;
        for(Direction direction : Direction.POSITIVE_FOUR_DIRECTIONS){
            for(int i = 0;i < 6;i++){
                if(util.validPosition(index, direction, i)){
                    result += summary[util.transform3d(index, direction, i)].getValue();
                }else{
                    break;
                }
            }
        }

        return result;
    }

}
