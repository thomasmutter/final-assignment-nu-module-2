package download;

import header.HeaderConstructor;
import header.HeaderParser;
import managerStates.ManagerState;

public class DownloadInitialize implements ManagerState {

	private DownloadManager manager;
	private HeaderParser parser;

	public DownloadInitialize(DownloadManager managerArg) {
		manager = managerArg;
		parser = new HeaderParser();
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		int seqNo = parser.getAcknowledgementNumber(incomingDatagram);
		int ackNo = parser.getSequenceNumber(incomingDatagram);
		byte[] ackDatagram = manager.formHeader(HeaderConstructor.DL, seqNo, ackNo, HeaderConstructor.ACKSIZE);
		manager.processOutgoingData(ackDatagram);
		nextState(ackNo);
	}

	private void nextState(int offset) {
		manager.setManagerState(new DownloadEstablished(manager, offset));
	}

}
