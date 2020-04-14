package upload;

import communicationProtocols.Protocol;
import header.HeaderConstructor;
import header.HeaderParser;
import managerStates.ManagerState;

public class UploadEstablished implements ManagerState {

	private UploadManager manager;
	private int fileLength;
	private int filePointer;
	private HeaderParser parser;
	private UploadWindow window;

	private byte[] headerToSend;
	private byte[] dataToSend;

	public UploadEstablished(UploadManager managerArg, int fileLengthArg, UploadWindow windowArg) {
		manager = managerArg;
		fileLength = fileLengthArg;
		window = windowArg;
		parser = new HeaderParser();
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		if (parser.getCommand(incomingDatagram) == HeaderConstructor.P) {
			nextState();
			manager.processOutgoingData(Protocol.PAUSE, new byte[] { 0 });
			return;
		}

		int seqNo = parser.getSequenceNumber(incomingDatagram);
		int ackNo = parser.getAcknowledgementNumber(incomingDatagram);

//		System.out.println("The filepointer is at: " + filePointer);

		if (filePointer != fileLength) {
			sendOrDiscardDatagram(seqNo, ackNo);
		} else {
			manager.shutdownSession(seqNo, ackNo);
		}

	}

	public byte[] getLastHeader() {
		return headerToSend;
	}

	public byte[] getLastData() {
		return dataToSend;
	}

	private void nextState() {
		manager.setManagerState(new UploadPaused(manager, this));
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
		dataToSend = manager.getSegmentFromFile(payloadSize, filePointer);
		filePointer += payloadSize;
		headerToSend = manager.formHeader(HeaderConstructor.UL, seqNo, ackToSend, payloadSize);
		manager.processOutgoingData(headerToSend, dataToSend);
	}

	private int computePayloadSize() {
		int payloadSize = Math.min(UploadManager.SIZE - HeaderConstructor.HEADERLENGTH, fileLength - filePointer);
		return payloadSize;
	}

}
