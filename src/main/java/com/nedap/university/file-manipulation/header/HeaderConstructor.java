package header;

public class HeaderConstructor {

	public static final int HEADERLENGTH = 1;

	public byte[] constructHeader(int payloadSize) {
		byte[] header = new byte[HEADERLENGTH];
		header[0] = (byte) payloadSize;
		return header;
	}
}
