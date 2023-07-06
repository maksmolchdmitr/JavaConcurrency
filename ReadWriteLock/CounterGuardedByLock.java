package ReadWriteLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CounterGuardedByLock extends AbstractCounter{
    private final Lock lock = new ReentrantLock();
    @Override
    protected Lock getReadLock() {
        return this.lock;
    }

    @Override
    protected Lock getWriteLock() {
        return this.lock;
    }
}
