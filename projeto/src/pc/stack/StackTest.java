package pc.stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.cooperari.CSystem;
import org.cooperari.config.CMaxTrials;
import org.cooperari.config.CRaceDetection;
import org.cooperari.config.CScheduling;
import org.cooperari.core.scheduling.CProgramStateFactory;
import org.cooperari.core.scheduling.CSchedulerFactory;
import org.cooperari.junit.CJUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class)
@CMaxTrials(50)
@CRaceDetection(true)
@CScheduling(schedulerFactory=CSchedulerFactory.OBLITUS, stateFactory=CProgramStateFactory.RAW)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class StackTest {

  protected abstract Stack<Integer> createStack();

  // Simple class for storing results.
  static class R {
    Integer v;

  }

  // Sanity test for purely sequential execution
  @Test @CMaxTrials(1)
  public void test0() {
    R a = new R(), b = new R(), c = new R(), d = new R() ;
    Stack<Integer> s = createStack();
    a.v = s.pop();
    s.push(1);
    b.v = s.pop();
    s.push(2);
    s.push(3);
    c.v = s.pop();
    d.v = s.pop();
    assertOneOf (
        array(a,    b,   c,   d ),
        array(null, 1,   3,   2 )
    );
    assertEquals(0, s.size());
  }

  @Test
  public void test1() {
    R a = new R(), b = new R(), c = new R(), d = new R();
    Stack<Integer> s = createStack();

    CSystem.forkAndJoin(
        () -> { s.push(1); },
        () -> { a.v = s.pop(); }
        );
    CSystem.forkAndJoin(
        () -> { s.push(2); },
        () -> { b.v = s.pop(); }
        );
    c.v = s.pop();
    d.v = s.pop();

    assertOneOf (
        array(a,   b,   c,   d  ),
        // push(1) > pop() > push(2) > pop() > pop()
        array(1,    2,    null, null),
        // push(1) > pop() > pop() > push(2) > pop()
        array(1,    null, 2,   null),
        // pop() > push(1) > pop() > push(2) > pop()
        array(null, 1,    2,   null),
        // pop() > push(1) >  push(2) > pop() > pop()
        array(null, 2,    1,   null)
    );
    assertEquals(0, s.size());
  }

  @Test
  public void test2() {
    R a = new R(), b = new R(), c = new R(), d = new R();
    Stack<Integer> s = createStack();

    CSystem.forkAndJoin(
        () -> { s.push(1); s.push(2); },
        () -> { a.v = s.pop(); }
        );
    CSystem.forkAndJoin(
        () -> { s.push(3); },
        () -> { b.v = s.pop(); }
        );
    c.v = s.pop();
    d.v = s.pop();

    assertOneOf (
        array(a,    b,     c,   d ),
        // push(1)  > push(2) > pop(a) > push(3) > pop(b) > pop(c) > pop(d)
        array(2,    3,     1,   null),
        // push(1)  > push(2) > pop(a) > pop(b) > push(3) > pop(c) > pop(d)
        array(2,    1,     3,   null),
        // push(1) > pop(a) > push(2) > push(3) > pop(b) > pop(c) > pop(d)
        array(1,    3,    2,     null),
        // push(1) > pop(a) > push(2) > pop(b) > push(3) > pop(c) > pop(d)
        array(1,    2,    3,     null),
        // pop(a) > push(1)  > push(2) > pop(b) > push(3) > pop(c) > pop(d)
        array(null, 2,    3,     1),
        // pop(a) > push(1) > push(2) > push(3) > pop(b) > pop(c) > pop(d)
        array(null, 3,    2,     1)
    );
    assertEquals(0, s.size());
  }


  @Test
  public void test3() {
    R a = new R(), b = new R(), c = new R(), d = new R();
    Stack<Integer> s = createStack();

    CSystem.forkAndJoin(
        () -> { s.push(1); s.push(2); },
        () -> { a.v = s.pop(); }
        );
    CSystem.forkAndJoin(
        () -> { s.push(3); d.v = s.pop(); },
        () -> { b.v = s.pop(); c.v = s.pop(); }
        );

    assertOneOf (
        array(a,    b,     c,   d ),

        // push(1)  > push(2) > pop(a) > push(3) > pop(b) > pop(c) > pop(d)
        array(2,    3,     1,   null),
        // push(1)  > push(2) > pop(a) > pop(b) > push(3) > pop(c) > pop(d)
        array(2,    1,     3,   null),
        // push(1) > push(2) > pop(a) > push(3) > pop(d) > pop(b) > pop(c)
        array(2,    1,    null,  3),
        // push(1) > push(2) > pop(a) > push(3) > pop(b) > pop(d) > pop(c)
        array(2,    3,    null,  1),

        // push(1) > pop(a) > push(2) > push(3) > pop(b) > pop(c) > pop(d)
        array(1,    3,    2,     null),
        // push(1) > pop(a) > push(2) > pop(b) > push(3) > pop(c) > pop(d)
        array(1,    2,    3,     null),
        // push(2) > push(1) > pop(a) > push(3) > pop(d) > pop(b) > pop(c)
        array(1,    2,    null,  3),
        // push(2) > push(1) > pop(a) > push(3) > pop(b) > pop(d) > pop(c)
        array(1,    3,    null,  2),

        // pop(a) > push(1)  > push(2) > pop(b) > push(3) > pop(c) > pop(d)
        array(null, 2,    3,     1),
        // pop(a) > push(1) > push(2) > push(3) > pop(b) > pop(c) > pop(d)
        array(null, 3,    2,     1),
        // pop() >  push(1) > push(2) > push(3) > pop(d) > pop(b) > pop(c)
        array(null, 2,    1,     3),
        // pop() >  push(1) > push(2) > push(3) > pop(b) > pop(d) > pop(c)
        array(null, 3,    1,     2)

    );
    assertEquals(0, s.size());
  }

  @Test
  public void test4() {
    R a = new R(), b = new R(), c = new R(), d = new R();
    Stack<Integer> s = createStack();

    CSystem.forkAndJoin(
        () -> { a.v = s.pop(); },
        () -> { s.push(1); s.push(2); }
        );
    CSystem.forkAndJoin(
        () -> { c.v = s.pop(); d.v = s.pop(); },
        () -> { b.v = s.pop();  s.push(3); }
        );

    assertOneOf (
        array(a,    b,     c,    d ),

        // pop(a) > push(1) > push(2) > pop(c) > pop(d) > pop(b) > push(3)
        array(null,   null,   2,    1),
        // pop(a) > push(1) > push(2) > pop(c) > pop(b) > pop(d) > push(3)
        array(null,   1,      2,    null),
        // pop(a) > push(1) > push(2) > pop(c) > pop(b) > push(3) > pop(d)
        array(null,   1,      2,    3),
        // pop(a) > push(1) > push(2) > pop(b) > pop(c) > pop(d) > push(3)
        array(null,   2,      1,    null),
        // pop(a) > push(1) > push(2) > pop(d) > push(3) > pop(c) > pop(d)
        array(null,   2,      3,    1),
        // pop(a) > push(1) > push(2) > pop(d) > pop(c) > push(3) > pop(d)
        array(null,   2,      1,    3),

        // push(1) > push(2) > pop(a) > pop(c) > pop(d) > pop(b) > push(3)
        array(2,      null,   1,    null),
        // push(1) > push(2) > pop(a) > pop(c) > pop(b) > pop(d) > push(3)
        array(2,      null,   1,    null),
        // push(1) > push(2) > pop(a) > pop(c) > pop(b) > push(3) > pop(d)
        array(2,      null,   1,    3),
        // push(1) > push(2) > pop(a) > pop(b) > pop(c) > pop(d) > push(c)
        array(2,      1,      null, null),
        // push(1) > push(2) > pop(a) > pop(b) > push(3) > pop(c) > pop(d)
        array(2,      1,      3,    null),
        // push(1) > push(2) > pop(a) > pop(b) > pop(c) > push(3) > pop(d)
        array(2,      1,      null, 3),

        // push(1) > pop(a) > push(2) > pop(c) > pop(d) > pop(b) > push(3)
        array(1,      null,   2,    null),
        // push(1) > pop(a) > push(2) > pop(c) > pop(b) > pop(d) > push(3)
        array(1,      null,   2,    null),
        // push(1) > pop(a) > push(2) > pop(c) > pop(b) > push(3) > pop(d)
        array(1,      null,   2,    3),
        // push(1) > pop(a) > push(2) > pop(b) > pop(c) > pop(d) > push(c)
        array(1,      2,      null, null),
        // push(1) > pop(a) > push(2) > pop(b) > push(3) > pop(c) > pop(d)
        array(1,      2,      3,    null),
        // push(1) > pop(a) > push(2) > pop(b) > pop(c) > push(3) > pop(d)
        array(1,      2,      null, 3)

    );
  }

  @Test
  public void test5() {
    R a = new R(), b = new R(), c = new R(), d = new R();
    Stack<Integer> s = createStack();

    CSystem.forkAndJoin(
        () -> { a.v=s.pop(); s.push(1); b.v=s.pop(); c.v=s.pop();}
        );

    assertOneOf (
        array(a,    b,     c,    d ),
        array(null, 1,     null, null)
    );
  }
  @SafeVarargs
  static <T> T[] array(T... values) {
    return values;
  }

  static void assertOneOf(R[] results, Integer[]... validValues) {
    if (validValues == null || validValues.length == 0) {
      fail("No valid values provided.");
    }
    for (int i = 0; i < validValues.length; i++) {
      if (validValues[i].length != results.length) {
        fail(String.format("VV entry %d has invalid length %d, expected %d.",
            i, validValues[i].length, results.length));
      }
    }
    Integer[] rvalues = new Integer[results.length];
    for (int i = 0; i < results.length; i++) {
      rvalues[i] = results[i].v;
    }
    //System.out.println(Arrays.toString(rvalues));

    for (int i = 0; i < validValues.length; i++) {
      if (Arrays.equals(rvalues, validValues[i])) {
        return;
      }
    }
    String msg = "Incorrect values: " + Arrays.toString(rvalues);
    System.out.printf(msg);
    fail(msg);
  }





}
