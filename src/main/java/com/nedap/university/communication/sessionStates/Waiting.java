package sessionStates;

import header.HeaderConstructor;

public class Waiting implements SessionState {

	private Session session;

	public Waiting(Session session) {
		this.session = session;
	}

	@Override
	public byte[] composeDatagram(byte[] incomingDatagram) {
		printData(incomingDatagram);
		return null;
	}

	private void printData(byte[] datagram) {
		byte[] data = new byte[datagram.length - HeaderConstructor.HEADERLENGTH];
		int j = 0;
		for (int i = HeaderConstructor.HEADERLENGTH; i < datagram.length; i++) {
			data[j] = datagram[i];
			j++;
		}
		System.out.println(new String(data));
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
