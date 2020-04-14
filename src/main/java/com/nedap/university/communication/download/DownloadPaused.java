package download;

import header.HeaderConstructor;
import header.HeaderParser;
import managerStates.ManagerState;

public class DownloadPaused implements ManagerState {

	private DownloadManager manager;
	private DownloadEstablished download;
	private HeaderParser parser;

	public DownloadPaused(DownloadManager managerArg, DownloadEstablished downloadEstablished) {
		manager = managerArg;
		download = downloadEstablished;
		parser = new HeaderParser();
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		if (parser.getCommand(incomingDatagram) == HeaderConstructor.R) {
			nextState();
			byte[] lastAck = download.getLastAck();
			byte[] ack = manager.formHeader(HeaderConstructor.R, parser.getSequenceNumber(lastAck),
					parser.getAcknowledgementNumber(lastAck), parser.getWindowSize(lastAck));
			manager.processOutgoingData(ack);
		}
	}

	private void nextState() {
		manager.setManagerState(download);
	}

}
