import static java.lang.System.out;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Lesson28 {
    public static void main(String[] args) throws InterruptedException {
        final PrintingTask printingTask = new PrintingTask();
        final Thread printingThread = new Thread(printingTask);
        printingThread.start();

        SECONDS.sleep(5);

        printingTask.setShouldPrint(false);
        out.println("Printing should be stopped!");
    }

    private static final class PrintingTask implements Runnable {
        private volatile boolean shouldPrint = true;

        public void setShouldPrint(boolean shouldPrint) {
            this.shouldPrint = shouldPrint;
        }

        @Override
        public void run() {
            try {
                while (this.shouldPrint) {
                    out.println("I am working!");
                    SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                currentThread().interrupt();
            }
        }

    }
}
