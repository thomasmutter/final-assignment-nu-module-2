package sessionStates;

public class Uploading implements SessionState {

	private Session session;

	public Uploading(Session inputSession) {
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

	@Override
	public byte[] headerToSend(byte[] oldHeader) {
		// TODO Auto-generated method stub
		return null;
	}

}
