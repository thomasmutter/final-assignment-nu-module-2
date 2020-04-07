package header;

public class HeaderParser {

	public byte[] getHeader(byte[] datagram) {
		byte[] header = new byte[HeaderConstructor.HEADERLENGTH];
		for (int i = 0; i < HeaderConstructor.HEADERLENGTH; i++) {
			header[i] = datagram[i];
		}
		return header;
	}

	public int getChecksum(byte[] header) {
		int[] checksumBytes = getBytes(header, 12, 2);
		return getIntFromByteArray(checksumBytes);
	}

	public int getWindowSize(byte[] header) {
		int[] windowBytes = getBytes(header, 10, 2);
		return getIntFromByteArray(windowBytes);
	}

	public int getSequenceNumber(byte[] header) {
		int[] seqNoBytes = getBytes(header, 2, 4);
		return getIntFromByteArray(seqNoBytes);
	}

	public int getAcknowledgementNumber(byte[] header) {
		int[] ackBytes = getBytes(header, 6, 4);
		return getIntFromByteArray(ackBytes);
	}

	public int getCommand(byte[] header) {
		return header[0];
	}

	public int getStatus(byte[] header) {
		return header[1];
	}

	private int[] getBytes(byte[] header, int startIndex, int totalBytes) {
		int[] bytes = new int[totalBytes];
		for (int i = 0; i < totalBytes; i++) {
			bytes[i] = (int) header[startIndex];
			startIndex++;
		}
		return bytes;
	}

	public int getIntFromByteArray(int[] array) {
		int intValue = 0;
		for (int i = 0; i < array.length; i++) {
			// System.out.println("8bit: " + array[i]);
			int byteToInt = array[i] << 8 * (array.length - i - 1);
			// System.out.println(byteToInt);
			intValue = intValue + byteToInt;
		}
		return intValue;
	}
}
