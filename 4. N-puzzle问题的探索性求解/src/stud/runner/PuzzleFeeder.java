package stud.runner;

import core.problem.Problem;
import core.runner.EngineFeeder;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import stud.problem.NPuzzle.NPuzzle;
import stud.problem.NPuzzle.NPuzzleState;
import stud.queue.Zobrist;

import java.util.ArrayList;

/**
 * 寻路问题的EngineFeeder。
 * 同学们可以参考编写自己的PuzzleFeeder
 */
public class PuzzleFeeder extends EngineFeeder {
    @Override
    public ArrayList<Problem> getProblems(ArrayList<String> problemLines) {
        int size = problemLines.size();
        int[][] states = getStates(problemLines, size);

        /* 读入各个问题 */
        ArrayList<Problem> problems = new ArrayList<>();
        int lineNo = 0;
        while (lineNo < problemLines.size()){
            //读入问题实例
            NPuzzle problem = getNPuzzle(problemLines.get(lineNo));
            //添加到问题列表
            problems.add(problem);
            lineNo++;
        } //读入问题结束

        return problems;
    }

    /**
     * 生成寻路问题的一个实例
     * @param problemLine
     * @return
     */
    private NPuzzle getNPuzzle(String problemLine) {
        String[] strings = problemLine.split(" ");
        int size = Integer.parseInt(strings[0]);
        int[] initial_state = new int[size * size];
        int[] goal = new int[size * size];
        for(int i = 1;i <= size * size;i++){
            initial_state[i - 1] = Integer.parseInt(strings[i]);
            goal[i - 1] = Integer.parseInt(strings[i + size * size]);
        }

        //在生成问题实例之前先生成zobrist hash置换表，利用static变量的全局性
        new Zobrist(size);

        //生成寻路问题的实例
        return new NPuzzle(new NPuzzleState(size, initial_state, true),new NPuzzleState(size, goal, false));
    }

    /**
     *
     * @param problemLines
     * @param size
     * @return
     */

    private int[][] getStates(ArrayList<String> problemLines, int size) {
        int[][] states = new int[size][];
        for (int i = 0; i < size; i++){
            String[] strings = problemLines.get(i).split(" ");
            int[] numbers = new int[strings.length];
            for(String n : strings){
                numbers[i] = Integer.parseInt(n);
            }
            states[i] = numbers;
        }
        return states;
    }

    /**
     * 获得对状态进行估值的Predictor
     *
     * @param type 估值函数的类型
     * @return  估值函数
     */
    @Override
    public Predictor getPredictor(HeuristicType type) {
        return NPuzzleState.predictor(type);
    }

}
