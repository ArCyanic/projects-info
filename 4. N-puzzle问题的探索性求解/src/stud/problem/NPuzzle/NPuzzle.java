package stud.problem.NPuzzle;

import core.problem.Action;
import core.problem.Problem;
import core.problem.State;
import core.solver.queue.Node;

import java.util.Deque;

public class NPuzzle extends Problem {

    public void setSize(int size) {    //问题规模
        this.size = size;
    }

    private int getInversions(int[][] state) {
        int inversion = 0;
        int temp = 0;
        for(int i=0;i<state.length;i++) {
            for(int j=0;j<state[i].length;j++) {
                int index = i* state.length + j + 1;
                while(index < (state.length * state.length)) {
                    if(state[index/state.length][index%state.length] != 0
                            && state[index/state.length]
                            [index%state.length] < state[i][j]) {
                        temp ++;
                    }
                    index ++;
                }
                inversion = temp + inversion;
                temp = 0;
            }
        }
        return inversion;
    }

    @Override
    public boolean solvable() {    //问题是否可解，解题开头直接调用
        int[][] state = ((NPuzzleState)getInitialState()).getStates();

        if(state.length % 2 == 1) { //问题宽度为奇数
            return (getInversions(state) % 2 == 0);
        } else { //问题宽度为偶数
            if((state.length - ((NPuzzleState)getInitialState()).getRow()) % 2 == 1) { //从底往上数,空格位于奇数行
                return (getInversions(state) % 2 == 0);
            } else { //从底往上数,空位位于偶数行
                return (getInversions(state) % 2 == 1);
            }
        }
    }

    public State getInitialState() {   //获得状态对象
        return initialState;
    }

    @Override
    public int stepCost(State state, Action action) { //本问题不需要
        return 1;
    }

    @Override
    public boolean applicable(State state, Action action) {
        int[] offsets = NPuzzleDirection.offset(((NPuzzleMove)action).getDirection());
        int row = ((NPuzzleState)state).getRow() + offsets[0];
        int col = ((NPuzzleState)state).getCol() + offsets[1];
        return row >= 0 && row < size &&
                col >= 0 && col < size;
    }

    @Override
    public void showSolution(Deque<Node> path) {
        Node node = path.getFirst();
        if (((NPuzzleState)(node.getState())).getStates().length==4){
            Gui15 a=new Gui15(path);    //显示界面
        }
        else if (((NPuzzleState)(node.getState())).getStates().length==3){
            Gui8 a=new Gui8(path);    //显示界面
        }

    }

    public NPuzzle(State initialState, State goal) {  //构造函数
        super(initialState, goal, ((NPuzzleState)(initialState)).getSize());
    }
}
