package download;

import communicationProtocols.Protocol;
import managerStates.ManagerState;
import session.Paused;

public class DownloadPaused extends Paused implements ManagerState {

	private DownloadManager manager;
	private DownloadEstablished download;

	public DownloadPaused(DownloadManager managerArg, DownloadEstablished downloadEstablished) {
		super();
		manager = managerArg;
		download = downloadEstablished;
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		resumeOrPause(incomingDatagram);
	}

	@Override
	public void resumeOperation() {
		manager.setManagerState(download);
		manager.pauseSession(false);
	}

	@Override
	protected void sendMessage(byte status, int seqNo, int ackNo) {
		byte[] ack = manager.formHeader(status, seqNo, ackNo, Protocol.ACKSIZE);
		manager.processOutgoingData(ack);

	}

	@Override
	public void pauseOperation() {
		System.out.println("Pausing");
		manager.pauseSession(true);
	}

}
