package sessionStates;

public interface SessionState {

	byte[] composeDatagram();

	void handleData();

}
