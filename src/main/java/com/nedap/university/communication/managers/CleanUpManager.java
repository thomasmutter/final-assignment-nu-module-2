package managers;

import header.HeaderConstructor;
import header.HeaderParser;
import remaking.Session;
import time.TimeKeeper;

public class CleanUpManager implements PacketManager {

	private HeaderConstructor constructor;
	private HeaderParser parser;
	private Session session;
	private TimeKeeper keeper;

	public CleanUpManager(Session sessionArg) {
		session = sessionArg;
		constructor = new HeaderConstructor();
		parser = new HeaderParser();
		keeper = new TimeKeeper(session);
	}

	@Override
	public void processIncomingData(byte[] data) {
		if (parser.getStatus(data) == (byte) (HeaderConstructor.FIN + HeaderConstructor.ACK)) {
			session.shutdown();
		} else {
			sendFin((byte) (HeaderConstructor.FIN + HeaderConstructor.ACK), data);
			keeper.setFinTimer();
		}

	}

	public void sendFin(byte statusArg, byte[] data) {
		String dataString = "FIN";
		byte[] finDatagram = formHeader(statusArg, data);
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

	private byte[] formHeader(byte statusArg, byte[] oldHeader) {
		byte flags = 0;
		byte status = statusArg;
		int seqNo = parser.getAcknowledgementNumber(oldHeader) + 1;
		int ackNo = parser.getSequenceNumber(oldHeader);
		int checksum = 0;
		int windowSize = 1;
		return constructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

}
