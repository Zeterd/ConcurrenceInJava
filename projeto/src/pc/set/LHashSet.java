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
  private ReentrantLock[] lock;

  /**
   * Constructor.
   * @param fair Fairness flag.
   */
  @SuppressWarnings("unchecked")
  public LHashSet(boolean fair) {
    table = (LinkedList<E>[]) new LinkedList[NUMBER_OF_BUCKETS];
    lock = new ReentrantLock[NUMBER_OF_BUCKETS];
    for(int i=0; i<NUMBER_OF_BUCKETS; i++){
        lock[i] = new ReentrantLock(fair);
    }
    size = 0;
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

    LinkedList<E> list = getEntry(elem);
    boolean r = ! list.contains(elem);
    int index = 0;

    lock[index].lock();
    
    if (r) {
      list.addFirst(elem);
      size++;
    }

    lock[index].unlock();

    return r;
  }

  @Override
  public boolean remove(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    LinkedList<E> list = getEntry(elem);
    int index = list.indexOf(elem);

    if(index < 0 || index >= NUMBER_OF_BUCKETS)
      return false;

    lock[index].lock();
    boolean r = list.remove(elem);

    if (r) {
      size--;
    }
    lock[index].unlock();
    return r;
  }

  @Override
  public boolean contains(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    LinkedList<E> list = getEntry(elem);
    int index = list.indexOf(elem);

    if(index < 0 || index >= NUMBER_OF_BUCKETS)
      return false;

    //System.out.println(index);
    lock[index].lock();
    boolean r = list.contains(elem);
    lock[index].unlock();

    return r;

  }
}
