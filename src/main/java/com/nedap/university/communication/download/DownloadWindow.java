package download;

public class DownloadWindow {

	private int lastByteReceived;

	public DownloadWindow() {
		lastByteReceived = 0;
	}

	public void moveDatagramWindow(int seqNo) {
		lastByteReceived = seqNo;
	}

	public int getLastByteReceived() {
		return lastByteReceived;
	}

	public boolean datagramInWindow(int offset, int payloadSize) {
//		int a = offset - lastByteReceived;
//		System.out.println("The incoming data differs with " + a + " bytes from the last received data");
		return offset - payloadSize == lastByteReceived;
	}

}
