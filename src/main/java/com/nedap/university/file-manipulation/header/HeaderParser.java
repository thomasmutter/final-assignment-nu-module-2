package header;

import communicationProtocols.Protocol;

public class HeaderParser {

	public static byte[] getHeader(byte[] datagram) {
		byte[] header = new byte[Protocol.HEADERLENGTH];
		for (int i = 0; i < Protocol.HEADERLENGTH; i++) {
			header[i] = datagram[i];
		}
		return header;
	}

	public static byte[] getData(byte[] datagram) {
		byte[] data = new byte[datagram.length - Protocol.HEADERLENGTH];
		int j = 0;
		for (int i = Protocol.HEADERLENGTH; i < datagram.length; i++) {
			data[j] = datagram[i];
			j++;
		}
		return data;
	}

//	public static byte[] trimEmptyData(byte[] datagram) {
//		byte[] data = new byte[Protocol.HEADERLENGTH + getWindowSize(datagram)];
//		for (int i = 0; i < data.length; i++) {
//			data[i] = datagram[i];
//		}
//		return data;
//	}

	public static int getOffset(byte[] header) {
		int[] offsetBytes = getBytes(header, 10, 4);
		return getIntFromByteArray(offsetBytes);
	}

	public static int getSequenceNumber(byte[] header) {
		int[] seqNoBytes = getBytes(header, 2, 4);
		return getIntFromByteArray(seqNoBytes);
	}

	public static int getAcknowledgementNumber(byte[] header) {
		int[] ackBytes = getBytes(header, 6, 4);
		return getIntFromByteArray(ackBytes);
	}

	public static int getCommand(byte[] header) {
		return header[0];
	}

	public static byte getStatus(byte[] header) {
		return header[1];
	}

	private static int[] getBytes(byte[] header, int startIndex, int totalBytes) {
		int[] bytes = new int[totalBytes];
		for (int i = 0; i < totalBytes; i++) {
			bytes[i] = (((int) header[startIndex]) & 0xFF);
			startIndex++;
		}
		return bytes;
	}

	public static int getIntFromByteArray(int[] array) {
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
