package download;

import communicationProtocols.Protocol;
import header.HeaderParser;
import managerStates.ManagerState;

public class DownloadEstablished implements ManagerState {

	private DownloadManager manager;
	private DownloadWindow algorithm;

	public DownloadEstablished(DownloadManager managerArg, int offset) {
		manager = managerArg;
		algorithm = new DownloadWindow(offset);
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		int seqNo = HeaderParser.getSequenceNumber(incomingDatagram);
		int ackNo = HeaderParser.getAcknowledgementNumber(incomingDatagram);
		if (HeaderParser.getStatus(incomingDatagram) == Protocol.P) {
			nextState(incomingDatagram);
		} else if (!containsFin(incomingDatagram)) {
			int payload = HeaderParser.getWindowSize(incomingDatagram);
			byte[] data = HeaderParser.getData(incomingDatagram);
			actAccordingToWindow(seqNo, ackNo, payload, data);
		} else {
			manager.shutdownSession(ackNo, seqNo);
		}
	}

	private boolean containsFin(byte[] data) {
		return HeaderParser.getStatus(data) == Protocol.FIN
				|| HeaderParser.getStatus(data) == (byte) (Protocol.FIN + Protocol.ACK);
	}

	private void nextState(byte[] incomingDatagram) {
		manager.setManagerState(new DownloadPaused(manager, this));
		manager.processIncomingData(incomingDatagram);
	}

	private byte[] composeNewAck(int incomingAck) {
		return manager.formHeader(Protocol.ACK, incomingAck + Protocol.ACKSIZE, algorithm.getAckNo(), 1);
	}

	private void actAccordingToWindow(int seqNo, int ackNo, int payload, byte[] data) {
		if (algorithm.datagramInWindow(seqNo, payload)) {
			algorithm.moveDatagramWindow(seqNo);
			manager.writeToByteArray(data);
			manager.processOutgoingData(composeNewAck(ackNo));
		} else {
			// System.out.println("The sequence number is: " + seqNo);
			// System.out.println("The ack number is: " + ackNo);
			System.out.println("Packet dropped, not in window");
		}
	}

}
