package sessionStates;

import java.net.DatagramPacket;

public class Finalizing implements SessionState {

	private Session session;

	public Finalizing(Session inputSession) {
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
