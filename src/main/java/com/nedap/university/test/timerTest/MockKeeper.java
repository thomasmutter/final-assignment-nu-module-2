package timerTest;

import java.util.HashMap;
import java.util.Map;

import remaking.Session;
import time.OwnTimer;
import time.TimeKeeper;

public class MockKeeper extends TimeKeeper {

	private Map<Integer, Boolean> unAckedPackets;

	public MockKeeper(Session sessionArg) {
		super(sessionArg);
		unAckedPackets = new HashMap<>();
		// TODO Auto-generated constructor stub
	}

	public void setRetransmissionTimer(int seqNo) {
		timer.getTimerMap().put(System.currentTimeMillis(), seqNo);
		unAckedPackets.put(seqNo, true);
	}

	@Override
	public void retransmit(int sequenceNumber) {
		if (unAckedPackets.containsKey(sequenceNumber)) {
			System.out.println("Resending packet with seqNo " + sequenceNumber);
			setRetransmissionTimer(sequenceNumber);
		}
	}

	public void processIncomingAck(int ackNo) {
		int ackNumber = ackNo;
		System.out.println("Datagram with sequenceNumber: " + ackNumber + " is acked");
		unAckedPackets.remove(ackNumber);
		timer.getTimerMap().values().remove(ackNumber);
		System.out.println("Ack removed from maps");
	}

	public OwnTimer getTimer() {
		return timer;
	}

}
