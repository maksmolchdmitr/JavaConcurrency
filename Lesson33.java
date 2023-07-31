import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

public class Lesson33 {
    public static void main(String[] args) throws InterruptedException {
        EvenNumberGenerator generator = new EvenNumberGenerator();
        final int count = 10000;
        final Runnable task = () -> range(0, count)
                .forEach(i -> generator.generate());
        final int threadCount = 5;
        final Thread[] threads = create(task, threadCount);
        start(threads);
        wait(threads);
        final int expectedValue = count * threadCount * 2;
        final int actualValue = generator.getValue();
        if (expectedValue != actualValue) {
            throw new RuntimeException("Expected is %s but was %s\n".formatted(expectedValue, actualValue));
        }
    }

    private static Thread[] create(final Runnable task, int count) {
        return range(0, count)
                .mapToObj(i -> new Thread(task))
                .toArray(Thread[]::new);
    }

    private static void start(Thread[] threads) {
        stream(threads).forEach(Thread::start);
    }

    private static void wait(Thread[] threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }
}

final class EvenNumberGenerator {
    private final AtomicInteger value = new AtomicInteger();

    public int generate() {
        return this.value.getAndAdd(2);
    }

    public int getValue() {
        return this.value.intValue();
    }
}
