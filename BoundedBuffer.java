import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.lang.Thread.*;

public final class BoundedBuffer<T> {
    private final T[] elements;
    private int size;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = this.lock.newCondition();
    private final Condition notEmpty = this.lock.newCondition();

    @SuppressWarnings("unchecked")
    public BoundedBuffer(int capacity) {
        elements = (T[]) new Object[capacity];
    }

    public boolean isFull() {
        this.lock.lock();
        try {
            return this.size == this.elements.length;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean isEmpty() {
        this.lock.lock();
        try {
            return this.size == 0;
        } finally {
            this.lock.unlock();
        }
    }

    public void put(T el) {
        this.lock.lock();
        try {
            while (this.isFull()) {
                this.notFull.await();
            }
            this.elements[this.size++] = el;
            this.notEmpty.signal();
        } catch (InterruptedException e) {
            currentThread().interrupt();
        } finally {
            this.lock.unlock();
        }
    }

    public T take() {
        this.lock.lock();
        try {
            while (this.isEmpty()) {
                this.notEmpty.await();
            }
            this.notFull.signal();
            return this.elements[--this.size];
        } catch (InterruptedException e) {
            currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public String toString() {
        this.lock.lock();
        try {
            return Arrays.stream(this.elements, 0, this.size)
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));
        } finally {
            this.lock.unlock();
        }
    }
}
