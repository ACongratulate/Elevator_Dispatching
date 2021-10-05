import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Elevator {
    int epoch;//电梯号
    int run_time;//电梯速度，爬升或下降一层需3s
    int open_close_time;//电梯开关门时间
    int opened_time;//电梯门打开时间
    int[] service_floor = new int[25];//服务楼层数组
    int service_max_floor;//最大服务楼层
    int service_min_floor;//最小服务楼层
    int LOOK_max_floor;//LOOK最大楼层
    int LOOK_min_floor;//LOOK最小楼层
    int max_psg;//乘客人数限制
    int max_load;//最大载重量
    int current_lood;//当前载重量
    int current_psg;//当前人数
    int current_floor;//当前楼层
    int state;//电梯状态,0\1\2\3分别表示开门、已开门、关门、移动
    int dire;//电梯运行方向，0为向上，1为向下
    int[] stop_floor = new int[25];//在SSTF算法中需要停的楼层
    int next_stop_floor;
    int scheduling_algorithm;//电梯使用的算法，用于让乘客选择上电梯方式
    Lock lock = new ReentrantLock();

    public Elevator(int epoch)//构造函数
    {
        this.epoch = epoch;
        this.run_time = 3000;
        this.open_close_time = 2000;
        this.opened_time = 4000;
        this.state = 2;
        this.dire=0;
        //每个电梯的属性
        switch (epoch)
        {
            case 1:
                for(int i = 0;i<21;i++){
                    this.service_floor[i] = 1;
                }
                this.max_load = 800;
                this.max_psg = 10;
                this.service_max_floor =  20;
                this.service_min_floor =  0;
                this.current_floor = 3;
                break;
            case 2:
                for(int i = 1; i < 11; i++){
                    this.service_floor[i] = 1;
                }
                this.max_load = 800;
                this.max_psg = 10;
                this.service_max_floor =  10;
                this.service_min_floor =  1;
                this.current_floor = 8;
                break;
            case 3:
                for(int i = 0; i < 11; i++){
                    this.service_floor[i] = 1;
                }
                this.max_load = 1600;
                this.max_psg = 20;
                this.service_max_floor =  10;
                this.service_min_floor =  0;
                this.current_floor = 6;
                break;
            case 4:
                this.service_floor[0] = 1;
                this.service_floor[1] = 1;
                for(int i = 11; i < 21; i++){
                    this.service_floor[i] = 1;
                }
                this.max_load = 2000;
                this.max_psg = 20;
                this.service_max_floor =  20;
                this.service_min_floor =  0;
                this.current_floor = 15;
                break;
        }
    }
    int change(int x){
        if(x == 0) return -1;
        else return x;
    }
    //开门动作
    void open() throws InterruptedException {
        //System.out.println(epoch+ "号电梯: Open the door in the " + this.current_floor + "th floor");
        //改变电梯状态信号量的值为正在开门
        this.state = 0;
        Thread.sleep(this.open_close_time);
    }
    //电梯敞开
    void opened() throws InterruptedException {
        //System.out.println(epoch + "号电梯: The door is opened");
        //改变电梯状态信号量的值为已经打开
        this.state = 1;
        Thread.sleep(this.opened_time);
    }
    //电梯关门
    void close() throws InterruptedException {
        //System.out.println(epoch+ "号电梯: Close the door");
        //改变电梯状态信号量的值为正在关门
        this.state = 2;
        Thread.sleep(this.open_close_time);
    }
    //电梯移动
    void running() throws InterruptedException {
        //System.out.println(epoch + "号电梯: The lift is running");
        //改变电梯状态信号量的值为正在移动
        this.state = 3;
        if (this.dire == 0) {
            this.current_floor++;
        } else {
            this.current_floor--;
        }
        //System.out.println("     " + this.epoch +"号电梯到达" + change(this.current_floor) + "层");
        if (this.current_floor == this.service_max_floor) {
            this.dire = 1;
        }
        if (this.current_floor == this.service_min_floor) {
            this.dire = 0;
        }
        Thread.sleep(this.run_time);
    }
}
