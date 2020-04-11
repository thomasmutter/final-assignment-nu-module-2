package download;

import java.util.Random;

import header.HeaderConstructor;
import header.HeaderParser;
import managerStates.ManagerState;

public class DownloadInitialize implements ManagerState {

	private DownloadManager manager;
	private HeaderParser parser;

	public DownloadInitialize(DownloadManager managerArg) {
		manager = managerArg;
		parser = new HeaderParser();
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		int seqNo = parser.getAcknowledgementNumber(incomingDatagram);
		int ackNo = new Random().nextInt(Integer.MAX_VALUE);
		byte[] ackDatagram = manager.formHeader(seqNo, ackNo, HeaderConstructor.ACKSIZE);
		manager.processOutgoingData(ackDatagram);
		nextState();
	}

	@Override
	public void nextState() {
		manager.setManagerState(new DownloadEstablished(manager));
	}

}
