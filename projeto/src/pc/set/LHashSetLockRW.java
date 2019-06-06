package pc.set;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Hash set implementation.
 *
 */
public class LHashSetLockRW<E> implements Set<E>{

  private static final int NUMBER_OF_BUCKETS = 16; // should not be changed

  private LinkedList<E>[] table;
  private final AtomicInteger size;
  private final ReentrantReadWriteLock[] locks;

  /**
   * Constructor.
   * @param fair Fairness flag.
   */
  @SuppressWarnings("unchecked")
  public LHashSetLockRW(boolean fair) {
    table = (LinkedList<E>[]) new LinkedList[NUMBER_OF_BUCKETS];
    size = new AtomicInteger();
    locks = new ReentrantReadWriteLock[NUMBER_OF_BUCKETS];
    for (int i = 0; i < NUMBER_OF_BUCKETS; i++) {
        locks[i] = new ReentrantReadWriteLock(fair);
    }
  }

  @Override
  public int size() {
    return size.get();
  }

  private LinkedList<E> getEntry(E elem) {
    int pos = Math.abs(elem.hashCode() % table.length);
    LinkedList<E> list = table[pos];

    if (list == null) {
      table[pos] = list = new LinkedList<>();
    }

    return list;
  }

  @Override
  public boolean add(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }

    int pos = Math.abs(elem.hashCode() % table.length);
    LinkedList<E> list;
    boolean r = false;
    locks[pos].writeLock().lock();  // block until condition holds
    try {
      list = getEntry(elem);
      r = ! list.contains(elem);

      if (r) {
        list.addFirst(elem);
        size.incrementAndGet();
      }
    } finally {
      locks[pos].writeLock().unlock();
    }

    return r;
  }

  @Override
  public boolean remove(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }

    int pos = Math.abs(elem.hashCode() % table.length);
    boolean r = false;
    locks[pos].writeLock().lock();  // block until condition holds
    try {
      r = getEntry(elem).remove(elem);

      if (r) {
        size.decrementAndGet();
      }
    } finally {
      locks[pos].writeLock().unlock();
    }

    return r;
  }

  @Override
  public boolean contains(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }

    boolean r = false;
    int pos = Math.abs(elem.hashCode() % table.length);
    locks[pos].readLock().lock();  // block until condition holds
    try {
      r = getEntry(elem).contains(elem);
    } finally {
      locks[pos].readLock().unlock();
    }
    return r;
  }
}
