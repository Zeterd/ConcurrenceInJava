package pc.stack;

import java.util.Random;

import pc.util.Benchmark;

/**
 * Benchmark program for stack implementations.
 */
public class StackBenchmark {

  private static final int DURATION = 10;
  private static final int MAX_THREADS = 32;
  private static final int INITIAL_ELEMENTS_IN_STACK = 100;

  /**
   * Program to run a benchmark over stack implementations.
   * @param args Arguments are ignroed
   */
  public static void main(String[] args) {
    //double serial = runBenchmark(1, new UStack<Integer>());

    for (int n = 1; n <= MAX_THREADS; n = n * 2) {
      runBenchmark(n, new LLinkedStack<Integer>());
      runBenchmark(n, new LArrayStack<Integer>());
      runBenchmark(n, new ALinkedStack<Integer>(false));
      runBenchmark(n, new ALinkedStack<Integer>(true));
//      runBenchmark(n, new ALinkedStackASR<Integer>(false));
//      runBenchmark(n, new ALinkedStackASR<Integer>(true));
//      runBenchmark(n, new AArrayStack<Integer>(false));
//      runBenchmark(n, new AArrayStack<Integer>(true));
    }
  }

  private static void runBenchmark(int threads, Stack<Integer> s) {
    for (int i = 0; i < INITIAL_ELEMENTS_IN_STACK; i++) { 
      s.push(i); 
    }
    Benchmark b = new Benchmark(threads, DURATION, new StackOperation(s));
    System.out.printf("%d threads using %s ... ", threads, s.getClass().getSimpleName());
    System.out.printf("%.2f Mops/s%n", b.run());
  }

  private static class StackOperation implements Runnable {
    private final Random rng;
    private final Stack<Integer> stack;

    StackOperation(Stack<Integer> s) {
      this.stack = s;
      rng = new Random();
    }

    @Override
    public void run() {
      int v = rng.nextInt();
      if (v % 2 == 0) {
        stack.push(v);
      } else {
        stack.pop();
      }
    }
  }
}


