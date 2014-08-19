import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClass {
  public Logger logger = LoggerFactory.getLogger(MyClass.class);

  public String getName() {
    return "Hello World";
  }

  public MyClass() {
    logger.info("Hello World");
  }
}