class BuggyCounter {
  int value = 0;
  void increment() { value ++; } 
  int getValue()   { return value; } 
}
public class CounterWithRaces {
  static void th_main(BuggyCounter c) {
    c.increment();
  }
  public static void main(String[] args) throws InterruptedException {
    int n = 100;
    BuggyCounter c = new BuggyCounter();
    for (int i = 0 ; i < n; i++) {
      Thread a = new Thread( () -> th_main(c));
      Thread b = new Thread( () -> th_main(c));
      Thread c2 = new Thread( () -> th_main(c));
      Thread d = new Thread( () -> th_main(c));

      a.start(); 
      th_main(c);
      a.join();

      b.start(); 
      th_main(c);
      b.join();

      c2.start(); 
      th_main(c);
      c2.join();

      d.start(); 
      th_main(c);
      d.join();


    }
    System.out.println(c.getValue() + " == " + (n * 2) + " ?");
  }
}


