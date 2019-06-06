package pc.stack;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Lock-free, array-based stack with optional exponential back-off
 * scheme.
 * 
 * @param <E> Type of elements in the stack.
 */
public class AArrayStack<E> implements Stack<E> {

  private final int INITIAL_CAPACITY = 16;
  private E[] array;
  private final Backoff backoff;
  private AtomicStampedReference<Boolean> ref;

  /**
   * Constructor with no arguments, disabling back-off by default.
   */
  public AArrayStack() {
    this(false);
  }

  /**
   * Constructor with explicit back-off setting.
   * @param enableBackoff Flag indicating if back-off should be used or not.
   */
  @SuppressWarnings("unchecked")
  public AArrayStack(boolean enableBackoff) {
    array = (E[]) new Object[INITIAL_CAPACITY];
    backoff = enableBackoff ? new Backoff() : null;
    ref = new AtomicStampedReference<>(true, 0);

  }

  @Override
  public int size() {
    return ref.getStamp();
  }

  @Override
  public void push(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    while (true) {
      int n = ref.getStamp();
      if (ref.compareAndSet(true,false,n, n+1)) {
        if (n == array.length) {
          array = Arrays.copyOf(array, 2 * array.length);
        }

        array[n] = elem;

        ref.set(true, n + 1);
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
      int n = ref.getStamp();
      if (n == 0) {
        elem = null;
        break;
      }
      if (ref.compareAndSet(true, false, n, n - 1)) {
        elem = array[n - 1];
        array[n - 1] = null;

        ref.set(true, n - 1);

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

  // For tests
  @SuppressWarnings("javadoc")
  public static class Test extends StackTest {
    @Override
    public Stack<Integer> createStack() {
      return new AArrayStack<>();
    }
  }
} 
