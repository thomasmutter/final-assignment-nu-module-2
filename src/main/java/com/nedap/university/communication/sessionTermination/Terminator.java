package sessionTermination;

public interface Terminator {

	void terminateSession(byte status, int seqNo, int ackNo);

}
