import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GUI<ReaderListen> extends JFrame{
    Elevator[] e = new Elevator[5];
    private Font font1;
    private JLabel tmp, tmp1;
    private JLabel[] d = new JLabel[5];
    private Object lock=new Object();
    private JLabel st;
    public int waitpsg, liftpsg, getpsg;
    Lock uilock = new ReentrantLock();

    public GUI(Elevator e1, Elevator e2, Elevator e3, Elevator e4) {
        this.e[1] = e1;
        this.e[2] = e2;
        this.e[3] = e3;
        this.e[4] = e4;

        this.font1 = new Font("微软雅黑", Font.BOLD, 17);
        this.tmp = new JLabel("");
        this.tmp1 = new JLabel("");
        init();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }
    int change(int x){
        if(x == 0) return -1;
        else return x;
    }

    private Timer time;
    //时间显示
    private JLabel getTimelabel() {

        JLabel timelabel = new JLabel("");
        timelabel.setBounds(30, 20, 400, 20);
        timelabel.setFont(font1);
        time = new Timer(1000,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timelabel.setText(new SimpleDateFormat("yyyy年MM月dd日 EEEE hh:mm:ss").format(new Date()));
            }
        });
        time.start();
        return timelabel;
    }

    void init(){
        add(getTimelabel());

        //添加时间和电梯图片
        ImageIcon icon = new ImageIcon("src\\eleclose.png");
        JLabel t1 = new JLabel(icon);
        JLabel t2 = new JLabel(icon);
        JLabel t3 = new JLabel(icon);
        JLabel t4 = new JLabel(icon);
        t1.setBounds(25,103,160,220);
        t2.setBounds(185,103,160,220);
        t3.setBounds(345,103,160,220);
        t4.setBounds(505,103,160,220);
        add(t1);
        add(t2);
        add(t3);
        add(t4);

        //显示当前乘客状态
        st = new JLabel();
        st.setText("当前有" + this.waitpsg + "位乘客在等待电梯，有" + this.liftpsg + "位乘客在电梯中，有" + this.getpsg + "位已达到目标楼层");
        st.setBounds(30,350,600,30);
        st.setFont(font1);
        add(st);
        add(tmp1);

        //设置电梯状态等的字体和位置
        for(int i = 1; i < 5; i++){
            d[i] = new JLabel();
            d[i].setFont(font1);
        }
        d[1].setBounds(70,80, 160, 25);
        d[2].setBounds(230,80, 160, 25);
        d[3].setBounds(390,80, 160, 25);
        d[4].setBounds(550,80, 160, 25);

        //显示电梯状态
        for(int i = 1; i < 5; i++){
            show_disp(i);
        }
    }

    private void show_disp(int ep){
        String dir;
        if(e[ep].dire == 0){
            dir = "↑";
        }
        else {
            dir = "↓";
        }
        d[ep].setText(e[ep].current_psg + "人 " + change(e[ep].current_floor) + "F " + dir);
        add(d[ep]);
        add(tmp);
    }

    void remove_dis(int ep){
        synchronized (lock) {
            remove(d[ep]);
            remove(tmp);
            repaint();
            show_disp(ep);
        }
    }

    void renew_psg(){
        synchronized (lock){
            remove(st);
            remove(tmp1);
            repaint();
            st.setText("当前有" + this.waitpsg + "位乘客在等待电梯，有" + this.liftpsg + "位乘客在电梯中，有" + this.getpsg + "位已达到目标楼层");
            add(st);
            add(tmp1);
        }
    }
}