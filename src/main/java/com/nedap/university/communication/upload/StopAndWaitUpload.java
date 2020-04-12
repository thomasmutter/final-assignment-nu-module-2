package upload;

import automaticRepeatRequest.StopAndWait;
import header.HeaderConstructor;

public class StopAndWaitUpload extends StopAndWait {

	private UploadManager manager;
	private int segmentSize;
	private int lastByteAcked;
	private int fileLength;

	public StopAndWaitUpload(UploadManager managerArg, int fileLengthArg) {
		manager = managerArg;
		segmentSize = 512 - HeaderConstructor.HEADERLENGTH;
		fileLength = fileLengthArg;
	}

	@Override
	public void moveDatagramWindow(byte[] datagram) {
		int segmentLength = Math.min(segmentSize, manager.getFileLength() - lastByteAcked);
		System.out.println("The segmentLength sent is " + segmentLength);
		byte[] header = formHeader(datagram, segmentLength);
		byte[] data = manager.getSegmentFromFile(segmentLength, lastByteAcked);
		if (lastByteAcked != manager.getFileLength()) {
			manager.processOutgoingData(header, data);
			lastByteAcked += segmentLength;
		} else {
			manager.endUpload(datagram);
		}
	}

	@Override
	public int getAckNo() {
		return lastByteAcked;
	}

	@Override
	public void moveDatagramWindow(int seqNo, int payloadSize) {
		lastByteAcked = seqNo;

	}

}
