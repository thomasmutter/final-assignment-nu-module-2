package managers;

import header.HeaderConstructor;
import header.HeaderParser;
import remaking.Session;
import sessionTermination.ReceiverTermination;
import sessionTermination.Terminator;

public class ReadDataManager implements PacketManager {

	private Session session;
	private HeaderConstructor headerConstructor;
	private HeaderParser parser;

	public ReadDataManager(Session sessionArg) {
		session = sessionArg;
		headerConstructor = new HeaderConstructor();
		parser = new HeaderParser();
	}

	@Override
	public void processIncomingData(byte[] data) {
		if (parser.getCommand(data) == HeaderConstructor.RP) {
			session.setManager(new UploadManager(session, getDataAsString(data)));
		} else if (parser.getStatus(data) == HeaderConstructor.FIN) {
			shutdownSession(parser.getSequenceNumber(data), parser.getAcknowledgementNumber(data));
			return;
		} else {
			System.out.println(getDataAsString(data));
		}
		sendAck(data);
	}

	private void sendAck(byte[] data) {
		byte[] header = headerToSend(parser.getHeader(data));
		byte[] datagram = new byte[header.length];
		System.arraycopy(header, 0, datagram, 0, header.length);
		session.addToSendQueue(datagram);
	}

	private String getDataAsString(byte[] datagram) {
		byte[] data = new byte[datagram.length - HeaderConstructor.HEADERLENGTH];
		int j = 0;
		for (int i = HeaderConstructor.HEADERLENGTH; i < datagram.length; i++) {
			data[j] = datagram[i];
			j++;
		}
		return new String(data);
	}

	public byte[] headerToSend(byte[] oldHeader) {
		byte flags = HeaderConstructor.LS;
		byte status = HeaderConstructor.ACK;
		int seqNo = parser.getAcknowledgementNumber(oldHeader) + 1;
//		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = parser.getSequenceNumber(oldHeader);
		int checksum = 0;
		int windowSize = 0;
		return headerConstructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

	private void shutdownSession(int seqNo, int ackNo) {
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new ReceiverTermination(cleanUp);
		session.setManager(cleanUp);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(seqNo, ackNo);
	}

}
