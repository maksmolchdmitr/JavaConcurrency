import static java.lang.System.*;
import static java.lang.Thread.*;

public class Lesson6 {
    public static void main(String[] args) {
        out.println(currentThread().getName());
        // main

        final Thread thread = new MyThread();
        thread.start();
        // Thread-0

        final Thread anonymousThread = new Thread(){
            @Override
            public void run() {
                out.println(currentThread().getName());
            }
        };
        anonymousThread.start();
        // Thread-1

        final Runnable task = () -> out.println(currentThread().getName());
        final Thread threadWithRunnable = new Thread(task);
        threadWithRunnable.start();
        // Thread-2
    }

    private static final class MyThread extends Thread{
        @Override
        public void run() {
            out.println(currentThread().getName());
        }
    }
}
