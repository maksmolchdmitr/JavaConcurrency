import java.util.stream.IntStream;

import static java.lang.System.*;
import static java.util.stream.IntStream.*;

public class Lesson7 {
    public static void main(String[] args) throws InterruptedException {
        final TaskSummingNums first = new TaskSummingNums(0, 100);
        final TaskSummingNums second = new TaskSummingNums(101, 200);
        Thread firstThread = createAnsStartThread(first);
        Thread secondThread = createAnsStartThread(second);
        firstThread.join();
        secondThread.join();
        final int result = first.getRes() + second.getRes();
        out.println(result);
        // Without join: result = 0
        // With join: 20100 (=5050+15050)
    }

    private static Thread createAnsStartThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    private final static class TaskSummingNums implements Runnable {
        private final int from, to;

        public int getRes() {
            return res;
        }

        private int res = 0;

        private TaskSummingNums(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            rangeClosed(this.from, this.to).forEach(i -> this.res += i);
            out.println(this.res);
        }
    }
}
