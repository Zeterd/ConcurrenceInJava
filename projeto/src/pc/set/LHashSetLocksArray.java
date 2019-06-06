package pc.set;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Hash set implementation.
 *
 */
public class LHashSetLocksArray<E> implements Set<E>{

  private static final int NUMBER_OF_BUCKETS = 16; // should not be changed

  private LinkedList<E>[] table;
  private final ReentrantLock[] lock;
  private final AtomicInteger size;

  /**
   * Constructor.
   * @param fair Fairness flag.
   */
  @SuppressWarnings("unchecked")
  public LHashSetLocksArray(boolean fair) {
    table = (LinkedList<E>[]) new LinkedList[NUMBER_OF_BUCKETS];
    lock = new ReentrantLock[NUMBER_OF_BUCKETS];
    for(int i=0; i<NUMBER_OF_BUCKETS; i++){
        lock[i] = new ReentrantLock(fair);
    }
    size = new AtomicInteger();
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
    int index = Math.abs(elem.hashCode()%table.length);
    //System.out.println("elemHas: " + elem.hashCode() + " | table.length: " + table.length + " | index: "+index);
    boolean r = false;

    lock[index].lock();
    try{
      LinkedList<E> list = getEntry(elem);
      r = ! list.contains(elem);
      if (r) {
        list.addFirst(elem);
        size.incrementAndGet();
      }
    }
    finally{lock[index].unlock();}

    return r;
  }

  @Override
  public boolean remove(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }

    int index = Math.abs(elem.hashCode() % table.length);

    if(index < 0 || index >= NUMBER_OF_BUCKETS)
      return false;

    boolean r = false;

    lock[index].lock();
    try{
      LinkedList<E> list = getEntry(elem);
      r = list.remove(elem);

      if (r) {
        size.decrementAndGet();
      }
    }finally{
      lock[index].unlock();
    }
    return r;
  }

  @Override
  public boolean contains(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    int index = Math.abs(elem.hashCode()%table.length);
    boolean r = false;

    if(index < 0 || index >= NUMBER_OF_BUCKETS)
      return false;

    lock[index].lock();
    try{
      LinkedList<E> list = getEntry(elem);
      r = list.contains(elem);
  }finally{lock[index].unlock();}

    return r;

  }
}
