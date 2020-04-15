package download;

import header.HeaderConstructor;
import managerStates.ManagerState;
import remaking.Paused;

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
	}

	@Override
	protected void sendMessage(byte status, int seqNo, int ackNo) {
		byte[] ack = manager.formHeader(status, seqNo, ackNo, HeaderConstructor.ACKSIZE);
		manager.processOutgoingData(ack);

	}

}
