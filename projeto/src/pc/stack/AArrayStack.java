package pc.stack;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicMarkableReference;

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
    // TODO ... what to use ?
  }

  @Override
  public int size() {
    return 0; // TODO
  }

  @Override
  public void push(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }

    // TODO ...
  }

  @Override
  public E pop() {
    E elem = null;
    // TODO ...

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
