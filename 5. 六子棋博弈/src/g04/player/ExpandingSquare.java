package g04.player;

import core.board.Board;
import core.board.PieceColor;

import java.util.*;

//���Ŵ���
public class ExpandingSquare {
    private int offset;//�����Ͻǵ�б�����
    private int length;//���ڳ���
    private int start_index;//��ʼ����
    private PriorityQueue<Node> q;//��λ�õĹ�ֵ������������ȶ���
    private HashMap<Integer, Node> map;//�洢�±꼰λ�ýڵ��ӳ��

    ExpandingSquare(Road[] summary){
        offset = 6;
        start_index = offset * 19 + offset;
        length = (9 - offset) * 2 + 1;
        q = new PriorityQueue<>();
        map = new HashMap<>();

        for(int i = offset; i < offset + length; i++){
            for(int j = offset; j < offset + length; j++){
                int index = i * 19 + j;
                if(index != 180){
                    Node newNode = new Node(index, summary);
                    q.offer(newNode);
                    map.put(index, newNode);
                }

            }
        }
    }

    public void update(int index1, int index2, char instruction, Road[] summary){
        ArrayList<Integer> neighbors_point1 = null;
        ArrayList<Integer> neighbors_point2 = null;
        if(instruction == 'd'){
            if(map.containsKey(index1)){
                q.remove(new Node(index1));
                neighbors_point1 = map.get(index1).getNeighbors_point();
                map.remove(index1);
            }
            if(map.containsKey(index2)){
                q.remove(new Node(index2));
                neighbors_point1 = map.get(index2).getNeighbors_point();
                map.remove(index2);
            }
        }else if(instruction == 'a'){
            //�����½ڵ�
            Node node1 = new Node(index1, summary); Node node2 = new Node(index2, summary);
            //����q
            if(q.contains(node1)) q.remove(node1);if(q.contains(node2)) q.remove(node2);
            q.add(node1); q.add(node2);
            //����map
            map.put(index1, node1); map.put(index2, node2);
            //��map���º��ٻ�ȡ�ھ�λ���б�
            neighbors_point1 = map.get(index1).getNeighbors_point(); neighbors_point2 = map.get(index2).getNeighbors_point();
        }
        //�������λ�õĹ�ֵ
        //TODO:�Ż��㣺��ǰ��λ�ù�ֵ���㷽����������ʷ��Ϣ�����Կ��ǽ�Ϸ�������Ż�
        Set<Integer> set = new HashSet<>();
        if(neighbors_point1 != null){
            set.addAll(neighbors_point1);
        }
        if(neighbors_point2 != null){
            set.addAll(neighbors_point2);
        }
        if(set.size() > 0){
            for(int i : set){
                if(map.containsKey(i)){
                    map.get(i).calValue();
                }
            }
        }

    }

    public void expand(Board board, Road[] summary) {
        if(offset > 0){
            offset--;
            length -= 2;
            start_index -= 20;
            for(int i = offset; i < offset + length; i++){
                //������ϱ߽�(i == offset)�������±߽�(i == offset + length - 1), �ͼ���һ����
                if(i == offset || i == offset + length - 1){
                    for(int j = offset; j < offset + length; j++){
                        if(board.get(i * 19 + j) == PieceColor.EMPTY){
                            int index = i * 19 + j;
                            Node newNode = new Node(index, summary);
                            q.add(newNode);
                            map.put(index, newNode);
                        }
                    }
                }else {//�������м䲿�֣�ֻ������߽���ұ߽��ϵ�λ��
                    //��߽�
                    if(board.get(i * 19 + offset) == PieceColor.EMPTY){
                        int index = i * 19 + offset;
                        Node newNode = new Node(index, summary);
                        q.add(newNode);
                        map.put(index, newNode);
                    }
                    //�ұ߽�
                    if(board.get(i * 19 + offset + length - 1) == PieceColor.EMPTY){
                        int index = i * 19 + offset + length - 1;
                        Node newNode = new Node(index, summary);
                        q.add(newNode);
                        map.put(index, newNode);
                    }
                }
            }
        }
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public int getStart_index() {
        return start_index;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setStart_index(int start_index) {
        this.start_index = start_index;
    }

    public PriorityQueue<Node> getQ() {
        return q;
    }

    public void setQ(PriorityQueue<Node> q) {
        this.q = q;
    }
}
