package pc.stack;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Array-based stack - buggy implementation 1.
 * 
 * @param <E> Type of elements in the stack.
 */
public class AArrayStackV2<E> implements Stack<E> {

  private final int INITIAL_CAPACITY = 16;
  private final E[] array;
  private final AtomicInteger top;
  private final Backoff backoff;

  /**
   * Constructor with no arguments, disabling back-off by default.
   */
  public AArrayStackV2() {
    this(false);
  }

  /**
   * Constructor with explicit back-off setting.
   * @param enableBackoff Flag indicating if back-off should be used or not.
   */
  @SuppressWarnings("unchecked")
  public AArrayStackV2(boolean enableBackoff) {
    array = (E[]) new Object[INITIAL_CAPACITY];
    top = new AtomicInteger(0);
    backoff = enableBackoff ? new Backoff() : null;
  }

  @Override
  public int size() {
    return top.get();
  }

  @Override
  public void push(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    while (true) {
      int n = top.get();
      if (top.compareAndSet(n, n+1)) {
        array[n] = elem;
        if (backoff != null) {
          backoff.diminish();
        }
        break; 
      }
      if (backoff != null) {
        backoff.delay(); 
      }
    }
  }

  @Override
  public E pop() {
    E elem = null;
    while (true) {
      int n = top.get();
      if (n == 0) {
        elem = null;
        break;
      }
      if (top.compareAndSet(n, n - 1)) {
        elem = array[n - 1];
        array[n - 1] = null;
        if (backoff != null) {
          backoff.diminish();
        }
        break;
      }
      if (backoff != null) {
        backoff.delay();
      }
    }
    return elem;
  }

  //For tests
  @SuppressWarnings("javadoc")
  public static class Test extends StackTest {
    @Override
    public Stack<Integer> createStack() {
      return new AArrayStackV2<>();
    }
  }
} 
