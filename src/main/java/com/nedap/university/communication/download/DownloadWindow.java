package download;

public class DownloadWindow {

	private int lastByteAcked;

	public DownloadWindow(int offset) {
		lastByteAcked = offset;
	}

	public void moveDatagramWindow(int seqNo) {
		lastByteAcked = seqNo;
	}

	public int getAckNo() {
		return lastByteAcked;
	}

	public boolean datagramInWindow(int incomingSeqNo, int payloadSize) {
		return incomingSeqNo - payloadSize == lastByteAcked;
	}

}
