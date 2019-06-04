package pc.stack;

/**
 * 
 * Interface for a stack. 
 *
 * <p>
 * The following restrictions should apply to implementations of this interface:
 * <ol>
 *  <li>
 *    The stack is unbounded, i.e., it may contain an arbitrary number of elements.
 *  </li>
 *  <li>
 *    The <code>push()</code> operation does not accept <code>null</code>, throwing
 *  <code>IllegalArgumentException</code> in that case. 
 *  </li>
 *  <li> 
 *    The <code>pop()</code> operation returns <code>null</code> if the stack is empty.
 *  </li>
 * </ul>
 * </p>
 * @param <E> Type of elements in the stack.
 */
public interface Stack<E> {

  /**
   * Get number of elements in the stack.
   */
  int size();

  /**
   * Push an element onto the stack.
   * @param elem The element
   * @throws IllegalArgumentException if <code>elem == null</code>
   */
  void push(E elem) throws IllegalArgumentException;
  
  /**
   * Pop an element from the stack. 
   * @return An element from the stack, or <code>null</code> if the stack is empty.
   */
  E pop(); 
}
