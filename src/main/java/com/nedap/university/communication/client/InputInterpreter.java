package client;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import header.HeaderConstructor;
import managers.DownloadManager;
import managers.PacketManager;
import managers.ReadDataManager;
import managers.UploadManager;
import remaking.SessionV2;

public class InputInterpreter {

	private static final String PATH = "src/main/java/com/nedap/university/resources";
	private static List<String> commandList;

	private HeaderConstructor header;
	private String[] inputArray;

	public InputInterpreter(String input) {
		inputArray = input.split("\\s+");
		header = new HeaderConstructor();
		initializeCommandList();
	}

	public byte[] getDatagramFromInput() {
		byte[] data = inputArray[1].getBytes();
		byte[] header = formHeader(inputArray[0], data.length);

		if (inputArray.length > 1) {
			data = inputArray[1].getBytes();
		}

		byte[] datagram = new byte[header.length + data.length];

		System.arraycopy(header, 0, datagram, 0, header.length);
		System.arraycopy(data, 0, datagram, header.length, data.length);
		return datagram;
	}

	public PacketManager getPacketManagerFromInput(SessionV2 session) {
		switch (getFlagsFromCommand(inputArray[0])) {
		case HeaderConstructor.UL:
			return new UploadManager(session, PATH + inputArray[1]);
		case HeaderConstructor.DL:
			return new DownloadManager(session, PATH + inputArray[1]);
		default:
			return new ReadDataManager(session);
		}
	}

	private byte[] formHeader(String command, int payloadSize) {
		byte flagsToSend = getFlagsFromCommand(command);
		System.out.println("The flags are: " + flagsToSend);
		byte status = 0;
		int seqNo = (new Random()).nextInt(Integer.MAX_VALUE);
		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = 0;
		int checksum = 0;
		int windowSize = payloadSize;
		System.out.println("The payload size is: " + windowSize);
		return header.constructHeader(flagsToSend, status, seqNo, ackNo, windowSize, checksum);
	}

	private byte getFlagsFromCommand(String command) {
		return (byte) (1 << commandList.indexOf(command));
	}

	private static void initializeCommandList() {
		String[] commandArray = new String[] { "dl", "ul", "rm", "rp", "ls", "p", "r" };
		commandList = Arrays.asList(commandArray);
	}

}
