package time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import communicationProtocols.Protocol;
import header.HeaderParser;
import session.Session;

public class TimeKeeper {

	private Map<Integer, byte[]> unAckedPackets;
	private Map<Integer, Long> reverseTimeMap;
	private Session session;
	protected OwnTimer timer;
	private Thread timerThread;
	private int uniqueAcks;

	private List<Byte> timerBlacklist;

	public TimeKeeper(Session sessionArg) {
		session = sessionArg;
		unAckedPackets = Collections.synchronizedMap(new HashMap<>());
		reverseTimeMap = new HashMap<>();
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
		timerThread.start();
	}

	public void pauseRetransmissionTimer(boolean isPaused) {
		timer.setPaused(isPaused);
	}

	public void setRetransmissionTimer(byte[] datagram) {
		if (!timerBlacklist.contains(HeaderParser.getStatus(datagram))) {
			int sequenceNumber = HeaderParser.getSequenceNumber(datagram);
			unAckedPackets.put(sequenceNumber, datagram);
			timer.getTimerMap().put(System.currentTimeMillis(), sequenceNumber);
			reverseTimeMap.put(sequenceNumber, System.currentTimeMillis());
		}
	}

	public void retransmit(int sequenceNumber) {
		synchronized (unAckedPackets) {
			if (unAckedPackets.containsKey(sequenceNumber)) {
//				System.out.println("Resending packet with seqNo " + sequenceNumber);
				session.addToSendQueue(unAckedPackets.get(sequenceNumber));
			}
		}
	}

	public void setRttForRetransmission(long rtt) {
		timer.setSleepTimer(rtt);
	}

	public long getRttFromTimer() {
		return timer.getRtt();
	}

	public void closeTimer() {
		timer.setStopped(true);
	}

	public void processIncomingAck(byte[] datagram) {
		int ackNumber = HeaderParser.getAcknowledgementNumber(datagram);
		timer.getTimerMap().values().remove(ackNumber);

		if (reverseTimeMap.containsKey(ackNumber)) {
			uniqueAcks++;

			long avgRtt = (timer.getRtt() + System.currentTimeMillis() - reverseTimeMap.get(ackNumber)) / uniqueAcks;
			long rtt = Math.max(avgRtt, 100);
//			System.out.println(rtt);
			setRttForRetransmission(rtt);
			reverseTimeMap.remove(ackNumber);
		}

	}

}
