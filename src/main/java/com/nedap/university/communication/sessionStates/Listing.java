package sessionStates;

import java.io.File;
import java.util.Random;

import header.HeaderConstructor;
import header.HeaderParser;

public class Listing implements SessionState {

	private Session session;
	private HeaderConstructor headerConstructor;
	private HeaderParser parser;
	private static final String PATH = "src/main/java/com/nedap/university/resources";

	public Listing(Session session) {
		this.session = session;
		headerConstructor = new HeaderConstructor();
		parser = new HeaderParser();
	}

	@Override
	public byte[] composeDatagram(byte[] incomingDatagram) {
		byte[] header = headerToSend(parser.getHeader(incomingDatagram));
		byte[] data = getBytesFromPath();

		byte[] datagram = new byte[header.length + data.length];

		System.arraycopy(header, 0, datagram, 0, header.length);
		System.arraycopy(data, 0, datagram, header.length, data.length);

		return datagram;
	}

	@Override
	public void handleData() {
		// TODO Auto-generated method stub

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

	@Override
	public byte[] headerToSend(byte[] oldHeader) {
		byte flags = HeaderConstructor.LS;
		byte status = HeaderConstructor.ACK + HeaderConstructor.FIN;
		int seqNo = (new Random()).nextInt(Integer.MAX_VALUE);
		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = parser.getSequenceNumber(oldHeader) + parser.getWindowSize(oldHeader);
		int checksum = 0;
		int windowSize = oldHeader.length;
		System.out.println("The payload size is: " + windowSize);
		return headerConstructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

}
