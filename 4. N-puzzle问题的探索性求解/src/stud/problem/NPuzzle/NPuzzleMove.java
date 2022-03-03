package stud.problem.NPuzzle;

import core.problem.Action;

public class NPuzzleMove extends Action {

    //每一步的Action
    private NPuzzleDirection direction;
    private static int count = 1;

    public NPuzzleDirection getDirection() {    //取方向
        return direction;
    }

    public void setDirection(NPuzzleDirection direction) {  //方向
        this.direction = direction;
    }

    public static int getCount() {    //取数
        return count;
    }

    public static void setCount(int count) {  //计数器
        NPuzzleMove.count = count;
    }

    public NPuzzleMove(NPuzzleDirection dir){  //构造函数
        this.direction =dir;
    }


    @Override
    public void draw() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return direction.name();
    }

    @Override
    public int stepCost() {
        return 1;
    }

}
