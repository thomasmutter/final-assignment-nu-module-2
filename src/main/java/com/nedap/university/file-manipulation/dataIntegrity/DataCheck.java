package dataIntegrity;

import java.util.Arrays;

import communicationProtocols.Protocol;
import header.HeaderConstructor;

public class DataCheck {

	private HeaderConstructor constructor;
	private int totalBitPointer;

	public DataCheck() {
		constructor = new HeaderConstructor();
	}

	public static int getBitFromByteArray(int index, byte[] array) {
		int bytePointer = index / 8;
		int bitPointer = index % 8;
		int bit = ((array[bytePointer] & 0xFF) >> (7 - bitPointer)) & 1;
		return bit;
	}

	public int shiftByBits(int oldRemainder, byte[] file) {
		int newRemainder = oldRemainder;
		while (log2(newRemainder) < log2(Protocol.CRCPOLYNOMIAL) && totalBitPointer < 8 * file.length) {
			int bit = getBitFromByteArray(totalBitPointer, file);
			if (bit == 0) {
				newRemainder <<= 1;
			} else {
				newRemainder <<= 1;
				newRemainder += 1;
			}
			totalBitPointer++;
		}
		return newRemainder;
	}

	public int calculateRemainder(byte[] file) {
		file = appendArrayToData(file, new byte[] { 0, 0 });
		int remainder = shiftByBits(0, file);
		while (totalBitPointer < file.length * 8) {
			remainder ^= Protocol.CRCPOLYNOMIAL;
			remainder = shiftByBits(remainder, file);
		}
		return remainder;
	}

	private byte[] intToByte(int[] array) {
		byte[] bytes = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			bytes[i] = (byte) array[i];
		}
		return bytes;
	}

	public byte[] appendArrayToData(byte[] file, byte[] array) {
		byte[] fileWithCRC = new byte[file.length + array.length];
		System.arraycopy(file, 0, fileWithCRC, 0, file.length);
		System.arraycopy(array, 0, fileWithCRC, file.length, array.length);
		totalBitPointer = 0;
		return fileWithCRC;
	}

	public byte[] appendCRC(byte[] file) {
		int crc = calculateRemainder(file);
		byte[] fileWithCRC = appendArrayToData(file, intToByte(constructor.getBytesFromInt(crc, 2)));
		return fileWithCRC;
	}

	public byte[] stripCRCbits(byte[] file) {
		int totalCRCBits = Integer.numberOfLeadingZeros(Protocol.CRCPOLYNOMIAL);
		byte[] originalFile = Arrays.copyOf(file, file.length - totalCRCBits / 8);
		return originalFile;
	}

	private int log2(int value) {
		return Integer.SIZE - Integer.numberOfLeadingZeros(value);
	}

}
