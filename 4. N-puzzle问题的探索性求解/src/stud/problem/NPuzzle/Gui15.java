package stud.problem.NPuzzle;
import core.solver.queue.Node;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Gui15 extends JFrame {    //继承JFrame顶层容器类

    private static int total = 0;
    private static int an = 0;
    //定义组件
    ArrayList<JPanel> JpList = new ArrayList();
    ArrayList<JLabel> JlList = new ArrayList();

    JPanel jp1,jp2,jp3,jp4,jp5,jp6,jp7,jp8,jp9,jp10,jp11,jp12,jp13,jp14,jp15,jp16;
    JLabel jlb1,jlb2,jlb3,jlb4,jlb5,jlb6,jlb7,jlb8,jlb9,jlb10,jlb11,jlb12,jlb13,jlb14,jlb15,jlb16;

    public Gui15(Deque<Node> path)        //构造函数
    {
        init();

        for (int k=0;k<16;k++){
            this.add(JpList.get(k));

            JlList.get(k).setFont(new java.awt.Font(null,1, 50));
            JpList.get(k).add(JlList.get(k));
        }

        this.setLayout(new GridLayout(4,4));
        this.setTitle("NPuzzle问题可视化");    //创建界面标题
        this.setSize(400, 400);        //设置界面像素
        this.setLocation(500, 100);    //设置界面初始位置
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //设置界面和虚拟机一起关闭
        this.setVisible(true);    //设置界面可显示


        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (path.size()!=0){
                total++;
                Node node = path.removeFirst();
                int[][] state = ((NPuzzleState)(node.getState())).getStates();
                int size = state.length;
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (state[i][j] != 0) {
                            JlList.get(4*i+j).setText(String.valueOf(state[i][j]));
                        } else {
                            JlList.get(4*i+j).setText(" ");
                        }
                    }
                }
            }

            try {
                Thread.sleep(500);
                if (path.size()!=0){
                    Node node = path.removeFirst();
                    int[][] state = ((NPuzzleState)(node.getState())).getStates();
                    int size = state.length;
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            if (state[i][j] != 0) {
                                JlList.get(4*i+j).setText(String.valueOf(state[i][j]));
                            } else {
                                JlList.get(4*i+j).setText(" ");
                            }
                        }
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, (long) 0.5, (long) 1, TimeUnit.SECONDS);





    }

    private void init() {

        //创建组件
        jp1=new JPanel();    //创建三个面板
        jp2=new JPanel();
        jp3=new JPanel();
        jp4=new JPanel();

        jp5=new JPanel();
        jp6=new JPanel();
        jp7=new JPanel();
        jp8=new JPanel();

        jp9=new JPanel();
        jp10=new JPanel();
        jp11=new JPanel();
        jp12=new JPanel();

        jp13=new JPanel();
        jp14=new JPanel();
        jp15=new JPanel();
        jp16=new JPanel();

        jlb1=new JLabel("");
        jlb2=new JLabel("");
        jlb3=new JLabel("");
        jlb4=new JLabel("");

        jlb5=new JLabel("");
        jlb6=new JLabel("");
        jlb7=new JLabel("");
        jlb8=new JLabel("");

        jlb9=new JLabel("");
        jlb10=new JLabel("");
        jlb11=new JLabel("");
        jlb12=new JLabel("");

        jlb13=new JLabel("");
        jlb14=new JLabel("");
        jlb15=new JLabel("");
        jlb16=new JLabel("");

        JpList.add(jp1);
        JpList.add(jp2);
        JpList.add(jp3);
        JpList.add(jp4);
        JpList.add(jp5);
        JpList.add(jp6);
        JpList.add(jp7);
        JpList.add(jp8);
        JpList.add(jp9);
        JpList.add(jp10);
        JpList.add(jp11);
        JpList.add(jp12);
        JpList.add(jp13);
        JpList.add(jp14);
        JpList.add(jp15);
        JpList.add(jp16);

        JlList.add(jlb1);
        JlList.add(jlb2);
        JlList.add(jlb3);
        JlList.add(jlb4);
        JlList.add(jlb5);
        JlList.add(jlb6);
        JlList.add(jlb7);
        JlList.add(jlb8);
        JlList.add(jlb9);
        JlList.add(jlb10);
        JlList.add(jlb11);
        JlList.add(jlb12);
        JlList.add(jlb13);
        JlList.add(jlb14);
        JlList.add(jlb15);
        JlList.add(jlb16);





    }
}

