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
		int offset = HeaderParser.getOffset(incomingDatagram);
		if (HeaderParser.getStatus(incomingDatagram) != Protocol.FIN) {
			byte[] ackDatagram = manager.formHeader(Protocol.ACK, seqNo + Protocol.ACKSIZE, ackNo, Protocol.ACKSIZE);
			manager.initiateFileArray(offset);
			System.out.println("File array initialized");
			nextState();
			manager.processOutgoingData(ackDatagram);
		} else {
			System.out.println("File does not exist, please try a different command");
			manager.shutdownSession(0, 0);
		}
	}

	private void nextState() {
		manager.setManagerState(new DownloadEstablished(manager));
	}

}
