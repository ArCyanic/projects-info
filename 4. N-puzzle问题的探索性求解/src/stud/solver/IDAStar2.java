package stud.solver;

import core.problem.Problem;
import core.problem.State;
import core.solver.algorithm.Searcher;
import core.solver.algorithm.heuristic.EvaluationType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.problem.NPuzzle.NPuzzleState;
import stud.queue.ListFrontier;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 迭代加深的A*算法，需要同学们自己编写完成
 */
public class IDAStar2 implements Searcher {
    Predictor predictor;

    // 已经访问过的节点集合
    private final Set<State> explored;
    // 还未扩展的节点队列
    private final Frontier frontier;
    private final Frontier localFrontier;
    // 返回的结果
    private Deque<Node> result;

    public IDAStar2(Predictor predictor) {
        System.out.println("constructor");
        //使用AStar的代价函数
        frontier = new ListFrontier(Node.evaluator(EvaluationType.FULL));
        localFrontier = new ListFrontier(Node.evaluator(EvaluationType.FULL));

        explored = new HashSet<>();
        this.predictor = predictor;
    }

    @Override
    public Deque<Node> search(Problem problem) {


        // 先判断问题是否可解，无解时直接返回解路径为null
        if (!problem.solvable()) {
            System.out.println("No solution???");
            return null;
        }

        // 清空上次搜索的Frontier和Explored，重新开始搜索
        /// 需要清除吗？？？
        frontier.clear();
        explored.clear();

        // 起始节点root
        Node root = problem.root(predictor);

        //添加根节点
        frontier.offer(root);

        depthFirstSearch(problem,root,0,true);


        return result;
    }

    private Deque<Node> depthFirstSearch(Problem problem,Node node, int currentCostBound) {

        //判断是否可以结束迭代了
        if (problem.goal(node.getState())) {
            return generatePath(node);
        }
        //将当前节点添加到已经探索的节点中
        explored.add(node.getState());

        if(currentCostBound % 1000 == 0)return null;
        //获取扩展的节点列表
        List<Node> children =  problem.childNodes(node, predictor);


        //System.out.println(currentCostBound);
        //对于每一个扩展后的节点
        for (Node next : children){
            //计算当前的Cost
            int value = next.getPathCost() + next.getHeuristic();

            //如果当前的Cost比较小，则符合条件，加入队列
            if (currentCostBound >= value) {
                frontier.offer(next);

                Deque<Node> result = depthFirstSearch(problem,next, currentCostBound);

                if (result != null){
                    return result;
                }
            }
        }
        return null;
    }
    /**
     * 深度优先+AStar
     * 原理：
     *  1. 从根节点开始，递归生成子节点，构建一个解集树
     *  2. 对于每一个节点：生成其子节点，从子结点中选择cost最低的(Pathcost + Heuristic)
     *  3. 比较cost和深度depth，如果深度大于cost，则说明递归产生的cost大于AStar的cost，即走了弯路
     *  4. 相反，则说明递归的cost小于AStar的cost，可以继续沿着该路走
     * */
    private void depthFirstSearch(Problem problem,Node node, int depth,boolean isNew) {

        //判断是否可以结束迭代了，即该状态是最终状态
        if (problem.goal(node.getState())) {
            result = generatePath(node);
            return;
        }
        //将当前节点添加到已经探索的节点中
        explored.add(node.getState());
        //清空局部优先队列
        localFrontier.clear();


        //首先将子节点加入扩展节点队列中，再加入局部优先队列中
        for (Node next : problem.childNodes(node, predictor)) {
            if (!expanded(next)) {//如果该节点没被扩展，则添加进去，去重
                frontier.offer(next);
                localFrontier.offer(next);
            }
        }


        //结束条件是兄弟节点全部遍历完
        while(!localFrontier.isEmpty()){
            Node lowerCost = localFrontier.poll();//选出cost最低的一个Node
            //计算当前的cost和heuristic的和，即从起点到现在状态的cost加上从现在状态到终点的cost
            int value = lowerCost.getPathCost()+lowerCost.getHeuristic();
            //如果当前解树的深度大于value，说明这条路是弯路，立刻返回
            if(value > depth) {
                //否则，一条路走到黑
                depthFirstSearch(problem,lowerCost,depth+1,true);
            }

        }

    }


    @Override
    public int nodesExpanded() {
        return frontier.size();
//        return front;
    }

    @Override
    public int nodesGenerated() {
        return explored.size()+frontier.size();
//        return exped + front;
    }

    private boolean expanded(Node child) {
        return explored.contains(child.getState());
    }
}
