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
  private boolean[] loques;
  private int size;
  private final ReentrantLock rl;

  private void initLoques(){
      for(int i=0; i<NUMBER_OF_BUCKETS; i++){
          loques[i] = false;
      }
  }

  /**
   * Constructor.
   * @param fair Fairness flag.
   */
  @SuppressWarnings("unchecked")
  public LHashSet(boolean fair) {
    table = (LinkedList<E>[]) new LinkedList[NUMBER_OF_BUCKETS];
    loques = new boolean[NUMBER_OF_BUCKETS];
    initLoques();
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

    //3.1-rl.lock
    LinkedList<E> list = getEntry(elem);
    boolean r = ! list.contains(elem);
    int index = list.indexOf(elem);

    if(!loques[index])
      loques[index] = true;
      //ainda nao acabei por isso pode dar erros se tentatres :^)
      rl.lock();


    if (r) {
      list.addFirst(elem);
      size++;
    }
    //3.1-rl.unlock();
    return r;
  }

  @Override
  public boolean remove(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    rl.lock();
    boolean r = getEntry(elem).remove(elem);

    if (r) {
      size--;
    }
    rl.unlock();
    return r;
  }

  @Override
  public boolean contains(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    rl.lock();
    LinkedList<E> list = getEntry(elem);
    boolean r = list.contains(elem);
    rl.unlock();

    return r;

  }
}
