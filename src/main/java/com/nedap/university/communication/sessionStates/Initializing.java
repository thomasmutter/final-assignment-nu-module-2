package sessionStates;

import header.HeaderConstructor;

public class Initializing implements SessionState {

	private Session session;
	private String command;

	public Initializing(Session inputSession, String commandArg) {
		session = inputSession;
		command = commandArg;
	}

	@Override
	public byte[] composeDatagram() {
		String dataString = session.getFileName() + "\n" + command;
		byte[] header = new HeaderConstructor().constructHeader(0);
		byte[] data = dataString.getBytes();
		byte[] datagram = new byte[HeaderConstructor.HEADERLENGTH + data.length];

		System.arraycopy(header, 0, datagram, 0, HeaderConstructor.HEADERLENGTH);
		System.arraycopy(data, 0, datagram, HeaderConstructor.HEADERLENGTH, data.length);

		session.setSessionState(parseStringToState(command));
		return datagram;
	}

	@Override
	public void handleData() {

	}

	private SessionState parseStringToState(String command) {
		if (command.contentEquals("dl")) {
			return session.getDownloading();
		} else if (command.contentEquals("ul")) {
			return session.getUploading();
		} else {
			return session.getFinalizing();
		}
	}

}
