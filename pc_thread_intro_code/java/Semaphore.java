
public class Semaphore {
  private int value; 
  Semaphore(int v) {
    value = v;
  } 
  synchronized void up() {
    value ++;
    notifyAll();
  }
  synchronized void down() throws InterruptedException {
    while (value == 0) {
      wait();
    }
    value--;
  }
  synchronized int getValue() {
    return value;
  }

  static void doIt(int i, Semaphore s) {
    try {
      s.down();
      System.out.println(i + " in critical section");
      s.up();
    }
    catch(InterruptedException e) {
      throw new RuntimeException("Unexpected interrupt", e);
    }
  }
  public static void main(String[] args) throws Exception  {
    Semaphore s = new Semaphore(1);
    Thread a = new Thread(() -> doIt(0,s));
    Thread b = new Thread(() -> doIt(1,s));
    a.start();
    b.start();
    doIt(2, s);
    a.join();
    b.join();
    System.out.println(s.value);
  }

}
