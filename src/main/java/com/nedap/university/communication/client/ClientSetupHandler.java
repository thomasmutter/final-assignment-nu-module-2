package client;

import header.HeaderConstructor;
import sessionStates.Session;
import sessionStates.SetupManager;

public class ClientSetupHandler implements SetupManager {

	private HeaderConstructor headerConstructor;

	public ClientSetupHandler() {
		headerConstructor = new HeaderConstructor();
	}

	@Override
	public void setup(String command, Session session) {
		switch (command.toLowerCase()) {
		case "dl":
			session.setSessionState(session.getDownloading());
			break;
		case "ul":
			session.setSessionState(session.getUploading());
			break;
		default:
			session.setSessionState(session.getFinalizing());
			break;
		}
	}

	@Override
	public byte[] composeData(byte[] data) {
		byte[] headerToSend = headerConstructor.constructHeader(data.length);
		byte[] dataToSend = data;
		byte[] datagram = new byte[headerToSend.length + dataToSend.length];

		System.arraycopy(headerToSend, 0, datagram, 0, headerToSend.length);
		System.arraycopy(dataToSend, 0, datagram, headerToSend.length, dataToSend.length);

		return datagram;
	}
}
