import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Queue<E> {
  private final E[] elems;
  private int size;
  private int head;
  private final ReentrantLock qlock;
  private final Condition notEmpty, notFull;
  
  @SuppressWarnings("unchecked")
  Queue(int capacity) {
    if (capacity <= 0) 
      throw new IllegalArgumentException("Invalid capacity: " + capacity);
    
    elems = (E[]) new Object[capacity];
    size = 0;
    head = 0;
    qlock = new ReentrantLock();
    notEmpty = qlock.newCondition();
    notFull = qlock.newCondition();
  }
  
  void add(E elem) throws InterruptedException {
    qlock.lock();
    try {
      while (size == elems.length) { notFull.await(); }
      elems[(head + size) % elems.length] = elem;
      System.out.printf("W %d: %s%n", 
                         (head + size) % elems.length, elem); 
      size++;
      notEmpty.signal();
    }
    finally {
      qlock.unlock();
    }
  }

  E take() throws InterruptedException {
    qlock.lock();
    try {
      while (size == 0) { notEmpty.await(); }
      E elem = elems[head];
      System.out.printf("R %d: %s%n", head, elem); 
      head = (head + 1) % elems.length;
      size--;
      notFull.signal();
      return elem;
    }
    finally {
      qlock.unlock();
    }
  }

  int size() {
    return size;
  }

  static void producer(Queue<Integer> q, int i, Semaphore s) {
    try {
      s.down();
      q.add(i);
      System.out.printf("Producer %d > %d%n", i, i);
    }
    catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  static void consumer(Queue<Integer> q, int i, Semaphore s) {
    try {
      s.down();
      int v = q.take();
      System.out.printf("Consumer %d < %d%n", i, v);
    }
    catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws InterruptedException {
    int n = 100;
    int capacity = 3;
    Thread[] p = new Thread[n];
    Thread[] c = new Thread[n];
    Queue<Integer> q = new Queue<>(capacity);
    Semaphore s = new Semaphore(0);
    for (int i = 0; i < n; i++) {
      int _i = i;
      p[i] = new Thread(() -> producer(q, _i, s));
      c[i] = new Thread(() -> consumer(q, _i, s));
      p[i].start(); 
      c[i].start();
    }
    for (int i = 0; i < n; i++) {
      s.up(); 
      s.up();
    }
    for (int i = 0; i < n; i++) {
      p[i].join(); c[i].join();
    }
    System.out.println("Queue size at the end: " + q.size());
  }
}
