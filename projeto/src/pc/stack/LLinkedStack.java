package pc.stack;

/**
 * Blocking stack implementation based on the use of 
 * a linked list of nodes.
 *
 * @param <E> Type of elements in the stack.
 */
public class LLinkedStack<E> implements Stack<E> {

  private Node<E> top; 
  private int size; 

  /**
   * Constructor.
   */
  public LLinkedStack() {
    top = null;
    size = 0;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public void push(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    synchronized(this) {
      top = new Node<>(elem, top);
      size++;
    }
  }

  @Override
  public E pop() {
    synchronized (this) {
      if (size == 0) {
        return null;
      }
      E elem = top.data;
      top = top.next;
      size--;
      return elem;
    }
  }

  //For tests
  @SuppressWarnings("javadoc")
  public static class Test extends StackTest {
    @Override
    public Stack<Integer> createStack() {
      return new LLinkedStack<>();
    }
  } 
}
