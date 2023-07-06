package ReadWriteLock;

import java.util.OptionalLong;
import java.util.concurrent.locks.Lock;

import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class AbstractCounter {
    private long value;

    public final OptionalLong getValue() {
        final Lock lock = this.getReadLock();
        lock.lock();
        try {
            SECONDS.sleep(1);
            return OptionalLong.of(this.value);
        } catch (InterruptedException e) {
            currentThread().interrupt();
            return OptionalLong.empty();
        } finally {
            lock.unlock();
        }
    }

    public final void increment() {
        final Lock lock = this.getWriteLock();
        lock.lock();
        try {
            this.value++;
        } finally {
            lock.unlock();
        }
    }

    protected abstract Lock getReadLock();

    protected abstract Lock getWriteLock();
}
