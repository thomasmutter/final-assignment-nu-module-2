package sessionStates;

import java.net.DatagramPacket;

public class Uploading implements SessionState {

	private Session session;

	public Uploading(Session inputSession) {
		session = inputSession;
	}

	@Override
	public DatagramPacket composeDatagram() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleData() {
		// TODO Auto-generated method stub

	}

}
