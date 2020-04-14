package upload;

import fileConversion.ConversionHandler;
import header.HeaderConstructor;
import managerStates.ManagerState;
import otherCommands.CleanUpManager;
import otherCommands.PacketManager;
import remaking.Session;
import sessionTermination.SenderTermination;
import sessionTermination.Terminator;
import time.TimeKeeper;

public class UploadManager implements PacketManager {

	private Session session;
	private ManagerState state;
	private HeaderConstructor constructor;

	private byte[] fileAsBytes;
	public static final int SIZE = 512;

	public UploadManager(Session sessionArg, String pathArg) {
		session = sessionArg;
		constructor = new HeaderConstructor();
		state = new UploadInitialize(this, pathArg);
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

	public byte[] formHeader(int seqNo, int ackNo, int payloadSize) {
		byte flags = HeaderConstructor.UL;
		byte status = HeaderConstructor.ACK;

		int checksum = 0;
		int windowSize = payloadSize;
		return constructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

	public void setManagerState(ManagerState stateArg) {
		state = stateArg;
	}

	public int fetchFileFromDisk(String path) {
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

	public void shutdownSession(int seqNo, int ackNo) {
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new SenderTermination(cleanUp, new TimeKeeper(session));
		session.setManager(cleanUp);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(seqNo, ackNo);
	}
}
