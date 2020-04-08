package managers;

import header.HeaderConstructor;
import header.HeaderParser;
import remaking.SessionV2;
import time.TimeKeeper;

public class CleanUpManager implements PacketManager {

	private HeaderConstructor constructor;
	private HeaderParser parser;
	private SessionV2 session;
	private TimeKeeper keeper;

	public CleanUpManager(SessionV2 sessionArg) {
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
			sendFin((byte) (HeaderConstructor.FIN + HeaderConstructor.ACK));
			keeper.setFinTimer();
		}

	}

	public void sendFin(byte statusArg) {
		String dataString = "FIN";
		byte[] finDatagram = formHeader(statusArg);
		if (statusArg == (byte) (HeaderConstructor.FIN + HeaderConstructor.ACK)) {
			dataString = dataString + " ACK";
		}
		byte[] data = dataString.getBytes();
		System.out.println(dataString + " sent");

		byte[] datagram = new byte[finDatagram.length + data.length];

		System.arraycopy(finDatagram, 0, datagram, 0, finDatagram.length);
		System.arraycopy(data, 0, datagram, finDatagram.length, data.length);

		session.addToSendQueue(datagram);
	}

	private byte[] formHeader(byte statusArg) {
		byte flags = 0;
		byte status = statusArg;
		int seqNo = 0;
		int ackNo = 0;
		int checksum = 0;
		int windowSize = 0;
		return constructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

}
