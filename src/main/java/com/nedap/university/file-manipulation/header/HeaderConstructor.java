package header;

public class HeaderConstructor {

	/**
	 * Session state commands
	 */
	public static final byte DL = 1;
	public static final byte UL = 2;
	public static final byte RM = 4;
	public static final byte RP = 8;
	public static final byte LS = 16;
	public static final byte P = 32;
	public static final byte R = 64;

	/**
	 * Communication commands
	 */
	public static final byte ACK = 1;
	public static final byte FIN = 2;

	public static final int HEADERLENGTH = 14;
	private static final int NUMBERBYTES = 4;

	public byte[] constructHeader(byte flags, byte status, int sequenceNumber, int acknowledgementNumber,
			int windowSize, int checksum) {
		byte[] header = new byte[HEADERLENGTH];

		// Command flags - 1 byte
		header[0] = flags;

		// Status flags - 1 byte
		header[1] = status;

		// Sequence Number - 4 bytes
		int[] sequenceNumberAsBytes = getBytesFromInt(sequenceNumber, NUMBERBYTES);
		header = addToHeader(header, sequenceNumberAsBytes, 2);

		// Sequence Number - 4 bytes
		int[] acknowledgementNumberAsBytes = getBytesFromInt(acknowledgementNumber, NUMBERBYTES);
		header = addToHeader(header, acknowledgementNumberAsBytes, 6);

		// Window Size - 2 bytes
		int[] windowSizeAsBytes = getBytesFromInt(windowSize, 2);
		header = addToHeader(header, windowSizeAsBytes, 10);

		// Window Size - 2 bytes
		int[] checksumAsBytes = getBytesFromInt(checksum, 2);
		header = addToHeader(header, checksumAsBytes, 12);

		return header;
	}

	public int[] getBytesFromInt(int input, int totalBytes) {
		int[] inputAsBytes = new int[totalBytes];
		inputAsBytes[0] = input >> 8 * (totalBytes - 1);
		int remainder = input - (inputAsBytes[0] << (8 * (totalBytes - 1)));
		for (int i = 1; i < inputAsBytes.length; i++) {
			inputAsBytes[i] = remainder >> 8 * (totalBytes - i - 1);
			remainder = remainder - (inputAsBytes[i] << (8 * (totalBytes - i - 1)));
		}
		return inputAsBytes;
	}

	private byte[] addToHeader(byte[] header, int[] inputArray, int startIndex) {
		for (int i = 0; i < inputArray.length; i++) {
			header[startIndex] = (byte) inputArray[i];
			startIndex++;
		}
		return header;
	}

}
