package g04.player;

public class BackdateInformation {
    private int index;//��ά����
    private int num; //ע�������num�������Road.update�ķ���ֵ��������·�������������
    private int index3d;//��ά����
    private int index_offset;//·��ƫ����
    private int value;//·�Ĺ�ֵ

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
