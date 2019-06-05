package pc.stack;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Lock-free stack with optional exponential back-off scheme.
 *
 * @param <E> Type of elements in the stack.
 */
public class ALinkedStackASR<E> implements Stack<E> {


  private final AtomicStampedReference<Node<E>> ref;

  private final Backoff backoff;

  /**
   * Constructor with no arguments, disabling back-off by default.
   */
  public ALinkedStackASR() {
    this(false);
  }

  /**
   * Constructor with explicit back-off setting.
   * @param enableBackoff Flag indicating if back-off should be used or not.
   */
  public ALinkedStackASR(boolean enableBackoff) {
    ref = new AtomicStampedReference<>(null, 0);
    backoff = enableBackoff ? new Backoff() : null;
  }

  @Override
  public int size() {
    return ref.getStamp(); // use ASR stamp to store the size!
  }

  @Override
  public void push(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    // TODO
    Node<E> newNode = new Node<>(elem, null);
    Node<E> oldNode;

    while(true){

      int[] stampHolder = new int[1];
      oldNode = ref.get(stampHolder);

      int oldSize = stampHolder[0];
      newNode.next = oldNode;

      if(ref.compareAndSet(oldNode, newNode, oldSize, stampHolder[0] + 1)){
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
    // TODO ...
    Node<E> oldNode;
    Node<E> newNode;

    while(true){
      int[] stampHolder = new int[1];
      oldNode = ref.get(stampHolder);

      if(oldNode == null){
        elem = null;
        break;
      }

      newNode = oldNode.next;
      int newSize = stampHolder[0] - 1;
      elem = oldNode.data;

      if(ref.compareAndSet(oldNode, newNode, stampHolder[0], newSize)){
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
      return new ALinkedStackASR<>();
    }
  }
}

