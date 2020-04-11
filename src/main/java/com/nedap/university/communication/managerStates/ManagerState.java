package managerStates;

public interface ManagerState {

	void translateIncomingHeader(byte[] incomingDatagram);

	void nextState();

}
