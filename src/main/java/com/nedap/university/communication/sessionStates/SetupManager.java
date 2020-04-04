package sessionStates;

public interface SetupManager {

	void setup(String command, Session session);

	byte[] composeData(byte[] data);

}
