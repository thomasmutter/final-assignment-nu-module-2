package client;

import java.util.Arrays;
import java.util.List;

import download.DownloadEstablished;
import download.DownloadManager;
import header.HeaderConstructor;
import managers.PacketManager;
import managers.ReadDataManager;
import remaking.Session;
import upload.UploadManager;

public class InputInterpreter {

	private static final String PATH = "src/main/java/com/nedap/university/resources/";
	private static List<String> commandList;

	private HeaderConstructor header;
	private String[] inputArray;

	private int offset;

	public InputInterpreter(String input) {
		inputArray = input.split("\\s+");
		header = new HeaderConstructor();
		initializeCommandList();
	}

	public byte[] getDatagramFromInput() {
		byte[] data;
		if (inputArray.length > 1) {
			data = inputArray[1].getBytes();
		} else {
			data = new byte[1];
		}
		byte[] header = formHeader(inputArray[0], data.length);

		byte[] datagram = new byte[header.length + data.length];

		System.arraycopy(header, 0, datagram, 0, header.length);
		System.arraycopy(data, 0, datagram, header.length, data.length);
		return datagram;
	}

	public PacketManager getPacketManagerFromInput(Session session) {
		switch (getFlagsFromCommand(inputArray[0])) {
		case HeaderConstructor.UL:
			return new UploadManager(session, PATH + inputArray[1]);
		case HeaderConstructor.DL:
			DownloadManager manager = new DownloadManager(session, PATH + inputArray[1]);
			manager.setManagerState(new DownloadEstablished(manager, offset));
			return manager;
		default:
			return new ReadDataManager(session);
		}
	}

	private byte[] formHeader(String command, int payloadSize) {
		byte flagsToSend = getFlagsFromCommand(command);
		byte status = 0;
		int seqNo = 0;// (new Random()).nextInt(Integer.MAX_VALUE);
//		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = 0;// (new Random()).nextInt(Integer.MAX_VALUE);
		offset = ackNo;
		int checksum = 0;
		int windowSize = payloadSize;
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
