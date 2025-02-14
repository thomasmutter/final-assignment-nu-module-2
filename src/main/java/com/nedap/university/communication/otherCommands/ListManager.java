package otherCommands;

import java.io.File;
import java.util.Random;

import communicationProtocols.Protocol;
import header.HeaderConstructor;
import header.HeaderParser;
import session.Session;
import sessionTermination.SenderTermination;
import sessionTermination.Terminator;

public class ListManager implements PacketManager {

	private Session session;
	private static final String PATH = System.getProperty("user.dir") + File.separator;

	public ListManager(Session sessionArg) {
		session = sessionArg;
	}

	@Override
	public void processIncomingData(byte[] data) {
		if (HeaderParser.getStatus(HeaderParser.getHeader(data)) != Protocol.ACK) {
			sendList(data);
		} else {
			shutdownSession(HeaderParser.getSequenceNumber(data), HeaderParser.getAcknowledgementNumber(data));
		}
	}

	private void sendList(byte[] data) {
		byte[] payload = getBytesFromPath();
		byte[] header = headerToSend(HeaderParser.getHeader(data), payload.length);

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
		byte flags = Protocol.LS;
		byte status = Protocol.ACK;
		int seqNo = (new Random()).nextInt(Integer.MAX_VALUE);
		int ackNo = HeaderParser.getSequenceNumber(oldHeader);
		int offset = payloadSize;
		return HeaderConstructor.constructHeader(flags, status, seqNo, ackNo, offset);
	}

	private void shutdownSession(int seqNo, int ackNo) {
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new SenderTermination(cleanUp, session);
		session.setManager(cleanUp);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(Protocol.FIN, seqNo, ackNo);
	}

}
