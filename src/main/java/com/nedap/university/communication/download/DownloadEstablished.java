package download;

import header.HeaderConstructor;
import header.HeaderParser;
import managerStates.ManagerState;

public class DownloadEstablished implements ManagerState {

	private DownloadManager manager;
	private DownloadWindow algorithm;
	private HeaderParser parser;

	public DownloadEstablished(DownloadManager managerArg, int offset) {
		manager = managerArg;
		parser = new HeaderParser();
		algorithm = new DownloadWindow(offset);
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		int seqNo = parser.getSequenceNumber(incomingDatagram);
		int ackNo = parser.getAcknowledgementNumber(incomingDatagram);
		int payload = parser.getWindowSize(incomingDatagram);
		byte[] data = parser.getData(incomingDatagram);
		if (parser.getStatus(incomingDatagram) != HeaderConstructor.FIN) {
			actAccordingToWindow(seqNo, ackNo, payload, data);
		} else {
			manager.shutdownSession(seqNo, ackNo);
		}
	}

	private byte[] composeNewAck(int incomingAck) {
		return manager.formHeader(incomingAck + HeaderConstructor.ACKSIZE, algorithm.getAckNo(), 1);
	}

	private void actAccordingToWindow(int seqNo, int ackNo, int payload, byte[] data) {
		if (algorithm.datagramInWindow(seqNo, payload)) {
			algorithm.moveDatagramWindow(seqNo);
			manager.writeToByteArray(data);
			manager.processOutgoingData(composeNewAck(ackNo));
		} else {
			System.out.println("The sequence number is: " + seqNo);
			System.out.println("The ack number is: " + ackNo);
			System.out.println("Packet dropped, not in window");
		}
	}

}
