package sessionStates;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Session {

	private SessionState downloading, uploading, finalizing, initializing;
	private SessionState sessionState;
	private int port;
	private DatagramSocket socket;

	public Session(int servicePort) {
		statesSetup();
		port = servicePort;

		sessionState = initializing;
	}

	public void setSessionState(SessionState state) {
		sessionState = state;
	}

	public void initiateSession(InetAddress address, byte[] inputData) {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		byte[] datagram = sessionState.composeDatagram(inputData);
		DatagramPacket packet = new DatagramPacket(datagram, datagram.length, address, port);
		sendDatagram(packet);
	}

	public void startSession() {

	}

	private void sendDatagram(DatagramPacket datagram) {
		try {
			System.out.println("Sending datagram");
			socket.send(datagram);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void statesSetup() {
		downloading = new Downloading(this);
		uploading = new Uploading(this);
		finalizing = new Finalizing(this);
		initializing = new Initializing(this);
	}

	public SessionState getDownloading() {
		return downloading;
	}

	public SessionState getUploading() {
		return uploading;
	}

	public SessionState getFinalizing() {
		return finalizing;
	}

	public SessionState getInitializing() {
		return initializing;
	}

}
