package pc.stack;

import java.util.Arrays;

/**
 * Blocking implementation of stack using an array
 * to store elements.
 * 
 * @param <E> Type of elements in the stack.
 */
public class LArrayStack<E> implements Stack<E> {
  
  private final int INITIAL_CAPACITY = 16;
  private E[] array;
  private int top;
  
  /**
   * Constructor.
   */
  @SuppressWarnings("unchecked")
  public LArrayStack() {
    array = (E[]) new Object[INITIAL_CAPACITY];
    top = 0;
  }

  @Override
  public int size() {
    synchronized (this) {
      return top;
    }
  }

  @Override
  public void push(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    synchronized (this) {
      if (top == array.length) {
        array = Arrays.copyOf(array, 2 * array.length);
      }
      array[top] = elem;
      top++;
    }
  }

  @Override
  public E pop() {
    E elem = null;
    synchronized (this) {
      if (top > 0) {
        top --;
        elem = array[top];
      }
    }
    return elem;
  }
  
  //For tests
  @SuppressWarnings("javadoc")
  public static class Test extends StackTest {
    @Override
    public Stack<Integer> createStack() {
      return new LArrayStack<>();
    }
  }
} 
