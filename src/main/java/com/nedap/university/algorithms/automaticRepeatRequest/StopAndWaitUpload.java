package automaticRepeatRequest;

import header.HeaderConstructor;
import managers.UploadManager;

public class StopAndWaitUpload extends StopAndWait {

	private UploadManager manager;
	private int segmentSize;
	private int lastByteSent;

	public StopAndWaitUpload(UploadManager managerArg) {
		manager = managerArg;
		segmentSize = 512 - HeaderConstructor.HEADERLENGTH;
	}

	@Override
	public void handleIncomingDatagram(byte[] datagram) {
		int segmentLength = Math.min(segmentSize, manager.getFileLength() - lastByteSent);
		System.out.println("The segmentLength sent is " + segmentLength);
		byte[] header = formHeader(datagram, segmentLength);
		byte[] data = manager.getSegmentFromFile(segmentLength, lastByteSent);
		if (lastByteSent != manager.getFileLength()) {
			manager.processOutgoingData(header, data);
			lastByteSent += segmentLength;
		} else {
			manager.endUpload(datagram);
		}
	}

}
