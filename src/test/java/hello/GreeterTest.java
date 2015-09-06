package hello;

import static org.junit.Assert.*;

import org.junit.Test;

public final class GreeterTest {

	@Test
	public void testSayHello() {
		Greeter greeter = new Greeter();
		assertEquals("Hello world!", greeter.sayHello());
	}
	
}
