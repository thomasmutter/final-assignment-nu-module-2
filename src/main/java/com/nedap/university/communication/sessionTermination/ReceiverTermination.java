package sessionTermination;

import header.HeaderConstructor;
import otherCommands.CleanUpManager;

public class ReceiverTermination implements Terminator {

	private boolean FinSent;
	private CleanUpManager manager;

	public ReceiverTermination(CleanUpManager managerArg) {
		manager = managerArg;
	}

	@Override
	public void terminateSession(byte status, int seqNo, int ackNo) {
		if (FinSent && status == (byte) (HeaderConstructor.FIN + HeaderConstructor.ACK)) {
			manager.shutdownSession();
		} else {
			manager.sendFin(HeaderConstructor.FIN, seqNo, ackNo);
			FinSent = true;
		}

	}

}
