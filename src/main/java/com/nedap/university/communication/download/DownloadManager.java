package download;

import java.util.Arrays;

import communicationProtocols.Protocol;
import fileConversion.ConversionHandler;
import header.HeaderConstructor;
import managerStates.ManagerState;
import otherCommands.CleanUpManager;
import otherCommands.PacketManager;
import remaking.Session;
import sessionTermination.ReceiverTermination;
import sessionTermination.Terminator;

public class DownloadManager implements PacketManager {

	private ManagerState managerState;

	private Session session;
	private String path;
	private byte[] fileAsBytes = new byte[0];

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

	public void writeToByteArray(byte[] data) {
		int oldLength = fileAsBytes.length;
		fileAsBytes = Arrays.copyOf(fileAsBytes, fileAsBytes.length + data.length);
		System.arraycopy(data, 0, fileAsBytes, oldLength, data.length);
	}

	public byte[] formHeader(byte statusArg, int seqNo, int ackNo, int payloadSize) {
		byte flags = Protocol.DL;
		byte status = statusArg;

		int checksum = 0;
		int windowSize = payloadSize;
		return HeaderConstructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

	private void finalizeFileTransfer() {
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
		finalizeFileTransfer();
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new ReceiverTermination(cleanUp);
		session.setManager(cleanUp);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(Protocol.FIN, seqNo, ackNo);
	}

}
