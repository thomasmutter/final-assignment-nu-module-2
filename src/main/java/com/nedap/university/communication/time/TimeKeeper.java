package time;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import header.HeaderParser;
import remaking.SessionV2;

public class TimeKeeper {

	private static final long TIMEOUT = 1000;

	private int finTimersSet;
	private Map<Integer, byte[]> unAckedPackets;
	private SessionV2 session;
	private HeaderParser parser;

	public TimeKeeper(SessionV2 sessionArg) {
		session = sessionArg;
		unAckedPackets = new HashMap<>();
		parser = new HeaderParser();
	}

	public int getFinTimersSet() {
		return finTimersSet;
	}

	public void setFinTimer() {
		finTimersSet++;
		Timer timer = new Timer();
		timer.schedule(new FinTimer(this, session), 0);
	}

	public void decrementFinTimers() {
		finTimersSet--;
	}

	public void setRetransmissionTimer(byte[] datagram) {
		int sequenceNumber = parser.getSequenceNumber(parser.getHeader(datagram));
		unAckedPackets.put(sequenceNumber, datagram);
		Timer timer = new Timer();
		timer.schedule(new RetransmissionTimer(this, sequenceNumber), TIMEOUT);
	}

	public void retransmit(int sequenceNumber) {
		if (unAckedPackets.containsKey(sequenceNumber)) {
			System.out.println("Retransmission triggered");
			session.addToSendQueue(unAckedPackets.get(sequenceNumber));
		}
	}

	public void processIncomingAck(byte[] datagram) {
		int ackNumber = parser.getAcknowledgementNumber(parser.getHeader(datagram));
		unAckedPackets.remove(ackNumber);
	}
}
