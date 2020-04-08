package headerTests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import header.HeaderConstructor;

class HeaderConstructorTest {

	private HeaderConstructor header;

	@BeforeEach
	void setUp() throws Exception {
		header = new HeaderConstructor();
	}

	/**
	 * To test this, https://www.browserling.com/tools/ip-to-dec, is used to change
	 * an IPv4 address to the corresponding decimal. The IPv4 address (x.x.x.x) is a
	 * sequence of 4 bytes, so each decimal (x) corresponds to a number between 0 -
	 * 255 To test the operation of this function, a random IPv4 address is
	 * converted to its decimal counter part (a 32 bit integer) Then the four bytes
	 * making up that integer are checked
	 * 
	 * To do this test getBytesFromInt must be set to public
	 */
	@Test
	void testIntegerToBytes() {
		int testInt = 1647902466;

		int[] ipv4Address = new int[4];
		ipv4Address[0] = 98;
		ipv4Address[1] = 56;
		ipv4Address[2] = 255;
		ipv4Address[3] = 2;

		int[] calculatedBytes = header.getBytesFromInt(testInt, 4);

		for (Integer i : calculatedBytes) {
			System.out.println(i);
		}

		assertArrayEquals(calculatedBytes, ipv4Address);

	}

}
