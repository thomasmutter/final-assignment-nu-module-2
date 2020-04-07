package sessionStates;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import header.HeaderConstructor;

public class Session {

	private SessionState downloading, uploading, finalizing, initializing, removing, listing, replacing, waiting;
	private SessionState sessionState;

	private int port;
	private DatagramSocket socket;
	private InetAddress address;

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
		this.address = address;
		byte[] datagram = sessionState.composeDatagram(inputData);
		DatagramPacket packet = new DatagramPacket(datagram, datagram.length, address, port);
		sendDatagram(packet);
		startSession();
	}

	public void startSession() {
		while (true) {
			byte[] buffer = new byte[HeaderConstructor.HEADERLENGTH + 512];
			DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(incomingPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] datagram = sessionState.composeDatagram(incomingPacket.getData());
			DatagramPacket packet = new DatagramPacket(datagram, datagram.length, address, port);
			sendDatagram(packet);
		}
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
		removing = new Removing(this);
		replacing = new Replacing(this);
		listing = new Listing(this);
		waiting = new Waiting(this);
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

	public SessionState getRemoving() {
		// TODO Auto-generated method stub
		return null;
	}

	public SessionState getReplacing() {
		// TODO Auto-generated method stub
		return null;
	}

	public SessionState getListing() {
		return listing;
	}

	public SessionState getWaiting() {
		return waiting;
	}

}
