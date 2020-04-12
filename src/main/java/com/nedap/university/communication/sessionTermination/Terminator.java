package sessionTermination;

public interface Terminator {

	void terminateSession(int seqNo, int ackNo);

}
