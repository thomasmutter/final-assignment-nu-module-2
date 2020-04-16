package sessionTermination;

import communicationProtocols.Protocol;
import otherCommands.CleanUpManager;

public class ReceiverTermination implements Terminator {

	private boolean FinSent;
	private CleanUpManager manager;

	public ReceiverTermination(CleanUpManager managerArg) {
		manager = managerArg;
	}

	@Override
	public void terminateSession(byte status, int seqNo, int ackNo) {
		if (FinSent && (status == Protocol.FINACK)) {
			manager.shutdownSession();
		} else if (status == Protocol.FIN) {
			manager.sendFin(Protocol.FIN, seqNo, ackNo);
			FinSent = true;
		}

	}

}
