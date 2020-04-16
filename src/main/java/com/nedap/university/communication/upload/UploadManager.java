package upload;

import java.io.FileNotFoundException;

import communicationProtocols.Protocol;
import fileConversion.ConversionHandler;
import header.HeaderConstructor;
import managerStates.ManagerState;
import otherCommands.CleanUpManager;
import otherCommands.PacketManager;
import session.Session;
import sessionTermination.SenderTermination;
import sessionTermination.Terminator;

public class UploadManager implements PacketManager {

	private Session session;
	private ManagerState state;

	private byte[] fileAsBytes;
	private int fileSize;

	public UploadManager(Session sessionArg, String pathArg) throws FileNotFoundException {
		session = sessionArg;
		state = new UploadInitialize(this);
		fileSize = fetchFileFromDisk(pathArg);

	}

	@Override
	public void processIncomingData(byte[] incomingDatagram) {
		state.translateIncomingHeader(incomingDatagram);
	}

	public void processOutgoingData(byte[] header, byte[] data) {
		byte[] datagram = new byte[header.length + data.length];
		System.arraycopy(header, 0, datagram, 0, header.length);
		System.arraycopy(data, 0, datagram, header.length, data.length);
		session.addToSendQueue(datagram);
	}

	public byte[] formHeader(byte statusArg, int seqNo, int ackNo, int offsetArg) {
		byte flags = Protocol.UL;
		byte status = statusArg;
		int offset = offsetArg;
		return HeaderConstructor.constructHeader(flags, status, seqNo, ackNo, offset);
	}

	public void setManagerState(ManagerState stateArg) {
		state = stateArg;
	}

	public int getFileSize() {
		return fileSize;
	}

	public int fetchFileFromDisk(String path) throws FileNotFoundException {
		ConversionHandler fileToByteConverter = new ConversionHandler();
		fileAsBytes = fileToByteConverter.readFileToBytes(path);
		return fileAsBytes.length;
	}

	public byte[] getSegmentFromFile(int payloadSize, int lastByteSent) {
		byte[] segment = new byte[payloadSize];
		for (int i = 0; i < payloadSize; i++) {
			segment[i] = fileAsBytes[lastByteSent + i];
		}
		return segment;
	}

	public void pauseSession(boolean toPause) {
		session.pauseSender(toPause);
	}

	public void shutdownSession(int seqNo, int ackNo) {
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new SenderTermination(cleanUp, session);
		session.setManager(cleanUp);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(Protocol.FIN, seqNo, ackNo);
	}
}
