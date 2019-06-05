package pc.stack;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * Array-based stack - buggy implementation 1.
 *
 * @param <E> Type of elements in the stack.
 */
public class AArrayStackV1<E> implements Stack<E> {

  private final int INITIAL_CAPACITY = 16;
  private E[] array;
  private final AtomicMarkableReference<Integer> top;

  /**
   * Constructor.
   */
  @SuppressWarnings("unchecked")
  public AArrayStackV1() {
    array = (E[]) new Object[INITIAL_CAPACITY];
    top = new AtomicMarkableReference(0, true);
  }

  @Override
  public int size() {
    boolean[] markHolder = new boolean[1];
    return top.get(markHolder);
  }

  @Override
  public void push(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }

    while(true){
      boolean[] markHolder = new boolean[1];
      int pos = top.get(markHolder);

      if (pos == array.length) {
        array = Arrays.copyOf(array, 2 * array.length);
      }

      if(top.compareAndSet(pos, pos+1, markHolder[0], false)){
        //System.out.println("pos: " + pos);
        //System.out.println("mark: " + markHolder[0]);

        array[pos] = elem;
        //System.out.println("elem: " + array[pos].toString());
        break;
      }

    }
  }

  @Override
  public E pop() {
    boolean[] markHolder = new boolean[1];


    E elem = null;
    //int i = 0;
    while (true) {

      //System.out.println("--->" + i);
      if (top.get(markHolder) == 0) {
        return null;
      }


      int pos = top.get(markHolder);

      if (top.compareAndSet(pos, pos - 1, markHolder[0], false)) {
        //System.out.println("\npos: " + pos);
        //System.out.println("mark: " + markHolder[0]);

        elem = array[pos - 1];
        array[pos - 1] = null;
        break;
        //System.out.println("elem: " + elem.toString());
      }
      //i++;
    }
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
