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
		System.out.println("reached1");
		byte[] outgoingDatagram = setup.setupSession(session, incomingDatagram);
		return outgoingDatagram;
	}

	@Override
	public void handleData() {

	}

	public void setSetupManager(SetupManager setupArg) {
		setup = setupArg;
	}

	@Override
	public byte[] headerToSend(byte[] oldHeader) {
		// TODO Auto-generated method stub
		return null;
	}

}
