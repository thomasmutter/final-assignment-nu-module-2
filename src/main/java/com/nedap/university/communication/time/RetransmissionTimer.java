package time;

import java.util.TimerTask;

public class RetransmissionTimer extends TimerTask {

	private TimeKeeper keeper;
	private int sequenceNumber;

	public RetransmissionTimer(TimeKeeper keeperArg, int seqNo) {
		keeper = keeperArg;
		sequenceNumber = seqNo;
	}

	@Override
	public void run() {
		keeper.retransmit(sequenceNumber);
	}

}
