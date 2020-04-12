package managers;

import header.HeaderConstructor;
import header.HeaderParser;
import remaking.Session;
import sessionTermination.Terminator;

public class CleanUpManager implements PacketManager {

	private HeaderConstructor constructor;
	private HeaderParser parser;
	private Session session;
	private Terminator terminator;

	public CleanUpManager(Session sessionArg) {
		session = sessionArg;
		constructor = new HeaderConstructor();
		parser = new HeaderParser();
	}

	@Override
	public void processIncomingData(byte[] data) {
		int seqNo = parser.getSequenceNumber(data);
		int ackNo = parser.getAcknowledgementNumber(data);
		terminator.terminateSession(seqNo, ackNo);
	}

	public void sendFin(byte statusArg, int seqNo, int ackNo) {
		String dataString = "FIN";
		byte[] finDatagram = formHeader(statusArg, seqNo, ackNo);
		if (statusArg == (byte) (HeaderConstructor.FIN + HeaderConstructor.ACK)) {
			dataString = dataString + " ACK";
		}
		byte[] payload = dataString.getBytes();
		System.out.println(dataString + " sent");

		byte[] datagram = new byte[finDatagram.length + payload.length];

		System.arraycopy(finDatagram, 0, datagram, 0, finDatagram.length);
		System.arraycopy(payload, 0, datagram, finDatagram.length, payload.length);

		session.addToSendQueue(datagram);
	}

	private byte[] formHeader(byte statusArg, int seqNoArg, int ackNoArg) {
		byte flags = 0;
		byte status = statusArg;
		int seqNo = seqNoArg;
		int ackNo = ackNoArg;
		int checksum = 0;
		int windowSize = 1;
		return constructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

	public void setTerminator(Terminator terminatorArg) {
		terminator = terminatorArg;
	}

	public void shutdownSession() {
		session.shutdown();
	}

}
