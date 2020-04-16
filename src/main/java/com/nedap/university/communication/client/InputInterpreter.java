package client;

import java.util.Arrays;
import java.util.List;

import communicationProtocols.Protocol;
import download.DownloadManager;
import header.HeaderConstructor;
import otherCommands.ReadDataManager;
import remaking.Session;
import upload.UploadEstablished;
import upload.UploadManager;
import upload.UploadWindow;

public class InputInterpreter {

	private static final String PATH = "src/main/java/com/nedap/university/resources/";
	private static List<String> commandList;

	private String[] inputArray;
	private Client client;

	public InputInterpreter(String input, Client clientArg) {
		inputArray = input.split("\\s+");
		client = clientArg;
		initializeCommandList();
	}

	private byte[] getDatagramFromInput(int offset) {
		byte[] data;
		if (inputArray.length > 1) {
			data = inputArray[1].getBytes();
		} else {
			data = new byte[1];
		}
		byte[] header = formHeader(inputArray[0], offset);

		byte[] datagram = new byte[header.length + data.length];

		System.arraycopy(header, 0, datagram, 0, header.length);
		System.arraycopy(data, 0, datagram, header.length, data.length);
		return datagram;
	}

	public byte[] setUpSession(Session session) {
		switch (getFlagsFromCommand(inputArray[0])) {
		case Protocol.UL:
			UploadManager uploadManager = new UploadManager(session, PATH + inputArray[1]);
			uploadManager.setManagerState(new UploadEstablished(uploadManager, new UploadWindow()));
			session.setManager(uploadManager);
			return getDatagramFromInput(uploadManager.getFileSize());
		case Protocol.DL:
			DownloadManager downloadManager = new DownloadManager(session, PATH + inputArray[1]);
			session.setManager(downloadManager);
			return getDatagramFromInput(0);
		default:
			session.setManager(new ReadDataManager(session));
			return getDatagramFromInput(0);
		}
	}

	private byte[] formHeader(String command, int offsetArg) {
		byte flagsToSend = getFlagsFromCommand(command);
		byte status = 0;
		int seqNo = 0;// (new Random()).nextInt(Integer.MAX_VALUE);
//		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = 0;// (new Random()).nextInt(Integer.MAX_VALUE);
		int offset = offsetArg;
		return HeaderConstructor.constructHeader(flagsToSend, status, seqNo, ackNo, offset);
	}

	private byte getFlagsFromCommand(String command) {
		return (byte) (1 << commandList.indexOf(command));
	}

	private static void initializeCommandList() {
		String[] commandArray = new String[] { "dl", "ul", "rm", "rp", "ls", "p", "r" };
		commandList = Arrays.asList(commandArray);
	}

}
