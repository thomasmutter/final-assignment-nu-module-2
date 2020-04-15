package download;

import java.util.Arrays;

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
	private HeaderConstructor constructor;

	private Session session;
	private String path;
	private byte[] fileAsBytes = new byte[0];

	public DownloadManager(Session sessionArg, String pathArg) {
		session = sessionArg;
		path = pathArg;
		constructor = new HeaderConstructor();
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
		byte flags = HeaderConstructor.DL;
		byte status = statusArg;

		int checksum = 0;
		int windowSize = payloadSize;
		return constructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

	private void finalizeFileTransfer() {
		ConversionHandler converter = new ConversionHandler();
		System.out.println(fileAsBytes.length);
		converter.writeBytesToFile(fileAsBytes, path);
	}

	public void setManagerState(ManagerState state) {
		managerState = state;
	}

	public void shutdownSession(int seqNo, int ackNo) {
		finalizeFileTransfer();
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new ReceiverTermination(cleanUp);
		session.setManager(cleanUp);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(HeaderConstructor.FIN, seqNo, ackNo);
	}

}
