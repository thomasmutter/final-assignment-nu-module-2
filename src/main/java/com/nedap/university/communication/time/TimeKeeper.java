package time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import header.HeaderConstructor;
import header.HeaderParser;
import remaking.Session;

public class TimeKeeper {

	private static final long TIMEOUT = 1000;

	private int finTimersSet;
	private Map<Integer, byte[]> unAckedPackets;
	private Session session;
	private HeaderParser parser;
	protected OwnTimer timer;
	private Thread timerThread;

	private List<Byte> timerBlacklist;

	public TimeKeeper(Session sessionArg) {
		session = sessionArg;
		unAckedPackets = Collections.synchronizedMap(new HashMap<>());
		initializeBlacklist();
		parser = new HeaderParser();
		initiateRetransmissionTimer();
	}

	private void initializeBlacklist() {
		timerBlacklist = new ArrayList<>();
		timerBlacklist.add(HeaderConstructor.FINACK);
		timerBlacklist.add(HeaderConstructor.PAUSEACK);
		timerBlacklist.add(HeaderConstructor.RESUMEACK);
	}

	protected void initiateRetransmissionTimer() {
		timer = new OwnTimer(this);
		timerThread = new Thread(timer);
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

	public void pauseRetransmissionTimer(boolean isPaused) {
		timer.setPaused(isPaused);
	}

//	public void setRetransmissionTimer(byte[] datagram) {
//		int sequenceNumber = parser.getSequenceNumber(parser.getHeader(datagram));
//		// System.out.println("Timer set for datagram with seqNo: " + sequenceNumber);
//		unAckedPackets.put(sequenceNumber, datagram);
//		Timer timer = new Timer();
//		timer.schedule(new RetransmissionTimer(this, sequenceNumber), TIMEOUT);
//	}

	public void setRetransmissionTimer(byte[] datagram) {
		if (!timerBlacklist.contains(parser.getStatus(datagram))) {
			int sequenceNumber = parser.getSequenceNumber(datagram);
			unAckedPackets.put(sequenceNumber, datagram);
			timer.getTimerMap().put(System.currentTimeMillis(), sequenceNumber);
		}
	}

	public void retransmit(int sequenceNumber) {
		if (unAckedPackets.containsKey(sequenceNumber)) {
			System.out.println("Resending packet with seqNo " + sequenceNumber);
			session.addToSendQueue(unAckedPackets.get(sequenceNumber));
		}
	}

	public void processIncomingAck(byte[] datagram) {
		int ackNumber = parser.getAcknowledgementNumber(parser.getHeader(datagram));
//		System.out.println("Datagram with sequenceNumber: " + ackNumber + " is acked");
		unAckedPackets.remove(ackNumber);
		timer.getTimerMap().values().remove(ackNumber);
//		System.out.println("Ack removed from maps");
	}

}
