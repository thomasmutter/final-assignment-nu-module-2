package headerTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import header.HeaderConstructor;
import header.HeaderParser;

class HeaderParserTest {

	private HeaderParser parser;
	private HeaderConstructor constructor;
	private Random random;

	@BeforeEach
	void setUp() throws Exception {
		parser = new HeaderParser();
		constructor = new HeaderConstructor();
		random = new Random();
	}

	@Test
	void testByteArrayToInt() {
		int expectedInt = 1647902466;
		int[] ipv4Address = new int[4];
		ipv4Address[0] = 98;
		ipv4Address[1] = 56;
		ipv4Address[2] = 255;
		ipv4Address[3] = 2;

		int testInt = parser.getIntFromByteArray(ipv4Address);

		assertEquals(expectedInt, testInt);

	}

	@Test
	void testIfInputCanBeRecovered() {
		int input = random.nextInt(Integer.MAX_VALUE);

		System.out.println("Tested number is: " + input);

		int[] byteArray = constructor.getBytesFromInt(input, 4);
		int result = parser.getIntFromByteArray(byteArray);

		assertEquals(input, result);
	}

}
