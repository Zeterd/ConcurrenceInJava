package pc.stack;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

// Uncomment lines as neeeded.
@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ 
  LLinkedStack.Test.class, 
  LArrayStack.Test.class, 
  ALinkedStack.Test.class,
//  ALinkedStackASR.Test.class,
  AArrayStackV1.Test.class,
  AArrayStackV2.Test.class,
//  AArrayStack.Test.class,
})
public class AllTests {

}
