package pc.stack;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Lock-free stack using a linked list of nodes.
 * 
 * @param <E> Type of elements in the stack.
 */
public class ALinkedStack<E> implements Stack<E> {
  
  private static class State<E> {
    Node<E> top;
    int size;
  }

  private final AtomicReference<State<E>> ref;
  
  private final Backoff backoff;
  
  /**
   * Constructor with no arguments, disabling back-off by default.
   */
  public ALinkedStack() {
    this(false);
  }
  
  /**
   * Constructor with explicit back-off setting.
   * @param enableBackoff Flag indicating if back-off should be used or not.
   */
  public ALinkedStack(boolean enableBackoff) {
    State<E> initial = new State<>();
    initial.top = null;
    initial.size = 0;
    ref = new AtomicReference<>(initial);
    backoff = enableBackoff ? new Backoff() : null;
  }

  @Override
  public int size() {
    return ref.get().size;
  }

  @Override
  public void push(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    Node<E> newTop = new Node<>(elem, null);
    newTop.data = elem;
    State<E> oldState;
    State<E> newState = new State<>();
    while (true) {
      oldState = ref.get();
      newTop.next = oldState.top; 
      newState.top = newTop;
      newState.size = oldState.size + 1; 
      if (ref.compareAndSet(oldState, newState)) {
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
    State<E> oldState;
    State<E> newState = new State<>();
    E elem = null;
    while(true) {
      oldState = ref.get();
      if (oldState.size == 0) {
        elem = null;
        break;
      }
      newState.top = oldState.top.next;
      newState.size = oldState.size - 1;
      elem = oldState.top.data;
      if (ref.compareAndSet(oldState, newState)) {
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
      return new ALinkedStack<>();
    }
  }
} 
