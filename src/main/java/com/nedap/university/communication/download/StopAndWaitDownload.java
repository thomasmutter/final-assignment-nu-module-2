package download;

import automaticRepeatRequest.StopAndWait;

public class StopAndWaitDownload extends StopAndWait {

	private int lastByteAcked;

	@Override
	public void moveDatagramWindow(int seqNo, int payload) {
		lastByteAcked = seqNo;
	}

	@Override
	public int getAckNo() {
		return lastByteAcked;
	}

}
