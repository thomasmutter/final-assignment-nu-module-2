package download;

public class DownloadWindow {

	private int lastByteReceived;

	public DownloadWindow(int offset) {
		lastByteReceived = offset;
	}

	public void moveDatagramWindow(int seqNo) {
		lastByteReceived = seqNo;
	}

	public int getAckNo() {
		return lastByteReceived;
	}

	public boolean datagramInWindow(int incomingSeqNo, int payloadSize) {
		// int a = incomingSeqNo - lastByteReceived;
		// System.out.println("The incoming data differs with " + a + " bytes from the
		// last received data");
		return incomingSeqNo - payloadSize == lastByteReceived;
	}

}
