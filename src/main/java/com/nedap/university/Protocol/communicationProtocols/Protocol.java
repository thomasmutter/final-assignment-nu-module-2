package communicationProtocols;

import java.util.Random;

public class Protocol {

	public static final int PORT = 8887;
	public static final String BROADCAST = "255.255.255.255";
	public static final int SIGNOFLIFESIZE = 1;

	public static final int PACKETSIZE = 512;

	/**
	 * This integer represents a polynomial that is used for the computation of the
	 * crc value of data. This is a polynomial of order 17 (yielding a 16 bit
	 * checksum), that guarantees detection of up to simultaneous bit errors.
	 * Source: https://users.ece.cmu.edu/~koopman/crc/
	 */
	public static final int CRCPOLYNOMIAL = 0x8FDC;

	/**
	 * Pause and resume initialization messages
	 */
	public static final int TRIGGERLENGTH = 10;

	public static byte[] buildTrigger(byte status) {
		byte[] trigger = new byte[10];
		Random random = new Random();
		trigger[1] = status;
		trigger[0] = 0;
		for (int i = 2; i < trigger.length; i++) {
			trigger[i] = (byte) random.nextInt(Byte.MAX_VALUE);
		}
		return trigger;
	}
}
