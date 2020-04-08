package sessionStates;

public interface SessionState {

	byte[] composeDatagram(byte[] incomingDatagram);

	void handleData();

	byte[] headerToSend(byte[] oldHeader);

}
