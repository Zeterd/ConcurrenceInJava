package pc.stack;

/**
 * Single-linked node.
 */
public class Node<E> {
  
    /** Data. **/
    public E data;
    
    /** Reference to next element. */
    public Node<E> next;

    /**
     * Constructor.
     * @param data Initial value for data.
     * @param next Initial value for next element.
     */
    public Node(E data, Node<E> next) {
      this.data = data;
      this.next = next;
    }
  }
