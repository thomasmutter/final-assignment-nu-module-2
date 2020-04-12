package automaticRepeatRequest;

public abstract class StopAndWait implements ArqAlgorithm {

	protected int filePointer;

	@Override
	public boolean datagramInWindow(int incomingSeqNo, int payloadSize) {
		return incomingSeqNo - payloadSize == filePointer;
	}

	@Override
	public abstract void moveDatagramWindow(int seqNo, int payloadSize);

}
