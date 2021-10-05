public class PsgAct implements Runnable{
    Passenger psg;
    Elevator[] e = new Elevator[5];
    Floor f;
    GUI g;

    public PsgAct(Passenger ptmp, Elevator e1, Elevator e2, Elevator e3, Elevator e4,Floor f, GUI go){
        this.psg = ptmp;
        this.e[1] = e1;
        this.e[2] = e2;
        this.e[3] = e3;
        this.e[4] = e4;
        this.f = f;
        this.g = go;
    }
    int change(int x){
        if(x == 0) return -1;
        else return x;
    }

    @Override
    public void run() {
        //不停地等，不停地询问

        //添加一位等待乘客
        g.uilock.lock();
        g.waitpsg += 1;
        g.uilock.unlock();
        while (psg.state == 0){
            int flag = 0;
            //等电梯
            while (true){
                //BUS和LOOK算法
                if(e[1].scheduling_algorithm == 1 || e[1].scheduling_algorithm == 2){
                    for(int i = 1; i < 5; i++) {
                        e[i].lock.lock();
                        //电梯到这层楼、重量可以、人数可以、方向相同、电梯门开着
                        if (e[i].current_floor == psg.current_floor &&
                                psg.weight + e[i].current_lood <= e[i].max_load &&
                                e[i].current_psg < e[i].max_psg && psg.dire == e[i].dire && e[i].state == 1) {
                            flag = i;
                            //进入电梯
                            e[flag].current_psg += 1;
                            e[flag].current_lood += psg.weight;
                            //请求楼层数组减去这个人
                            f.floor[psg.current_floor][psg.dire]--;

                            //减去一位等待乘客，添加一位进电梯的乘客
                            g.uilock.lock();
                            g.waitpsg -= 1;
                            g.liftpsg += 1;
                            g.uilock.unlock();
                            System.out.println("乘客" + psg.num + "(" + change(psg.current_floor) + "->" + change(psg.target_floor) + ")" + "在" + change(psg.current_floor) + "层进入了" + flag + "号电梯,电梯里目前有" + e[flag].current_psg + "人/////////////////////////////////////////////");
                            e[i].lock.unlock();
                            break;
                        }
                        e[i].lock.unlock();
                    }
                    if(flag!=0) break;
                }
                //SSTF算法
                else if(e[1].scheduling_algorithm == 3){
                    e[psg.ele].lock.lock();
                    //电梯到这层楼、电梯门开着、方向无所谓
                    if(e[psg.ele].current_floor == psg.current_floor && e[psg.ele].state == 1) {
                        //不超载
                        if (psg.weight + e[psg.ele].current_lood <= e[psg.ele].max_load && e[psg.ele].current_psg < e[psg.ele].max_psg) {
                            flag = psg.ele;
                            //进入电梯
                            e[flag].current_psg += 1;
                            e[flag].current_lood += psg.weight;
                            e[flag].stop_floor[psg.target_floor]++;//接到乘客之后，将乘客的目标楼层加入停止队列
                            e[flag].stop_floor[psg.current_floor]--;//乘客初始楼层删除了
                            //减去一位等待乘客，添加一位进电梯的乘客
                            g.uilock.lock();
                            g.waitpsg -= 1;
                            g.liftpsg += 1;
                            g.uilock.unlock();
                            System.out.println("乘客" + psg.num + "(" + change(psg.current_floor) + "->" + change(psg.target_floor) + ")" + "在" + change(psg.current_floor) + "层进入了" + flag + "号电梯,电梯里还剩" + e[flag].current_psg + "人//////////////////////////////");
                            e[psg.ele].lock.unlock();
                            break;
                        }
                        //超载
                        else {
                            e[psg.ele].stop_floor[psg.current_floor]--;
                            e[psg.ele].lock.unlock();
                            //重新找电梯
                            while(true){
                                int Min = 100;
                                for(int i=1;i<5;i++){
                                    if(i == psg.ele) continue;
                                    //不能直达的电梯不行
                                    if(e[i].service_floor[psg.current_floor]==0
                                            ||e[i].service_floor[psg.target_floor]==0) continue;
                                    //超重超员的电梯不行
                                    if(e[i].current_psg==e[i].max_psg &&
                                            e[i].current_lood+psg.weight>=e[i].max_load) continue;
                                    //找最近的电梯
                                    int x = Math.abs(e[i].current_floor - psg.current_floor);
                                    if(x<Min){
                                        Min = x;
                                        psg.ele = i;
                                    }
                                }
                                //找到了一个新电梯
                                if(Min != 100){
                                    e[psg.ele].lock.lock();
                                    e[psg.ele].stop_floor[psg.current_floor]++;
                                    System.out.println( psg.num +"号乘客(" + psg.current_floor + "," + psg.target_floor + ")重新请求了" + psg.ele + "号电梯");
                                    e[psg.ele].lock.unlock();
                                    break;
                                }
                            }
                        }
                    }
                    else e[psg.ele].lock.unlock();
                }
            }

            //等待电梯离开本层
            while(e[flag].current_floor == psg.current_floor){
                ;
            }
            while (true){
                e[flag].lock.lock();
                if (e[flag].current_floor == psg.target_floor && e[flag].state == 1){
                    e[flag].current_psg -= 1;
                    e[flag].current_lood -= psg.weight;
                    e[flag].stop_floor[psg.target_floor] -= 1;//更新停止楼层数组

                    //减去一位在电梯里的乘客，添加一位已到达的乘客
                    g.uilock.lock();
                    g.liftpsg -= 1;
                    g.getpsg += 1;
                    g.uilock.unlock();
                    System.out.println("乘客"+psg.num+ "(" + change(psg.current_floor) + "," + change(psg.target_floor) + ")"+"在"+change(psg.target_floor)+"层出了"+flag+"号电梯,电梯里目前有" + e[flag].current_psg + "人********************************************");
                    psg.state = 1;
                    e[flag].lock.unlock();
                    break;
                }
                else if(e[flag].scheduling_algorithm <= 2 && (e[flag].current_floor == e[flag].service_max_floor || e[flag].current_floor == e[flag].service_min_floor) && e[flag].state == 1){
                    e[flag].current_psg -= 1;
                    e[flag].current_lood -= psg.weight;
                    psg.current_floor = e[flag].current_floor;//改变当前楼层
                    //更新目标楼层方向
                    if (psg.current_floor > psg.target_floor)psg.dire = 1;
                    else psg.dire = 0;
                    //减去一位在电梯里的乘客，添加一位等待的的乘客
                    g.uilock.lock();
                    g.liftpsg -= 1;
                    g.waitpsg += 1;
                    g.uilock.unlock();
                    System.out.println("乘客"+psg.num+ "(" + change(psg.current_floor) + "->" + change(psg.target_floor) + ")"+"在"+change(psg.current_floor)+"层被赶出了"+flag+"号电梯，继续等待");
                    psg.state = 0;
                    f.floor[psg.current_floor][psg.dire]++;//更新等待楼层数组
                    e[flag].lock.unlock();
                    //等待该电梯关门，上其他电梯
                    while(e[flag].state==1){
                    }
                    break;
                }
                e[flag].lock.unlock();
            }
        }
    }
}
