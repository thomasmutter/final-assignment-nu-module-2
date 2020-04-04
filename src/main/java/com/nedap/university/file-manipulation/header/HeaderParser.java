package header;

public class HeaderParser {

	public byte[] getHeader(byte[] datagram) {
		byte[] header = new byte[HeaderConstructor.HEADERLENGTH];
		for (int i = 0; i < HeaderConstructor.HEADERLENGTH; i++) {
			header[i] = datagram[i];
		}
		return header;
	}

	public int getWindowSize(byte[] header) {
		return header[0];
	}
}
