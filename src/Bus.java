public class Bus implements Runnable{
    Elevator e;
    GUI g;

    public Bus(Elevator eo, GUI go){
        this.e = eo;
        this.e.scheduling_algorithm = 1;
        this.g = go;
    }

    @Override
    public void run() {
        while (true){
            try {
                e.open();
                e.opened();
                g.remove_dis(e.epoch);
                e.close();
                do {
                    e.running();
                }while(e.service_floor[e.current_floor] != 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
