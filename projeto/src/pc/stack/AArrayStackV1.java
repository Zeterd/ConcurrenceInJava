package pc.stack;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Array-based stack - buggy implementation 1.
 * 
 * @param <E> Type of elements in the stack.
 */
public class AArrayStackV1<E> implements Stack<E> {

  private final int INITIAL_CAPACITY = 16;
  private final E[] array;
  private final AtomicInteger top;

  /**
   * Constructor.
   */
  @SuppressWarnings("unchecked")
  public AArrayStackV1() {
    array = (E[]) new Object[INITIAL_CAPACITY];
    top = new AtomicInteger(0);
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
    int pos = top.getAndIncrement();
    array[pos] = elem;
  }

  @Override
  public E pop() {
    if (top.get() == 0) {
      return null;
    }
    int pos = top.decrementAndGet();
    E elem = array[pos];
    array[pos] = null;
    return elem;
  }

  //For tests
  @SuppressWarnings("javadoc")
  public static class Test extends StackTest {
    @Override
    public Stack<Integer> createStack() {
      return new AArrayStackV1<>();
    }
  }
  
} 
