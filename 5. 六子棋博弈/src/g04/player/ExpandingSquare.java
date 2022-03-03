package g04.player;

import core.board.Board;
import core.board.PieceColor;

import java.util.*;

//扩张窗口
public class ExpandingSquare {
    private int offset;//里左上角的斜向距离
    private int length;//窗口长度
    private int start_index;//起始坐标
    private PriorityQueue<Node> q;//以位置的估值进行排序的优先队列
    private HashMap<Integer, Node> map;//存储下标及位置节点的映射

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
            //创建新节点
            Node node1 = new Node(index1, summary); Node node2 = new Node(index2, summary);
            //更新q
            if(q.contains(node1)) q.remove(node1);if(q.contains(node2)) q.remove(node2);
            q.add(node1); q.add(node2);
            //更新map
            map.put(index1, node1); map.put(index2, node2);
            //在map更新后再获取邻居位置列表
            neighbors_point1 = map.get(index1).getNeighbors_point(); neighbors_point2 = map.get(index2).getNeighbors_point();
        }
        //更新相关位置的估值
        //TODO:优化点：当前的位置估值计算方法放弃了历史信息，可以考虑结合方向进行优化
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
                //如果在上边界(i == offset)或者在下边界(i == offset + length - 1), 就加入一整行
                if(i == offset || i == offset + length - 1){
                    for(int j = offset; j < offset + length; j++){
                        if(board.get(i * 19 + j) == PieceColor.EMPTY){
                            int index = i * 19 + j;
                            Node newNode = new Node(index, summary);
                            q.add(newNode);
                            map.put(index, newNode);
                        }
                    }
                }else {//否则在中间部分，只加入左边界和右边界上的位置
                    //左边界
                    if(board.get(i * 19 + offset) == PieceColor.EMPTY){
                        int index = i * 19 + offset;
                        Node newNode = new Node(index, summary);
                        q.add(newNode);
                        map.put(index, newNode);
                    }
                    //右边界
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
