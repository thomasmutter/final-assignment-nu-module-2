package upload;

import communicationProtocols.Protocol;
import header.HeaderParser;
import managerStates.ManagerState;

public class UploadEstablished implements ManagerState {

	private UploadManager manager;
	private int filePointer;
	private UploadWindow window;

	public UploadEstablished(UploadManager managerArg, UploadWindow windowArg) {
		manager = managerArg;
		window = windowArg;
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		if (HeaderParser.getStatus(incomingDatagram) == Protocol.P) {
			System.out.println("Pause initiated");
			nextState(incomingDatagram);
			return;
		}

		int seqNo = HeaderParser.getSequenceNumber(incomingDatagram);
		int ackNo = HeaderParser.getAcknowledgementNumber(incomingDatagram);

//		System.out.println("The filepointer is at: " + filePointer);
//		System.out.println("The file size is: " + manager.getFileSize());

		if (ackNo != manager.getFileSize()) {
			sendOrDiscardDatagram(seqNo, ackNo);
		} else {
			manager.shutdownSession(ackNo, seqNo);
		}

	}

	private void nextState(byte[] incomingDatagram) {
		manager.setManagerState(new UploadPaused(manager, this));
		manager.processIncomingData(incomingDatagram);
	}

	private void sendOrDiscardDatagram(int seqNo, int ackNo) {
		if (window.isDataInWindow(seqNo)) {
			window.updateWindow(seqNo, ackNo);
			sendDatagramsInWindow(window.getNumberOfDatagramsToSend(), seqNo, ackNo);
		}
	}

	private void sendDatagramsInWindow(int numberOfPacketsToSend, int ackToSend, int oldSeqNo) {
		int payloadSize = computePayloadSize();
		int seqNo = oldSeqNo + payloadSize;
		byte[] dataToSend = manager.getSegmentFromFile(payloadSize, filePointer);
		filePointer += payloadSize;
		byte[] headerToSend = manager.formHeader(Protocol.ACK, seqNo, ackToSend, filePointer);
		manager.processOutgoingData(headerToSend, dataToSend);
	}

	private int computePayloadSize() {
		int payloadSize = Math.min(Protocol.PACKETSIZE - Protocol.HEADERLENGTH, manager.getFileSize() - filePointer);
		return payloadSize;
	}

}
