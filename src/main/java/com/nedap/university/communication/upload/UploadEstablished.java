package upload;

import automaticRepeatRequest.UploadWindow;
import header.HeaderConstructor;
import header.HeaderParser;
import managerStates.ManagerState;

public class UploadEstablished implements ManagerState {

	private UploadManager manager;
	private int fileLength;
	private int filePointer;
	private HeaderParser parser;
	private UploadWindow window;

	public UploadEstablished(UploadManager managerArg, int fileLengthArg, UploadWindow windowArg) {
		manager = managerArg;
		fileLength = fileLengthArg;
		window = windowArg;
		parser = new HeaderParser();
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		int seqNo = parser.getSequenceNumber(incomingDatagram);
		int ackNo = parser.getAcknowledgementNumber(incomingDatagram);

		System.out.println("The length of the file is: " + fileLength);
		System.out.println("THe filepointer is at: " + filePointer);

//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		if (filePointer != fileLength) {
			sendOrDiscardDatagram(seqNo, ackNo);
		} else {
			manager.shutdownSession(seqNo, ackNo);
		}

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
		byte[] data = manager.getSegmentFromFile(payloadSize, filePointer);
		filePointer += payloadSize;
		byte[] header = manager.formHeader(seqNo, ackToSend, payloadSize);
		manager.processOutgoingData(header, data);
	}

	private int computePayloadSize() {
		int payloadSize = Math.min(UploadManager.SIZE - HeaderConstructor.HEADERLENGTH, fileLength - filePointer);
		return payloadSize;
	}

}
