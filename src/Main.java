import java.util.Random;

public class Main {
    private static int ele_num = 4;
    private static int psg_num = 20;
    static Elevator[] e = new Elevator[ele_num + 5];
    static Passenger[] psg = new Passenger[psg_num + 5];
    static Floor floor = new Floor();
    static GUI g;
    static renewpag r;
    public static int change(int x){
        if(x == 0) return -1;
        else return x;
    }

    public void init(){
        //四个电梯，注意各电梯的服务范围，-1层用0层表示
        //电梯一、二、三、四
        for(int epoch = 1; epoch <= ele_num; epoch ++){
            e[epoch] = new Elevator(epoch);
            e[epoch].LOOK_max_floor = 1;
            e[epoch].LOOK_min_floor = 1;
        }
        //初始化乘客
        for(int ph = 0; ph < psg_num; ph++){
            Random random = new Random();
            int we = random.nextInt(81) + 40;//体重
            int cr = random.nextInt(21);//当前楼层
            int tr = random.nextInt(21);//目标楼层
            //当前楼层和目标楼层不能相同
            while (tr == cr) {
                tr = random.nextInt(21);
            }
            psg[ph] = new Passenger(ph, we, cr, tr);
        }

        //初始化界面
        g = new GUI(e[1], e[2], e[3], e[4]);
        g.setBounds(300,150,720,500);
        g.setTitle("电梯调度");

        r = new renewpag(g);
    }

    public static void main(String[] args) throws InterruptedException {
        Main m = new Main();
        m.init();
        new Thread(r).start();
        int scheduling_algorithm = 2;//电梯采用的算法

        //BUS算法
        if(scheduling_algorithm == 1){
            Bus[] b = new Bus[ele_num + 5];
            for (int i = 1; i <= ele_num; i++) {
                b[i] = new Bus(e[i],g);
                new Thread(b[i]).start();
            }
            PsgAct[] p = new PsgAct[psg_num + 5];
            for(int pe = 0; pe < psg_num; pe++){
                System.out.println("乘客"+psg[pe].num + "(" + change(psg[pe].current_floor) + "->" + change(psg[pe].target_floor) + ")在" + change(psg[pe].current_floor) + "层等待");
                p[pe] = new PsgAct(psg[pe], e[1],e[2],e[3],e[4],floor,g);
                new Thread(p[pe]).start();
            }
        }
        //LOOK算法
        else if(scheduling_algorithm == 2){
            LOOK[] looks = new LOOK[ele_num + 5];
            for (int i = 1; i <= ele_num; i++) {
                looks[i] = new LOOK(e[i],floor,g);
                new Thread(looks[i]).start();
            }
            PsgAct[] p = new PsgAct[psg_num + 5];
            for(int pe = 0; pe < psg_num; pe++){
                //请求楼层数组中加入这个人
                floor.floor[psg[pe].current_floor][psg[pe].dire]++;
                System.out.println("乘客"+psg[pe].num + "(" + change(psg[pe].current_floor) + "->" + change(psg[pe].target_floor) + ")在" + change(psg[pe].current_floor) + "层等待");
                p[pe] = new PsgAct(psg[pe], e[1],e[2],e[3],e[4],floor,g);
//                if(pe % 5 == 0){
//                Thread.sleep(60000);
//                }
                new Thread(p[pe]).start();
            }
        }
        //SSTF
        else if(scheduling_algorithm == 3){
            SSTF[] s = new SSTF[ele_num + 5];
            for(int i = 1; i <= ele_num; i++){
                s[i] = new SSTF(e[i],g);
                new Thread(s[i]).start();
            }
            PsgAct[] p = new PsgAct[psg_num + 5];
            for(int pe = 0; pe < psg_num; pe++){
                int Min = 100;
                int diantihao = 1;
                for(int epoch=1;epoch<5;epoch++){
                    //不能直达的电梯不行
                    if(e[epoch].service_floor[psg[pe].current_floor]==0
                            ||e[epoch].service_floor[psg[pe].target_floor]==0) continue;
                    //超重超员的电梯不行
                    if(e[epoch].current_psg==e[epoch].max_psg &&
                            e[epoch].current_lood+psg[pe].weight>=e[epoch].max_load) continue;
                    //找最近的电梯
                    int x = Math.abs(e[epoch].current_floor-psg[pe].current_floor);
                    if(x<Min){
                        Min = x;
                        diantihao = epoch;
                    }
                }
                e[diantihao].stop_floor[psg[pe].current_floor]++;
                psg[pe].ele = diantihao;
                System.out.println( pe +"号乘客(" + change(psg[pe].current_floor) + "," + change(psg[pe].target_floor) + ")请求" + diantihao + "号电梯");
                p[pe] = new PsgAct(psg[pe], e[1],e[2],e[3],e[4],floor,g);
                new Thread(p[pe]).start();
                //Thread.sleep(1000);
            }
        }
    }

    //用于gui中更新乘客的状态
    private class renewpag implements Runnable{
        GUI g;
        public renewpag(GUI go){
            this.g = go;
        }

        @Override
        public void run() {

            while (true){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                g.renew_psg();
            }
        }
    }
}
