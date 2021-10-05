public class LOOK implements Runnable{
    Elevator e;
    Floor f;
    GUI g;

    public LOOK(Elevator eo,Floor f, GUI go){
        this.e = eo;
        this.g = go;
        this.f = f;
        this.e.scheduling_algorithm = 2;
    }

    @Override
    public void run() {
        while (true){
            try {
                e.open();
                e.opened();
                g.remove_dis(e.epoch);
                e.close();

                while (true){
                    //电梯里没人了，就看要不要改变上下限
                    if(e.current_psg == 0){
                        //上
                        e.LOOK_max_floor = e.current_floor;
                        for(int i = e.current_floor+1;i<=e.service_max_floor;i++){
                            //上面有请求
                            if(e.service_floor[i]==1){
                                if(f.floor[i][0]>0 || f.floor[i][1]>0){
                                    e.LOOK_max_floor = i;
                                    break;
                                }
                            }
                        }
                        //下
                        e.LOOK_min_floor = e.current_floor;
                        for(int i = e.current_floor-1;i>=e.service_min_floor;i--){
                            //下面有请求
                            if(e.service_floor[i]==1){
                                if(f.floor[i][0]>0||f.floor[i][1]>0){
                                    e.LOOK_min_floor = i;
                                    break;
                                }
                            }
                        }
                        //本层有反向请求且不是顶层
                        if(e.current_floor!=e.service_max_floor && e.current_floor!=e.service_min_floor && f.floor[e.current_floor][1-e.dire]>0){
                            e.dire = 1 - e.dire;
                            e.open();
                            e.opened();
                            g.remove_dis(e.epoch);
                            e.close();
                            break;
                        }

                        if(e.LOOK_min_floor == e.current_floor && e.LOOK_max_floor == e.current_floor){
                            //System.out.println(e.epoch + "号电梯不动");
                        }
                        else if(e.dire == 0 && e.LOOK_max_floor == e.current_floor){
                            e.dire = 1;
                            //System.out.println(e.epoch + "号电梯在" + e.current_floor + "层掉头");
                            break;
                        }
                        else if(e.dire == 1 && e.LOOK_min_floor == e.current_floor){
                            e.dire = 0;
                            //System.out.println(e.epoch + "号电梯在" + e.current_floor + "层掉头");
                            break;
                        }
                        else break;
                    }
                    else break;
                }
                do {
                    e.running();
                }while(e.service_floor[e.current_floor] != 1);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
