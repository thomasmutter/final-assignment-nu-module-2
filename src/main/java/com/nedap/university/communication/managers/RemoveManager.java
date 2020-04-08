package managers;

import java.io.File;
import java.util.Random;

import header.HeaderConstructor;
import header.HeaderParser;
import remaking.SessionV2;

public class RemoveManager implements PacketManager {

	private SessionV2 session;
	private HeaderConstructor headerConstructor;
	private HeaderParser parser;
	private static final String PATH = "src/main/java/com/nedap/university/resources";

	public RemoveManager(SessionV2 sessionArg) {
		session = sessionArg;
		headerConstructor = new HeaderConstructor();
		parser = new HeaderParser();
	}

	@Override
	public void processIncomingData(byte[] data) {
		if (parser.getStatus(parser.getHeader(data)) != HeaderConstructor.ACK) {
			removeFile(getFileNameFromDatagram(data));
			session.addToSendQueue(headerToSend(data, 0));
		} else {
			session.finalizeSession();
		}
	}

	private String getFileNameFromDatagram(byte[] data) {
		byte[] payload = parser.getData(parser.trimEmptyData(data));
		return new String(payload);
	}

	private void removeFile(String fileName) {
		File here = new File(".");
		System.out.println(fileName.length());
		File file = new File(PATH + File.separator + fileName);
		System.out.println(here.getAbsolutePath());
		System.out.println(file.exists());
		System.out.println("The name of the file is: " + fileName);

		boolean succes = file.delete();
		System.out.println(succes);
	}

	private byte[] headerToSend(byte[] oldHeader, int payloadSize) {
		byte flags = HeaderConstructor.RM;
		byte status = HeaderConstructor.ACK;
		int seqNo = (new Random()).nextInt(Integer.MAX_VALUE);
//		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = parser.getSequenceNumber(oldHeader) + parser.getWindowSize(oldHeader);
		int checksum = 0;
		int windowSize = payloadSize;
//		System.out.println("The payload size is: " + windowSize);
		return headerConstructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

}
