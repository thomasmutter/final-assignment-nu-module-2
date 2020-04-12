package time;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import header.HeaderParser;
import remaking.Session;

public class TimeKeeper {

	private static final long TIMEOUT = 1000;

	private int finTimersSet;
	private Map<Integer, byte[]> unAckedPackets;
	private Session session;
	private HeaderParser parser;
	protected OwnTimer timer;

	public TimeKeeper(Session sessionArg) {
		session = sessionArg;
		unAckedPackets = Collections.synchronizedMap(new HashMap<>());
		parser = new HeaderParser();
		initiateRetransmissionTimer();
	}

	protected void initiateRetransmissionTimer() {
		timer = new OwnTimer(this);
		Thread timerThread = new Thread(timer);
		timerThread.setDaemon(true);
		timerThread.start();
	}

	public int getFinTimersSet() {
		return finTimersSet;
	}

	public void setFinTimer() {
		finTimersSet++;
		Timer timer = new Timer(true);
		timer.schedule(new FinTimer(this, session), TIMEOUT * 5);
	}

	public void decrementFinTimers() {
		finTimersSet--;
	}

//	public void setRetransmissionTimer(byte[] datagram) {
//		int sequenceNumber = parser.getSequenceNumber(parser.getHeader(datagram));
//		// System.out.println("Timer set for datagram with seqNo: " + sequenceNumber);
//		unAckedPackets.put(sequenceNumber, datagram);
//		Timer timer = new Timer();
//		timer.schedule(new RetransmissionTimer(this, sequenceNumber), TIMEOUT);
//	}

	public void setRetransmissionTimer(byte[] datagram) {
		int sequenceNumber = parser.getSequenceNumber(parser.getHeader(datagram));
		unAckedPackets.put(sequenceNumber, datagram);
		timer.getTimerMap().put(System.currentTimeMillis(), sequenceNumber);
	}

	public void retransmit(int sequenceNumber) {
		if (unAckedPackets.containsKey(sequenceNumber)) {
			System.out.println("Resending packet with seqNo " + sequenceNumber);
			session.addToSendQueue(unAckedPackets.get(sequenceNumber));
		}
	}

	public void processIncomingAck(byte[] datagram) {
		int ackNumber = parser.getAcknowledgementNumber(parser.getHeader(datagram));
		System.out.println("Datagram with sequenceNumber: " + ackNumber + " is acked");
		unAckedPackets.remove(ackNumber);
		timer.getTimerMap().values().remove(ackNumber);
		System.out.println("Ack removed from maps");
	}
}
