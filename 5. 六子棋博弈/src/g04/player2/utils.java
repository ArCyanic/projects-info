package g04.player2;

public class utils {

    /**
     * ��index���꣬��directionƫ��index_offset��λ��
     * ע�����ṩ��Ч���ж�
     * @param index ����
     * @param direction ����
     * @param index_offset ���������Ÿ÷����ƫ����
     * @return
     */
    public static int transform(int index, Direction direction, int index_offset){
        int[] offset = Direction.offset(direction);
        return index + 19 * offset[0] * index_offset + offset[1] * index_offset;
    }
    /**
     * �ж�һ���߲��Ƿ�Ϸ�
     * @param index ����
     * @param direction ����
     * @param index_offset ���������Ÿ÷����ƫ����
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

}
