package sessionStates;

public class Removing implements SessionState {

	private Session session;
	private String fileName;

	public Removing(Session session) {
		this.session = session;
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

	public void setFileName(String fileNameArg) {
		fileName = fileNameArg;
	}

	@Override
	public byte[] headerToSend(byte[] oldHeader) {
		// TODO Auto-generated method stub
		return null;
	}

}
