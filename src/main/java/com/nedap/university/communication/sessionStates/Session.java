package sessionStates;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Session {

	private SessionState downloading, uploading, finalizing, initializing;
	private SessionState sessionState;
	private String fileName;
	private int port;
	private DatagramSocket socket;

	public Session(String inputFile, int servicePort, String command) {
		statesSetup(command);
		fileName = inputFile;
		port = servicePort;

		sessionState = initializing;
	}

	public void setSessionState(SessionState state) {
		sessionState = state;
	}

	public void initiateSession(InetAddress address) {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] datagram = sessionState.composeDatagram();
		DatagramPacket packet = new DatagramPacket(datagram, datagram.length, address, port);
		sendDatagram(packet);
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

	public String getFileName() {
		return fileName;
	}

	private void statesSetup(String command) {
		downloading = new Downloading(this);
		uploading = new Uploading(this);
		finalizing = new Finalizing(this);
		initializing = new Initializing(this, command);
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
