package pc.stack;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of exponential back-off.
 * 
 * <p>
 * Note that there are data races in the use of this class, 
 * but they are tolerable in this case: the back-off 
 * limit is in any case always guaranteed to be in 
 * a valid range.
 * </p>
 *
 */
public final class Backoff {

  private static final int NANOS_PER_MILLIS = 1_000_000;
  private static final int MIN_BACKOFF = 1;
  private static final int MAX_BACKOFF = NANOS_PER_MILLIS;
  private int limit;
  
  /**
   * Constructor.
   */
  public Backoff() {
    limit = MIN_BACKOFF;
  }
  
  /**
   * Perform a delay and double the time for future back-off delays.
   */
  public void delay() {
    try {
      int delay = 1 + ThreadLocalRandom.current().nextInt(limit);
      Thread.sleep(delay / NANOS_PER_MILLIS, (int) (delay % NANOS_PER_MILLIS));
      limit = Math.min(MAX_BACKOFF, limit * 2);
    }
    catch(InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Halve the limit for back-off delays.
   */
  public void diminish() {
    limit = Math.max(MIN_BACKOFF, limit / 2);
  }
}
