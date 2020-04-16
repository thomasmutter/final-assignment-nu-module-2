package download;

import communicationProtocols.Protocol;
import fileConversion.ConversionHandler;
import header.HeaderConstructor;
import managerStates.ManagerState;
import otherCommands.CleanUpManager;
import otherCommands.PacketManager;
import session.Session;
import sessionTermination.ReceiverTermination;
import sessionTermination.Terminator;

public class DownloadManager implements PacketManager {

	private ManagerState managerState;

	private Session session;
	private String path;
	private byte[] fileAsBytes;

	public DownloadManager(Session sessionArg, String pathArg) {
		session = sessionArg;
		path = pathArg;
		managerState = new DownloadInitialize(this);
	}

	@Override
	public void processIncomingData(byte[] incomingDatagram) {
		managerState.translateIncomingHeader(incomingDatagram);
	}

	public void processOutgoingData(byte[] datagram) {
		session.addToSendQueue(datagram);
	}

	public void writeToByteArray(byte[] data, int offset, int payload) {
//		long start = System.currentTimeMillis();

		System.arraycopy(data, 0, fileAsBytes, offset - payload, payload);
//		System.out.println(System.currentTimeMillis() - start);
	}

	public byte[] formHeader(byte statusArg, int seqNo, int ackNo, int offsetArg) {
		byte flags = Protocol.DL;
		byte status = statusArg;

		int offset = offsetArg;
		return HeaderConstructor.constructHeader(flags, status, seqNo, ackNo, offset);
	}

	public void initiateFileArray(int length) {
		fileAsBytes = new byte[length];
	}

	public void finalizeFileTransfer() {
		ConversionHandler converter = new ConversionHandler();
		System.out.println(fileAsBytes.length);
		converter.writeBytesToFile(fileAsBytes, path);
	}

	public void setManagerState(ManagerState state) {
		managerState = state;
	}

	public void pauseSession(boolean toPause) {
		session.pauseSender(toPause);
	}

	public void shutdownSession(int seqNo, int ackNo) {
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new ReceiverTermination(cleanUp);
		session.setManager(cleanUp);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(Protocol.FIN, seqNo, ackNo);
	}

}
