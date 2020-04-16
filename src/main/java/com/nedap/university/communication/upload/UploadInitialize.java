package upload;

import communicationProtocols.Protocol;
import header.HeaderParser;
import managerStates.ManagerState;

public class UploadInitialize implements ManagerState {

	private UploadManager manager;

	public UploadInitialize(UploadManager managerArg) {
		manager = managerArg;
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		int incomingSeq = HeaderParser.getSequenceNumber(incomingDatagram);
		nextState();
		manager.processOutgoingData(
				manager.formHeader(Protocol.ACK, Protocol.ACKSIZE, incomingSeq, manager.getFileSize()),
				new byte[] { 0 });

	}

	private void nextState() {
		UploadEstablished established = new UploadEstablished(manager, new UploadWindow());
		manager.setManagerState(established);
	}

}
