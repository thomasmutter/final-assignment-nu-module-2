package sessionTermination;

import header.HeaderConstructor;
import managers.CleanUpManager;

public class ReceiverTermination implements Terminator {

	private boolean FinSent;
	private CleanUpManager manager;

	public ReceiverTermination(CleanUpManager managerArg) {
		manager = managerArg;
	}

	@Override
	public void terminateSession(int seqNo, int ackNo) {
		if (FinSent) {
			manager.shutdownSession();
		} else {
			manager.sendFin(HeaderConstructor.FIN, seqNo, ackNo);
			FinSent = true;
		}

	}

}
