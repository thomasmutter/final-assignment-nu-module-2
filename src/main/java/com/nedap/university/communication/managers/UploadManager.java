package managers;

import remaking.SessionV2;

public class UploadManager implements PacketManager {

	private SessionV2 session;

	public UploadManager(SessionV2 sessionArg) {
		session = sessionArg;
	}

	@Override
	public void processIncomingData(byte[] data) {
		// TODO Auto-generated method stub

	}

}
