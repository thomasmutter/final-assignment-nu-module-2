package automaticRepeatRequest;

public interface ArqAlgorithm {

	boolean datagramInWindow(int incomingSeqNo, int payloadSize);

	void moveDatagramWindow(int seqNo, int payloadSize);

	int getAckNo();

	int getWindowSize();

}
