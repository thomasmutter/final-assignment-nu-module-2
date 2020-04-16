package header;

import communicationProtocols.Protocol;

public class HeaderConstructor {

	public static byte[] constructHeader(byte flags, byte status, int sequenceNumber, int acknowledgementNumber,
			int offset) {
		byte[] header = new byte[Protocol.HEADERLENGTH];

		// Command flags - 1 byte
		header[0] = flags;

		// Status flags - 1 byte
		header[1] = status;

		// Sequence Number - 4 bytes
		int[] sequenceNumberAsBytes = getBytesFromInt(sequenceNumber, Protocol.NUMBERBYTES);
		header = addToHeader(header, sequenceNumberAsBytes, 2);

		// Sequence Number - 4 bytes
		int[] acknowledgementNumberAsBytes = getBytesFromInt(acknowledgementNumber, Protocol.NUMBERBYTES);
		header = addToHeader(header, acknowledgementNumberAsBytes, 6);

		// Offset
		int[] offsetAsBytes = getBytesFromInt(offset, Protocol.NUMBERBYTES);
		header = addToHeader(header, offsetAsBytes, 10);

		return header;
	}

	public static int[] getBytesFromInt(int input, int totalBytes) {
		// input = input % 500;
		int[] inputAsBytes = new int[totalBytes];
		inputAsBytes[0] = input >> 8 * (totalBytes - 1);
		int remainder = input - (inputAsBytes[0] << (8 * (totalBytes - 1)));
		for (int i = 1; i < inputAsBytes.length; i++) {
			inputAsBytes[i] = remainder >> 8 * (totalBytes - i - 1);
			remainder = remainder - (inputAsBytes[i] << (8 * (totalBytes - i - 1)));
		}
		return inputAsBytes;
	}

	private static byte[] addToHeader(byte[] header, int[] inputArray, int startIndex) {
		for (int i = 0; i < inputArray.length; i++) {
			header[startIndex] = (byte) inputArray[i];
			startIndex++;
		}
		return header;
	}

}
