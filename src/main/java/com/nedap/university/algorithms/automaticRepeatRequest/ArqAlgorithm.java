package automaticRepeatRequest;

public interface ArqAlgorithm {

	boolean datagramInWindow(byte[] datagram);

	void handleIncomingDatagram(byte[] datagram);

}
