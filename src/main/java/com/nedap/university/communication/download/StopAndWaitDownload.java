package download;

import automaticRepeatRequest.StopAndWait;

public class StopAndWaitDownload extends StopAndWait {

	private int lastByteAcked;

	private static final int WINDOWSIZE = 1;

	@Override
	public void moveDatagramWindow(int seqNo, int payload) {
		lastByteAcked = seqNo;
	}

	@Override
	public int getAckNo() {
		return lastByteAcked;
	}

	@Override
	public int getWindowSize() {
		return WINDOWSIZE;
	}

}
