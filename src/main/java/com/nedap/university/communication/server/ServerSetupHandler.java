package server;

import header.HeaderConstructor;
import header.HeaderParser;
import sessionStates.Session;
import sessionStates.SetupManager;

public class ServerSetupHandler implements SetupManager {

	private HeaderConstructor headerConstructor;
	private HeaderParser headerParser;

	public ServerSetupHandler() {
		headerConstructor = new HeaderConstructor();
		headerParser = new HeaderParser();
	}

	@Override
	public void setup(String command, Session session) {
		switch (command.toLowerCase()) {
		case "dl":
			session.setSessionState(session.getUploading());
			break;
		case "ul":
			session.setSessionState(session.getDownloading());
			break;
		default:
			session.setSessionState(session.getFinalizing());
			break;
		}
	}

	@Override
	public byte[] composeData(byte[] buffer) {

		byte[] dataToSend = reduceBufferToRelevantData(buffer);
		byte[] headerToSend = headerConstructor.constructHeader(dataToSend.length);

		byte[] datagram = new byte[headerToSend.length + dataToSend.length];

		System.arraycopy(headerToSend, 0, datagram, 0, headerToSend.length);
		System.arraycopy(dataToSend, 0, datagram, headerToSend.length, dataToSend.length);

		return datagram;
	}

	private byte[] newHeaderFromOldHeader(byte[] oldHeader) {

	}

	private byte[] reduceBufferToRelevantData(byte[] buffer) {
		byte[] incomingHeader = headerParser.getHeader(buffer);
		int dataSize = headerParser.getWindowSize(incomingHeader);
		byte[] dataToSend = new byte[dataSize];

		System.out.println(dataSize);
		for (int i = 0; i < dataSize; i++) {
			dataToSend[i] = buffer[i + HeaderConstructor.HEADERLENGTH];
		}
		String s = new String(dataToSend);
		System.out.println(s);
		return dataToSend;
	}

}
