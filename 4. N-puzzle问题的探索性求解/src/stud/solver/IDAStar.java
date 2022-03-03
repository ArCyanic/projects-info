package stud.solver;

import core.problem.Problem;
import core.solver.algorithm.Searcher;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.Node;

import java.util.*;

/**
 * 迭代加深的A*算法，需要同学们自己编写完成
 */

public class IDAStar implements Searcher {
    Predictor predictor;
    private final Set<Integer> explored = new HashSet<>();
    private final Set<Integer> expanded = new HashSet<>();

    // 返回的结果
    Deque<Node> path = new ArrayDeque<>();
    Problem problem;
    public IDAStar(Predictor predictor) {
        //FixMe
        this.predictor = predictor;
    }

    @Override
    public Deque<Node> search(Problem pro) {
        problem = pro;
        if (!problem.solvable()) {
            return null;
        }
        // 起始节点root
        Node root = problem.root(predictor);
        //最大探索深度
        int maxDepth = root.getHeuristic();

        while (!dfs(root, null, maxDepth))
        {
            maxDepth++;
        }
        return path;
    }


    /**
     *
     * @param node
     * @param pre 上一方向
     * @param maxDepth
     * @return
     */
    boolean dfs(Node node, Node pre, int maxDepth){
        if (node.getPathCost() >= maxDepth)   //剪枝
        {
            return false;
        }
        if (problem.goal(node.getState()))
        {
            //回溯得到路径
            path = generatePath(node);
            return true;
        }
        expanded.add(node.getState().hashCode());
        List<Node> children = problem.childNodes(node, predictor);

        for (var child : children){
            explored.add(child.getState().hashCode());
            //确保不会回到原来的方向
            if (!(pre == null || !child.getState().equals(pre.getState())))
                continue;
            if (child.evaluation() < maxDepth && dfs(child, node, maxDepth)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int nodesExpanded() {
        return expanded.size();
    }

    @Override
    public int nodesGenerated() {
        return explored.size();
    }
}
