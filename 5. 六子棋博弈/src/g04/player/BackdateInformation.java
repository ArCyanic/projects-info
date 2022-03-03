package g04.player;

public class BackdateInformation {
    private int index;//二维坐标
    private int num; //注：这里的num保存的是Road.update的返回值，而不是路本身的棋子数量
    private int index3d;//三维坐标
    private int index_offset;//路内偏移量
    private int value;//路的估值

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getIndex_offset() {
        return index_offset;
    }

    public void setIndex_offset(int index_offset) {
        this.index_offset = index_offset;
    }

    public int getIndex3d() {
        return index3d;
    }

    public void setIndex3d(int index3d) {
        this.index3d = index3d;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getIndex() {
        return index;
    }

    public int getNum() {
        return num;
    }

}
