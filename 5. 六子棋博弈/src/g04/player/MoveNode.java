package g04.player;

import core.game.Move;

public class MoveNode implements Comparable<MoveNode>{
    private Move move;
    private int value;

    public MoveNode(Move move, int value) {
        this.move = move;
        this.value = value;
    }

    @Override
    public int compareTo(MoveNode moveNode) {
        return moveNode.value - this.getValue();
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
