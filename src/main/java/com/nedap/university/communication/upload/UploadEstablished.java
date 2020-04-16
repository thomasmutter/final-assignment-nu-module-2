package upload;

import communicationProtocols.Protocol;
import header.HeaderParser;
import managerStates.ManagerState;

public class UploadEstablished implements ManagerState {

	private UploadManager manager;
	private int fileLength;
	private int filePointer;
	private UploadWindow window;

	public UploadEstablished(UploadManager managerArg, int fileLengthArg, UploadWindow windowArg) {
		manager = managerArg;
		fileLength = fileLengthArg;
		window = windowArg;
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		if (HeaderParser.getStatus(incomingDatagram) == Protocol.P) {
			nextState(incomingDatagram);
			return;
		}

		int seqNo = HeaderParser.getSequenceNumber(incomingDatagram);
		int ackNo = HeaderParser.getAcknowledgementNumber(incomingDatagram);

//		System.out.println("The filepointer is at: " + filePointer);

		if (ackNo != fileLength) {
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
		byte[] headerToSend = manager.formHeader(Protocol.ACK, seqNo, ackToSend, payloadSize);
		manager.processOutgoingData(headerToSend, dataToSend);
	}

	private int computePayloadSize() {
		int payloadSize = Math.min(Protocol.PACKETSIZE - Protocol.HEADERLENGTH, fileLength - filePointer);
		return payloadSize;
	}

}
