package managers;

import java.io.File;
import java.util.Random;

import header.HeaderConstructor;
import header.HeaderParser;
import remaking.SessionV2;

public class ListManager implements PacketManager {

	private SessionV2 session;
	private HeaderConstructor headerConstructor;
	private HeaderParser parser;
	private static final String PATH = "src/main/java/com/nedap/university/resources";

	public ListManager(SessionV2 sessionArg) {
		session = sessionArg;
		headerConstructor = new HeaderConstructor();
		parser = new HeaderParser();
	}

	@Override
	public void processIncomingData(byte[] data) {
		byte[] header = headerToSend(parser.getHeader(data));
		byte[] payload = getBytesFromPath();

		byte[] datagram = new byte[header.length + payload.length];

		System.arraycopy(header, 0, datagram, 0, header.length);
		System.arraycopy(payload, 0, datagram, header.length, payload.length);

		session.addToSendQueue(datagram);
		session.finalizeSession();
	}

	private byte[] getBytesFromPath() {
		File fileDirectory = new File(PATH);
		String[] listOfFiles = fileDirectory.list();
		String listString = "";
		for (String file : listOfFiles) {
			listString = listString + "\n" + file;
		}
		return listString.getBytes();
	}

	public byte[] headerToSend(byte[] oldHeader) {
		byte flags = HeaderConstructor.LS;
		byte status = HeaderConstructor.ACK;
		int seqNo = (new Random()).nextInt(Integer.MAX_VALUE);
		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = parser.getSequenceNumber(oldHeader) + parser.getWindowSize(oldHeader);
		int checksum = 0;
		int windowSize = oldHeader.length;
		System.out.println("The payload size is: " + windowSize);
		return headerConstructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

}
