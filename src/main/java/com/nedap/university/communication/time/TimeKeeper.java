package time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import communicationProtocols.Protocol;
import header.HeaderParser;
import remaking.Session;

public class TimeKeeper {

	private static final long TIMEOUT = 1000;

	private int finTimersSet;
	private Map<Integer, byte[]> unAckedPackets;
	private Session session;
	protected OwnTimer timer;
	private Thread timerThread;

	private List<Byte> timerBlacklist;

	public TimeKeeper(Session sessionArg) {
		session = sessionArg;
		unAckedPackets = Collections.synchronizedMap(new HashMap<>());
		initializeBlacklist();
		initiateRetransmissionTimer();
	}

	private void initializeBlacklist() {
		timerBlacklist = new ArrayList<>();
		timerBlacklist.add(Protocol.FINACK);
		timerBlacklist.add(Protocol.PAUSEACK);
		timerBlacklist.add(Protocol.RESUMEACK);
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

	public void setRetransmissionTimer(byte[] datagram) {
		if (!timerBlacklist.contains(HeaderParser.getStatus(datagram))) {
			int sequenceNumber = HeaderParser.getSequenceNumber(datagram);
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
		int ackNumber = HeaderParser.getAcknowledgementNumber(datagram);
//		System.out.println("Datagram with sequenceNumber: " + ackNumber + " is acked");
		unAckedPackets.remove(ackNumber);
		timer.getTimerMap().values().remove(ackNumber);
//		System.out.println("Ack removed from maps");
	}

}
