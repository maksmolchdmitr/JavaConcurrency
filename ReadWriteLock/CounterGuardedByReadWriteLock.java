package ReadWriteLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CounterGuardedByReadWriteLock extends AbstractCounter {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = this.readWriteLock.readLock();
    private final Lock writeLock = this.readWriteLock.writeLock();

    @Override
    protected Lock getReadLock() {
        return this.readLock;
    }

    @Override
    protected Lock getWriteLock() {
        return this.writeLock;
    }
}
