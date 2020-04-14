package sessionTermination;

import header.HeaderConstructor;
import otherCommands.CleanUpManager;
import time.TimeKeeper;

public class SenderTermination implements Terminator {

	private TimeKeeper keeper;
	private CleanUpManager manager;
	private boolean FinSent;

	public SenderTermination(CleanUpManager managerArg, TimeKeeper keeperArg) {
		manager = managerArg;
		keeper = keeperArg;
	}

	@Override
	public void terminateSession(byte status, int seqNo, int ackNo) {
		if (FinSent) {
			keeper.setFinTimer();
			manager.sendFin((byte) (HeaderConstructor.ACK + HeaderConstructor.FIN), seqNo, ackNo);
		} else {
			manager.sendFin(HeaderConstructor.FIN, seqNo, ackNo);
			FinSent = true;
		}
	}

}
