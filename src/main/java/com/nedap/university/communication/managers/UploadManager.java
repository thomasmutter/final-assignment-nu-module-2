package managers;

import automaticRepeatRequest.ArqAlgorithm;
import automaticRepeatRequest.StopAndWaitUpload;
import fileConversion.ConversionHandler;
import remaking.Session;

public class UploadManager implements PacketManager {

	private Session session;
	private byte[] fileAsBytes;
	private ArqAlgorithm algorithm;

	public UploadManager(Session sessionArg, String pathArg) {
		session = sessionArg;
		initialize(pathArg);
		algorithm = new StopAndWaitUpload(this);
	}

	@Override
	public void processIncomingData(byte[] data) {
		algorithm.moveDatagramWindow(data);
	}

	public void endUpload(byte[] data) {
		session.finalizeSession(data);
	}

	public void processOutgoingData(byte[] header, byte[] data) {
		byte[] datagram = new byte[header.length + data.length];

		System.arraycopy(header, 0, datagram, 0, header.length);
		System.arraycopy(data, 0, datagram, header.length, data.length);

		session.addToSendQueue(datagram);
	}

	public byte[] getSegmentFromFile(int payloadSize, int lastByteSent) {
		byte[] segment = new byte[payloadSize];
		for (int i = 0; i < payloadSize; i++) {
			segment[i] = fileAsBytes[lastByteSent + i];
		}
		return segment;
	}

	public int getFileLength() {
		return fileAsBytes.length;
	}

	private void initialize(String path) {
		ConversionHandler fileToByteConverter = new ConversionHandler();
		fileAsBytes = fileToByteConverter.readFileToBytes(path);
	}

}
