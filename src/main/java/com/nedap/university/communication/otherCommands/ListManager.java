package otherCommands;

import java.io.File;
import java.util.Random;

import header.HeaderConstructor;
import header.HeaderParser;
import remaking.Session;
import sessionTermination.SenderTermination;
import sessionTermination.Terminator;
import time.TimeKeeper;

public class ListManager implements PacketManager {

	private Session session;
	private HeaderConstructor headerConstructor;
	private HeaderParser parser;
	private static final String PATH = System.getProperty("user.dir") + File.separator;

	public ListManager(Session sessionArg) {
		session = sessionArg;
		headerConstructor = new HeaderConstructor();
		parser = new HeaderParser();
	}

	@Override
	public void processIncomingData(byte[] data) {
		if (parser.getStatus(parser.getHeader(data)) != HeaderConstructor.ACK) {
			sendList(data);
		} else {
			shutdownSession(parser.getSequenceNumber(data), parser.getAcknowledgementNumber(data));
		}
	}

	private void sendList(byte[] data) {
		byte[] payload = getBytesFromPath();
		byte[] header = headerToSend(parser.getHeader(data), payload.length);

		byte[] datagram = new byte[header.length + payload.length];

		System.arraycopy(header, 0, datagram, 0, header.length);
		System.arraycopy(payload, 0, datagram, header.length, payload.length);

		session.addToSendQueue(datagram);
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

	public byte[] headerToSend(byte[] oldHeader, int payloadSize) {
		byte flags = HeaderConstructor.LS;
		byte status = HeaderConstructor.ACK;
		int seqNo = (new Random()).nextInt(Integer.MAX_VALUE);
		int ackNo = parser.getSequenceNumber(oldHeader);
		int checksum = 0;
		int windowSize = payloadSize;
		return headerConstructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

	private void shutdownSession(int seqNo, int ackNo) {
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new SenderTermination(cleanUp, new TimeKeeper(session));
		session.setManager(cleanUp);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(HeaderConstructor.FIN, seqNo, ackNo);
	}

}
