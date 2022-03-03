package stud.queue;

import java.security.SecureRandom;

public class Zobrist {
    private static int[][] zobrist;
    public Zobrist(int size) {
        SecureRandom rand = new SecureRandom();
        zobrist = new int[size*size][];
        for(int i=0;i<size*size;i++){                    //构造棋盘
            zobrist[i]= new int[size*size];
            for(int j=0;j<size*size;j++){
                zobrist[i][j]=rand.nextInt();
            }
        }
    }

    public static int[][] getZobrist() {
        return zobrist;
    }
}
