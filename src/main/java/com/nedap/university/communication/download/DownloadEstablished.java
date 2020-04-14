package download;

import communicationProtocols.Protocol;
import header.HeaderConstructor;
import header.HeaderParser;
import managerStates.ManagerState;

public class DownloadEstablished implements ManagerState {

	private DownloadManager manager;
	private DownloadWindow algorithm;
	private HeaderParser parser;

	private byte[] lastAckSent;

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
//		System.out.println("The received command is " + parser.getCommand(incomingDatagram));
//		System.out.println(HeaderConstructor.P);
//		System.out.println(HeaderConstructor.P == parser.getCommand(data));
		if (parser.getCommand(incomingDatagram) == HeaderConstructor.P) {
			System.out.println("SENDING PAUSE");
			manager.processOutgoingData(Protocol.PAUSE);
			nextState();
		} else if (!containsFin(incomingDatagram)) {
			actAccordingToWindow(seqNo, ackNo, payload, data);
		} else {
			manager.shutdownSession(ackNo, seqNo);
		}
	}

	public byte[] getLastAck() {
		return lastAckSent;
	}

	private boolean containsFin(byte[] data) {
		return parser.getStatus(data) == HeaderConstructor.FIN
				|| parser.getStatus(data) == (byte) (HeaderConstructor.FIN + HeaderConstructor.ACK);
	}

	private void nextState() {
		manager.setManagerState(new DownloadPaused(manager, this));
	}

	private byte[] composeNewAck(int incomingAck) {
		return manager.formHeader(HeaderConstructor.DL, incomingAck + HeaderConstructor.ACKSIZE, algorithm.getAckNo(),
				1);
	}

	private void actAccordingToWindow(int seqNo, int ackNo, int payload, byte[] data) {
		if (algorithm.datagramInWindow(seqNo, payload)) {
			algorithm.moveDatagramWindow(seqNo);
			manager.writeToByteArray(data);
			lastAckSent = composeNewAck(ackNo);
			manager.processOutgoingData(lastAckSent);
		} else {
			// System.out.println("The sequence number is: " + seqNo);
			// System.out.println("The ack number is: " + ackNo);
			System.out.println("Packet dropped, not in window");
		}
	}

}
