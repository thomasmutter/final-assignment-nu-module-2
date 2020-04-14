package upload;

public class UploadWindow {

	private int lastByteAcked;
	private int nextSeqExpected;

	private int ackOffset;

	private static final int WINDOWSIZE = 1;

	public UploadWindow(int ackOffsetArg) {
		ackOffset = ackOffsetArg;
		nextSeqExpected = 0;
	}

	public void updateWindow(int receivedSeqNo, int receivedAckNo) {
		lastByteAcked = receivedAckNo;
		nextSeqExpected++;
	}

	public boolean isDataInWindow(int receivedSeqNo) {
//		int ok = receivedSeqNo - ackOffset;
//		System.out.println("Received seqNo - offset " + ok);
//		System.out.println("next seq exp " + nextSeqExpected);
		return receivedSeqNo - ackOffset == nextSeqExpected;
	}

	public int getNumberOfDatagramsToSend() {
		return WINDOWSIZE;
	}

	public int getLastByteAcked() {
		return lastByteAcked;
	}

}
