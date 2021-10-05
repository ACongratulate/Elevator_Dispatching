public class SSTF implements Runnable{
    Elevator e;
    GUI g;

    public SSTF(Elevator el, GUI go){
        this.g = go;
        this.e = el;
        this.e.scheduling_algorithm = 3;
    }
    @Override
    public void run() {
        while (true){
            try {
                e.open();
                e.opened();
                g.remove_dis(e.epoch);
                e.close();
                //移动前判定下一步该不该走，该走哪
                e.next_stop_floor = e.current_floor;
                while(true){
                    //按了本楼层停着的电梯
                    if(e.stop_floor[e.current_floor]==1){
                        e.open();
                        e.opened();
                        g.remove_dis(e.epoch);
                        e.close();
                    }
                    //看看上面有没有
                    int up_floor=100;
                    for(int i=e.current_floor+1;i<=e.service_max_floor;i++){
                        if(e.stop_floor[i]>0){
                            up_floor = i - e.current_floor;
                            break;
                        }
                    }
                    //看看下面有没有
                    int down_floor=100;
                    for(int i=e.current_floor-1;i>=e.service_min_floor;i--){
                        if(e.stop_floor[i]>0){
                            down_floor = e.current_floor - i;
                            break;
                        }
                    }
                    //比一比更近的
                    //没有请求了
                    if(up_floor==100&&down_floor==100) {

                    }
                    //向上走
                    else if(up_floor<=down_floor) {
                        e.next_stop_floor = e.current_floor + up_floor;
                        e.dire = 0;//同时还要改变方向
                    }
                    //向下走
                    else if(up_floor>down_floor){
                        e.next_stop_floor = e.current_floor - down_floor;
                        e.dire = 1;//同时还要改变方向
                    }
                    //出发
                    if(e.next_stop_floor!=e.current_floor) break;
                }
                do {
                    e.running();
                    //到达目标楼层，停电梯
                }while(e.service_floor[e.current_floor] != 1 || e.current_floor != e.next_stop_floor);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
