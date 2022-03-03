package core.game;

public class GameResult {
    private final String black;     //�ڷ�
    private final String white;     //�׷�
    private final String winner;    //ʤ��
    private final int steps;
    private final String endReason; //��ʤԭ��

    public GameResult(String black, String white, String winner, int steps, String endReason) {
        this.black = black;
        this.white = white;
        this.winner = winner;
        this.steps = steps;
        this.endReason = endReason;

    }

    /**
     * ����name�ڱ��ζԾ��еĵ÷�
     * ʤ��2�֣�ƽ��1�֣�����0��
     * @param name
     * @return
     */
    public int score(String name) {
        if ("NONE".equals(this.winner))
            return 1;
        if (name.equals(this.winner)) {
            return 2;

        }
        return 0;
    }

    /**
     * ��ȡ����name�ڱ��ζԾ��еĶ���
     * @param name
     * @return name�Ķ��ֵ�����
     */
    public String getOpponent(String name){
        if (this.black.equals(name)){
            return this.white;
        }
        return this.black;
    }
    public String toString() {
        return "" + this.endReason;
    }

//    public String toString() {
//        return "\t���壺" + this.black + "\n\t���壺" + this.white + "\n\tʤ����" + this.winner + "\n\t������" + this.steps + "\n\t����ԭ��" + this.endReason + "\n";
//
//    }
}
