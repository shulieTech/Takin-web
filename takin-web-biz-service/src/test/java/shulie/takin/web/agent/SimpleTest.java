package shulie.takin.web.agent;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/11/19 5:20 下午
 */
public class SimpleTest {

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            System.out.println(111);
            while (true) {
                System.out.println(222);
            }
        });
        t.start();

        System.out.println("interrupt start");
        t.interrupt();
        System.out.println("interrupt end");

        Thread.sleep(30000);
    }
}
