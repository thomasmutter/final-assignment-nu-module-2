package sessionStates;

import header.HeaderConstructor;

public class Initializing implements SessionState {

	private Session session;
	private SetupManager setup;
	private HeaderConstructor headerConstructor;

	public Initializing(Session inputSession) {
		session = inputSession;
		headerConstructor = new HeaderConstructor();
	}

	@Override
	public byte[] composeDatagram(byte[] incomingDatagram) {
		return setup.composeData(incomingDatagram);
	}

	@Override
	public void handleData() {

	}

	public void setSetupManager(SetupManager setupArg) {
		setup = setupArg;
	}

}
