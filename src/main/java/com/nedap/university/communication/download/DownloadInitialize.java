package download;

import communicationProtocols.Protocol;
import header.HeaderParser;
import managerStates.ManagerState;

public class DownloadInitialize implements ManagerState {

	private DownloadManager manager;

	public DownloadInitialize(DownloadManager managerArg) {
		manager = managerArg;
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		int seqNo = HeaderParser.getAcknowledgementNumber(incomingDatagram);
		int ackNo = HeaderParser.getSequenceNumber(incomingDatagram);
		byte[] ackDatagram = manager.formHeader(Protocol.ACK, seqNo, ackNo, Protocol.ACKSIZE);
		manager.processOutgoingData(ackDatagram);
		nextState(ackNo);
	}

	private void nextState(int offset) {
		manager.setManagerState(new DownloadEstablished(manager, offset));
	}

}
