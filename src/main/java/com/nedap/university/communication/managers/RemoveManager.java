package managers;

import remaking.SessionV2;

public class RemoveManager implements PacketManager {

	private SessionV2 session;

	public RemoveManager(SessionV2 sessionArg) {
		session = sessionArg;
	}

	@Override
	public void processIncomingData(byte[] data) {
		// TODO Auto-generated method stub

	}

}
