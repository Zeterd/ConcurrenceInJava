class Counter {
  int value = 0;
  Object l = new Object();
  void increment() { 
    synchronized(l) {
      value ++; 
    }
  } 
  int getValue()   { return value; } 
}

public class CounterWithLock {
  static void th_main(BuggyCounter c) {
    c.increment();
  }
  public static void main(String[] args) throws InterruptedException {
    int n = 100;
    BuggyCounter c = new BuggyCounter();
    for (int i = 0 ; i < n; i++) {
      Thread a = new Thread( () -> th_main(c));
      a.start(); 
      th_main(c);
      a.join();
    }
    System.out.println(c.getValue() + " == " + (n * 2) + " ?");
  }
}


