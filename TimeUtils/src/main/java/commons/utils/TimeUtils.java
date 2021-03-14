package commons.utils;

public class TimeUtils {
    private TimeUtils(){

    }
    public static long startTime=-1;
    public static long endTime=-1;
    public static TimeUtils timeUtils=null;
    public void computeTime(){
        if (startTime == -1) {
            startTime=System.currentTimeMillis();
        }else if (startTime != -1 && endTime == -1){
            endTime = System.currentTimeMillis();
            double lastTime=endTime-startTime;
            System.out.println("过程累积消耗时间是" + (double)(lastTime/1000) +"秒");
            startTime = -1;
            endTime = -1;
        }
    }

    public static TimeUtils getUtils() {
        if (timeUtils == null){
            return new TimeUtils();
        }else {

            return timeUtils;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        timeUtils=getUtils();
        timeUtils.computeTime();
        Thread.sleep(1648);
        timeUtils.computeTime();
    }
}
