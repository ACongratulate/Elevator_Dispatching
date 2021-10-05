public class Passenger{
    int num;//乘客序号
    int weight;//体重
    int current_floor;//当前楼层
    int target_floor;//目标楼层
    int dire;//乘客的目的地方向
    int state;//0等待，1到达
    int ele;//分配的电梯

    public Passenger(int n, int w, int cf, int tf){
        this.num = n;
        this.weight = w;
        this.current_floor = cf;
        this.target_floor = tf;
        if(cf < tf) this.dire = 0;
        else this.dire = 1;
        this.state = 0;
    }
}