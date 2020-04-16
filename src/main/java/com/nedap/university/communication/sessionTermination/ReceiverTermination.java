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
		if (FinSent && status == (byte) (Protocol.FIN + Protocol.ACK)) {
			manager.shutdownSession();
		} else {
			manager.sendFin(Protocol.FIN, seqNo, ackNo);
			FinSent = true;
		}

	}

}
