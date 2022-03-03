package g04.player;

import java.util.ArrayList;

public class Node implements Comparable<Node>{
    private int index;
    private int value;
    //һ���ڵ�Ĺ�ֵ�ܵ������������е�·Ӱ�죬����ڳ�ʼ���ڵ�ʱ��ʼ����ȡ������ص�ָ��
    private ArrayList<Road> neighbors_road;
    private ArrayList<Integer> neighbors_point;

    /**
     * ��ʼ���ڵ�
     * һ���ؼ�Ӧ�ó������ڻָ��ֳ���ʱ�򣬽�һ��λ��������ΪEMPTY
     */
    public Node(int index, Road[] summary) {
        this.index = index;
        neighbors_road = new ArrayList();
        neighbors_point = new ArrayList<>();
        for (Direction direction : Direction.NEGATIVE_FOUR_DIRECTIONS) {
            //����ÿ������λ����1~6�����������Ҫ����
            //�ҵ������ھ�λ��
            int distance = 1;
            while(distance <= 5 && util.validPosition(index, direction, distance)){
                neighbors_point.add(util.transform(index, direction, distance));
                distance++;
            }
            distance = 1;
            while(distance <= 5 && util.validPosition(index, direction.opposite(), distance)){
                neighbors_point.add(util.transform(index, direction.opposite(), distance));
                distance++;
            }

            for (int i = 0; i < 6; i++) {
                if (util.validPosition(index, direction, i)) {
                    Road road = summary[util.transform(index, direction, i)];
                    neighbors_road.add(road);
                    value += road.getValue();
                }
            }
        }
    }

    /**
     * ����ɾ����Ϊ������
     * @param index
     */
    public Node(int index){
        this.index = index;
    }

    public void calValue(){
        value = 0;
        for(Road road : neighbors_road){
            value += road.getValue();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        if (index != node.index) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public int compareTo(Node node) {
        return node.value - this.value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ArrayList<Road> getNeighbors_road() {
        return neighbors_road;
    }

    public void setNeighbors_road(ArrayList<Road> neighbors_road) {
        this.neighbors_road = neighbors_road;
    }

    public ArrayList<Integer> getNeighbors_point() {
        return neighbors_point;
    }

    public void setNeighbors_point(ArrayList<Integer> neighbors_point) {
        this.neighbors_point = neighbors_point;
    }
}
