package sessionTermination;

import communicationProtocols.Protocol;
import otherCommands.CleanUpManager;
import session.Session;
import time.FinTimer;

public class SenderTermination implements Terminator {

	private CleanUpManager manager;
	private FinTimer timer;
	private boolean FinSent;

	public SenderTermination(CleanUpManager managerArg, Session session) {
		manager = managerArg;
		timer = new FinTimer(session);
	}

	@Override
	public void terminateSession(byte status, int seqNo, int ackNo) {
		if (FinSent) {
			manager.sendFin((byte) (Protocol.ACK + Protocol.FIN), seqNo, ackNo);
			new Thread(timer).start();
			timer.increaseTimers();
		} else if (status == Protocol.FIN) {
			FinSent = true;
			manager.sendFin(Protocol.FIN, seqNo, ackNo);
		}
	}

}
