package client;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import header.HeaderConstructor;
import sessionStates.Session;
import sessionStates.SetupManager;

public class ClientSetupHandler implements SetupManager {

	private HeaderConstructor headerConstructor;
	private byte flags;

	private static List<String> commandList;

	public ClientSetupHandler() {
		headerConstructor = new HeaderConstructor();
		initializeCommandList();
	}

	@Override
	public byte[] setupSession(Session session, byte[] incomingData) {
		switch (flags) {
		case HeaderConstructor.UL:
			session.setSessionState(session.getUploading());
			break;
		case HeaderConstructor.DL:
			session.setSessionState(session.getDownloading());
			break;
		case HeaderConstructor.RM:
			session.setSessionState(session.getRemoving());
			break;
		case HeaderConstructor.RP:
			session.setSessionState(session.getReplacing());
			break;
		case HeaderConstructor.LS:
			session.setSessionState(session.getWaiting());
			break;
		default:
			session.setSessionState(session.getFinalizing());
			break;
		}
		return composeData(incomingData);
	}

	public byte[] composeData(byte[] data) {
		byte[] headerToSend = headerToSend(data);
		byte[] dataToSend = data;
		byte[] datagram = new byte[headerToSend.length + dataToSend.length];

		System.arraycopy(headerToSend, 0, datagram, 0, headerToSend.length);
		System.arraycopy(dataToSend, 0, datagram, headerToSend.length, dataToSend.length);

		return datagram;
	}

	private byte[] headerToSend(byte[] oldHeader) {
		byte flagsToSend = flags;
		System.out.println("The flags are: " + flagsToSend);
		byte status = 0;
		int seqNo = (new Random()).nextInt(Integer.MAX_VALUE);
		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = 0;
		int checksum = 0;
		int windowSize = oldHeader.length;
		System.out.println("The payload size is: " + windowSize);
		return headerConstructor.constructHeader(flagsToSend, status, seqNo, ackNo, windowSize, checksum);
	}

	public void setFlagsFromCommand(String command) {
		System.out.println(commandList.indexOf(command));
		flags = (byte) (1 << commandList.indexOf(command));
	}

	private static void initializeCommandList() {
		String[] commandArray = new String[] { "dl", "ul", "rm", "rp", "ls", "p", "r" };
		commandList = Arrays.asList(commandArray);
	}

}
