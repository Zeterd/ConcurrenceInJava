package pc.set;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * Hash set implementation.
 *
 */
public class LHashSet<E> implements Set<E>{

  private static final int NUMBER_OF_BUCKETS = 16; // should not be changed 

  private LinkedList<E>[] table;
  private int size;
  private final ReentrantLock rl; // TODO this is not being used

  /**
   * Constructor.
   * @param fair Fairness flag.
   */
  @SuppressWarnings("unchecked")
  public LHashSet(boolean fair) {
    table = (LinkedList<E>[]) new LinkedList[NUMBER_OF_BUCKETS];
    size = 0;
    rl = new ReentrantLock(fair);
  }

  @Override
  public int size() {
    return size;
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
    
    // TODO use lock
    LinkedList<E> list = getEntry(elem);
    boolean r = ! list.contains(elem);

    if (r) {
      list.addFirst(elem);
      size++;
    }
    
    return r;
  }

  @Override
  public boolean remove(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    // TODO use lock
    boolean r = getEntry(elem).remove(elem);
    
    if (r) {
      size--;
    }
    
    return r;
  }

  @Override
  public boolean contains(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    // TODO use lock
    return getEntry(elem).contains(elem);
  }
}
