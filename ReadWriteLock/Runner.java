package ReadWriteLock;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.System.out;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.stream;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.IntStream.range;

public class Runner {
    public static void main(String[] args) throws InterruptedException {
        testCounter(CounterGuardedByLock::new);
        // CounterGuardedByLock::new          - 211120493 - 55
        // CounterGuardedByReadWriteLock::new - 38698074  - 201
    }

    private static void testCounter(final Supplier<? extends AbstractCounter> counterFactory) throws InterruptedException {
        final AbstractCounter counter = counterFactory.get();

        final int amountOfThreadsGettingValue = 50;
        final ReadingValueTask[] readingValueTasks = createReadingTasks(counter, amountOfThreadsGettingValue);
        final Thread[] readingThreads = mapToThreads(readingValueTasks);

        final Runnable incrementingCounterTask = createIncrementingCounterTask(counter);
        final int amountOfThreadsIncrementingCounter = 2;
        final Thread[] incrementingThreads = createThreads(incrementingCounterTask, amountOfThreadsIncrementingCounter);

        startThreads(readingThreads);
        startThreads(incrementingThreads);
        SECONDS.sleep(5);
        interruptThreads(readingThreads);
        interruptThreads(incrementingThreads);

        waitUntilFinish(readingThreads);

        final long totalCountOfReads = fingTotalAmountOfReads(readingValueTasks);
        out.printf("Amount of reading values: %d\n", totalCountOfReads);
    }

    private static long fingTotalAmountOfReads(final ReadingValueTask[] tasks) {
        return stream(tasks)
                .mapToLong(ReadingValueTask::getAmountOfReads)
                .sum();
    }

    private static void waitUntilFinish(Thread[] threads) {
        forEach(threads, Runner::waitUntilFinish);
    }

    private static void waitUntilFinish(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            currentThread().interrupt();
        }
    }

    private static void interruptThreads(final Thread[] threads) {
        forEach(threads, Thread::interrupt);
    }

    private static void startThreads(final Thread[] threads) {
        forEach(threads, Thread::start);
    }

    private static void forEach(final Thread[] threads, final Consumer<Thread> action) {
        stream(threads)
                .forEach(action);
    }

    private static Runnable createIncrementingCounterTask(final AbstractCounter counter) {
        return () -> {
            while (!currentThread().isInterrupted()) {
                incrementCounter(counter);
            }
        };
    }

    private static Thread[] createThreads(final Runnable task, final int amountOfThreads) {
        return range(0, amountOfThreads)
                .mapToObj(i -> new Thread(task))
                .toArray(Thread[]::new);
    }

    private static void incrementCounter(final AbstractCounter counter) {
        try {
            counter.increment();
            SECONDS.sleep(1);
        } catch (InterruptedException e) {
            currentThread().interrupt();
        }
    }

    private static ReadingValueTask[] createReadingTasks(AbstractCounter counter, int amountOfTasks) {
        return range(0, amountOfTasks)
                .mapToObj(i -> new ReadingValueTask(counter))
                .toArray(ReadingValueTask[]::new);
    }

    private static Thread[] mapToThreads(final Runnable[] tasks) {
        return stream(tasks)
                .map(Thread::new).
                toArray(Thread[]::new);
    }

    private final static class ReadingValueTask implements Runnable {
        private final AbstractCounter counter;

        public long getAmountOfReads() {
            return amountOfReads;
        }

        private long amountOfReads;

        private ReadingValueTask(AbstractCounter counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            while (!currentThread().isInterrupted()) {
                this.counter.getValue();
                this.amountOfReads++;
            }
        }
    }
}
