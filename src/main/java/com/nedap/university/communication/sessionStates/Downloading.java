package sessionStates;

public class Downloading implements SessionState {

	private Session session;

	public Downloading(Session inputSession) {
		session = inputSession;
	}

	@Override
	public byte[] composeDatagram(byte[] incomingDatagram) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleData() {
		// TODO Auto-generated method stub

	}

}
