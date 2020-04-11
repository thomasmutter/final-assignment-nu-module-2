package download;

import automaticRepeatRequest.ArqAlgorithm;
import header.HeaderConstructor;
import header.HeaderParser;
import managerStates.ManagerState;

public class DownloadEstablished implements ManagerState {

	private DownloadManager manager;
	private ArqAlgorithm algorithm;
	private HeaderParser parser;

	public DownloadEstablished(DownloadManager managerArg) {
		manager = managerArg;
		parser = new HeaderParser();
		algorithm = new StopAndWaitDownload();
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		int seqNo = parser.getSequenceNumber(incomingDatagram);
		int ackNo = parser.getAcknowledgementNumber(incomingDatagram);
		int payload = parser.getWindowSize(incomingDatagram);
		if (algorithm.getAckNo() != seqNo - 1) {
			actAccordingToWindow(seqNo, ackNo, payload);
		} else {
			manager.shutdownSession(seqNo, ackNo);
		}
	}

	@Override
	public void nextState() {
		// TODO Auto-generated method stub
	}

	private byte[] composeNewAck(int incomingAck) {
		return manager.formHeader(incomingAck + HeaderConstructor.ACKSIZE, algorithm.getAckNo(), 1);
	}

	private void actAccordingToWindow(int seqNo, int ackNo, int payload) {
		if (algorithm.datagramInWindow(seqNo, payload)) {
			algorithm.moveDatagramWindow(seqNo, payload);
			manager.processOutgoingData(composeNewAck(ackNo));
		} else {
			System.out.println("Packet dropped, not in window");
		}
	}

}
