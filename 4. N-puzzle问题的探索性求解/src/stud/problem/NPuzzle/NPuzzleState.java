package stud.problem.NPuzzle;

import core.problem.Action;

import core.problem.State;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import stud.queue.Zobrist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;

import static core.solver.algorithm.heuristic.HeuristicType.*;

public class NPuzzleState extends State {

    private int size = 0;
    private int hash = 0;
    private int[][] states;
    private int col = 0;
    private int row = 0;
    private int manhattan = 0;
    private int[][] zobrist;

    public void setHash(int hash) {
        this.hash = hash;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getManhattan() {
        return manhattan;
    }

    public void setManhattan(int manhattan) {
        this.manhattan = manhattan;
    }

    public int[][] getStates() {
        return states;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    //拷贝构造函数
    public NPuzzleState(NPuzzleState state) {
        this.size = state.getSize();
        this.states = new int[size][];
        for (int i = 0; i < state.getSize(); i++) {
            this.states[i] = new int[size];
            for (int j = 0; j < state.getSize(); j++) {
                this.states[i][j] = state.getStates()[i][j];
            }
        }
        this.zobrist = state.zobrist;
    }

    //一般构造函数, 只用于初始化根节点和目标节点
    public NPuzzleState(int size, int[] board, boolean isRoot) {
        //initiate size
        this.size = size;
        //initiate states
        this.states = new int[size][];
        for (int i = 0; i < size; i++) {
            this.states[i] = new int[size];
            for (int j = 0; j < size; j++) {
                this.states[i][j] = board[i * size + j];
                if (this.states[i][j] == 0) {
                    row = i;
                    col = j;
                }
            }
        }

        //因为要动态改变manhattan和hash code，所以只对根节点进行初始计算，后续进行动态计算
        //只需要初始化根节点的的曼哈顿距离，而且只在初始化根节点的时候构造一个zobristSeed
        if (isRoot) {
            //initiate manhattan
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (states[i][j] == 0) continue;
                    manhattan += Math.abs((states[i][j] - 1) / size - i) + Math.abs((states[i][j] - 1) % size - j);
                }
            }
            Zobrist z = new Zobrist(size);
            this.zobrist = z.getZobrist();
        }
        else{
            this.zobrist = Zobrist.getZobrist();
        }
        //initiate zobrist hash code
        //注意目标节点也要进行hash初始化
        for (int i = 0; i < size * size; i++) {
            if (states[i / size][i % size] != 0) {
                hash ^= zobrist[i][states[i / size][i % size]];
            }
        }
    }

    public int Manhattan() {
        return this.manhattan;
    }

    public int Misplaced() {
        int ans = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (states[i][j] == 0 && i * size + j + 1 == size * size) {
                    continue;
                }
                if (states[i][j] != i * size + j + 1) {
                    ans++;
                }
            }
        }

        return ans;
    }

    private static final EnumMap<HeuristicType, Predictor> predictors = new EnumMap<>(HeuristicType.class);

    static {
//        predictors.put(PF_EUCLID,
//                (state, goal) -> ((NPuzzleState)state).disjoint_pattern((NPuzzleState)goal));
        //TODO: re-parameter the anonymous function
        predictors.put(MANHATTAN,
                (state, goal) -> ((NPuzzleState) state).Manhattan());
        predictors.put(MISPLACED,
                (state, goal) -> ((NPuzzleState) state).Misplaced());
    }

    public static Predictor predictor(HeuristicType type) {
        return predictors.get(type);
    }

    //绘图输出
    //TODO: re-write this function
    @Override
    public void draw() {               //用于输出打印
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (states[i][j] != 0) {
                    System.out.print(states[i][j] + " ");
                } else {
                    System.out.print("# ");
                }
            }
            System.out.println();
        }
    }

    public static void printState(int[][] state){
        int size = state.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (state[i][j] != 0) {
                    System.out.print(state[i][j] + " ");
                } else {
                    System.out.print("# ");
                }
            }
            System.out.println();
        }
    }

    @Override
    public State next(Action action) {

        //1 当前Action所带来的位移量
        NPuzzleDirection dir = ((NPuzzleMove) action).getDirection();
        int[] offsets = NPuzzleDirection.offset(dir);

        //2 新的行列值
        int newRow = row + offsets[0];
        int newCol = col + offsets[1];

        //3 交换两个slide的数值
        //3.1 生成新的state实例
        NPuzzleState newState = new NPuzzleState(this);
        //交换数值
        int temp = states[newRow][newCol];
        newState.states[row][col] = temp;
        newState.states[newRow][newCol] = 0;

        //提取出新的states
        int[][] newStates = newState.getStates();

        //3.2 修改Col和Row成员变量的数值
        newState.setCol(newCol); newState.setRow(newRow);
        //3.3 动态修改Manhattan值
        //新的曼哈顿值 = 旧的曼哈顿值 - 旧棋盘中新位置的参量 + 新棋盘中旧位置的参量
        int old = Math.abs((temp - 1) / size - newRow) + Math.abs((temp - 1) % size - newCol);
        int fresh = Math.abs((temp - 1) / size - row) + Math.abs((temp - 1) % size - col);
        newState.setManhattan(manhattan - old + fresh);

        //3.4 动态修改zobrist hash code
        //新的hash = 旧的hash ^ 旧棋盘中新位置的参量 ^ 新棋盘中旧位置的参量
        old = zobrist[newRow * size + newCol][temp];
        fresh = zobrist[row * size + col][temp];
        newState.setHash(hash ^ old ^ fresh);
        return newState;
    }

    @Override
    public Iterable<? extends Action> actions() {
        Collection<NPuzzleMove> moves = new ArrayList<>();
        for (NPuzzleDirection d : NPuzzleDirection.FOUR_DIRECTIONS)
            moves.add(new NPuzzleMove(d));
        return moves;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {                    //用于比较两个棋盘是否相同
        return this.hash == (obj).hashCode();
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }

}
