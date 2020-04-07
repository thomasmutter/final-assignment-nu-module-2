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

	private String command;
	private static List<String> commandList;
	private HeaderConstructor header;

	public InputInterpreter(String input) {
		command = input;
		header = new HeaderConstructor();
		initializeCommandList();
	}

	public byte[] getDatagramFromInput() {
		String[] commandArray = command.split("\\s+");
		byte[] header = formHeader(commandArray[0]);
		byte[] data = new byte[1];

		if (commandArray.length > 1) {
			data = commandArray[1].getBytes();
		}

		byte[] datagram = new byte[header.length + data.length];

		System.arraycopy(header, 0, datagram, 0, header.length);
		System.arraycopy(data, 0, datagram, header.length, data.length);
		return datagram;
	}

	public PacketManager getPacketManagerFromInput(SessionV2 session) {
		switch (getFlagsFromCommand(command.split("\\s+")[0])) {
		case HeaderConstructor.UL:
			return new UploadManager(session);
		case HeaderConstructor.DL:
			return new DownloadManager(session);
		default:
			return new ReadDataManager(session);
		}
	}

	private byte[] formHeader(String command) {
		byte flagsToSend = getFlagsFromCommand(command);
		System.out.println("The flags are: " + flagsToSend);
		byte status = 0;
		int seqNo = (new Random()).nextInt(Integer.MAX_VALUE);
		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = 0;
		int checksum = 0;
		int windowSize = 0;
		System.out.println("The payload size is: " + windowSize);
		return header.constructHeader(flagsToSend, status, seqNo, ackNo, windowSize, checksum);
	}

	private byte getFlagsFromCommand(String command) {
		System.out.println(commandList.indexOf(command));
		return (byte) (1 << commandList.indexOf(command));
	}

	private static void initializeCommandList() {
		String[] commandArray = new String[] { "dl", "ul", "rm", "rp", "ls", "p", "r" };
		commandList = Arrays.asList(commandArray);
	}

}
