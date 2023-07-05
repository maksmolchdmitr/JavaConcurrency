import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.System.out;
import static java.util.stream.IntStream.range;

public class Lesson22 {
    public static void main(String[] args) {
        EvenNumsGenerator generator = new EvenNumsGenerator();
        Runnable generateNums = () -> range(0, 100)
                .forEach(i -> out.println(generator.generate()));
        final Thread thread1 = new Thread(generateNums);
        thread1.start();
        final Thread thread2 = new Thread(generateNums);
        thread2.start();
        final Thread thread3 = new Thread(generateNums);
        thread3.start();
        // max number is 598
    }

    private static final class EvenNumsGenerator {
        private final Lock lock = new ReentrantLock();
        private int prevNum = -2;

        public int generate() {
            this.lock.lock();
            try {
                return this.prevNum += 2;
            } finally {
                this.lock.unlock();
            }
        }
    }
}
