import static java.lang.System.*;
import static java.lang.Thread.*;

public class Lesson8 {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(()->printThreadState(currentThread()));
        printThreadState(thread);
        // Thread-0 : NEW
        thread.start();
        // Thread-0 : RUNNABLE
        thread.join();
        printThreadState(thread);
        // Thread-0 : TERMINATED
    }

    private static void printThreadState(Thread thread){
        out.printf("%s : %s\n", thread.getName(), thread.getState());
    }
}
