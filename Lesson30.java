import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.System.out;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Lesson30 {
    public static void main(String[] args) {
        final Lock firstLock = new ReentrantLock(), secondLock = new ReentrantLock();
        final LockWithName firstLockWithName = new LockWithName("First lock", firstLock), secondLockWithName = new LockWithName("Second lock", secondLock);

        final Thread firstThread = new Thread(new Task(firstLockWithName, secondLockWithName));
//        final Thread secondThread = new Thread(new Task(secondLockWithName, firstLockWithName));
        //Thread 'Thread-0' is trying to acquire lock 'First lock'!
        //Thread 'Thread-0' acquired lock 'First lock'
        //Thread 'Thread-1' is trying to acquire lock 'Second lock'!
        //Thread 'Thread-1' acquired lock 'Second lock'
        //Thread 'Thread-0' is trying to acquire lock 'Second lock'!
        //Thread 'Thread-1' is trying to acquire lock 'First lock'!
        //DEADLOCK!
        final Thread secondThread = new Thread(new Task(firstLockWithName, secondLockWithName));
        //Thread 'Thread-0' is trying to acquire lock 'First lock'!
        //Thread 'Thread-0' acquired lock 'First lock'
        //Thread 'Thread-1' is trying to acquire lock 'First lock'!
        //Thread 'Thread-0' is trying to acquire lock 'Second lock'!
        //Thread 'Thread-0' acquired lock 'Second lock'
        //Thread 'Thread-0' released lock 'Second lock'
        //Thread 'Thread-0' released lock 'First lock'
        //Thread 'Thread-1' acquired lock 'First lock'
        //Thread 'Thread-1' is trying to acquire lock 'Second lock'!
        //Thread 'Thread-1' acquired lock 'Second lock'
        //Thread 'Thread-1' released lock 'Second lock'
        //Thread 'Thread-1' released lock 'First lock'
        //
        //Process finished with exit code 0! SUCCESS!

        firstThread.start();
        secondThread.start();
    }

    public static class LockWithName implements Lock {
        private final String name;
        private final Lock lock;

        public String getName() {
            return name;
        }

        public LockWithName(String name, Lock lock) {
            this.name = name;
            this.lock = lock;
        }

        @Override
        public void lock() {
            lock.lock();
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            lock.lockInterruptibly();
        }

        @Override
        public boolean tryLock() {
            return lock.tryLock();
        }

        @Override
        public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
            return lock.tryLock(l, timeUnit);
        }

        @Override
        public void unlock() {
            lock.unlock();
        }

        @Override
        public Condition newCondition() {
            return lock.newCondition();
        }
    }

    public static final class Task implements Runnable {
        private final LockWithName firstLock, secondLock;

        public Task(LockWithName firstLock, LockWithName secondLock) {
            this.firstLock = firstLock;
            this.secondLock = secondLock;
        }

        @Override
        public void run() {
            final String currentThreadName = currentThread().getName();
            out.printf("Thread '%s' is trying to acquire lock '%s'!\n", currentThreadName, this.firstLock.getName());
            this.firstLock.lock();
            try {
                out.printf("Thread '%s' acquired lock '%s'\n", currentThreadName, this.firstLock.getName());
                MILLISECONDS.sleep(200);
                out.printf("Thread '%s' is trying to acquire lock '%s'!\n", currentThreadName, this.secondLock.getName());
                this.secondLock.lock();
                try {
                    out.printf("Thread '%s' acquired lock '%s'\n", currentThreadName, this.secondLock.getName());
                } finally {
                    this.secondLock.unlock();
                    out.printf("Thread '%s' released lock '%s'\n", currentThreadName, this.secondLock.getName());
                }
            } catch (InterruptedException e) {
                currentThread().interrupt();
            } finally {
                this.firstLock.unlock();
                out.printf("Thread '%s' released lock '%s'\n", currentThreadName, this.firstLock.getName());
            }
        }
    }
}
