package pc.set;

import java.util.Random;

import pc.util.Benchmark;

/**
 * Benchmark program for stack implementations.
 */
public class SetBenchmark {

  private static final int DURATION = 10;
  private static final int MAX_THREADS = 32;
  private static final int N = 256;

  /**
   * Program to run a benchmark over set implementations.
   * @param args Arguments are ignored
   */
  public static void main(String[] args) {
    //double serial = runBenchmark(1, new UStack<Integer>());

    for (int n = 1; n <= MAX_THREADS; n = n * 2) {
      runBenchmark(n, new LHashSet<Integer>(false));
      runBenchmark(n, new LHashSet<Integer>(true));
    }
  }

  private static void runBenchmark(int threads, Set<Integer> s) {
    for (int i = 0; i < N; i++) { 
      s.add(i); 
    }
    Benchmark b = new Benchmark(threads, DURATION, new SetOperation(s));
    System.out.printf("%d threads using %s ... ", threads, s.getClass().getSimpleName());
    System.out.printf("%.2f Mops/s%n", b.run());
  }

  private static class SetOperation implements Runnable {
    private final Random rng;
    private final Set<Integer> set;

    SetOperation(Set<Integer> s) {
      this.set = s;
      rng = new Random();
    }

    @Override
    public void run() {
      int op = rng.nextInt(10);
      int v = rng.nextInt(N);
      switch (op) {
        case 0:
          set.add(v);
          break;
        case 1:
          set.remove(v);
          break;
        default:
          set.contains(v);
      }
    }
  }
}


