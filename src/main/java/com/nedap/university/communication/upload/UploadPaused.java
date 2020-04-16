package upload;

import communicationProtocols.Protocol;
import managerStates.ManagerState;
import remaking.Paused;

public class UploadPaused extends Paused implements ManagerState {

	private UploadManager manager;
	private UploadEstablished upload;

	public UploadPaused(UploadManager managerArg, UploadEstablished previousState) {
		manager = managerArg;
		upload = previousState;
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		resumeOrPause(incomingDatagram);
	}

	@Override
	public void resumeOperation() {
		manager.setManagerState(upload);
		manager.pauseSession(false);

	}

	@Override
	protected void sendMessage(byte status, int seqNo, int ackNo) {
		byte[] ack = manager.formHeader(status, seqNo, ackNo, Protocol.ACKSIZE);
		manager.processOutgoingData(ack, new byte[] { 0 });

	}

	@Override
	public void pauseOperation() {
		System.out.println("Pausing");
		manager.pauseSession(true);
	}

}
