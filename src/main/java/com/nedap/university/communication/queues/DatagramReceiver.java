package queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import remaking.SessionV2;

public class DatagramReceiver implements Runnable {

	private DatagramSocket socket;
	private SessionV2 session;
//	private InetAddress address;
//	private int port;

	public DatagramReceiver(DatagramSocket socketArg, SessionV2 sessionArg) {
		socket = socketArg;
		session = sessionArg;
	}

	@Override
	public void run() {
		session.startUp(receiveDatagram());
		while (!socket.isClosed()) {
			session.giveDatagramToManager(receiveDatagram());
		}
	}

//	public InetAddress getAddress() {
//		return address;
//	}
//
//	public int getPort() {
//		return port;
//	}

	private DatagramPacket receiveDatagram() {
		byte[] buffer = new byte[512];
		DatagramPacket incomingDatagram = new DatagramPacket(buffer, buffer.length);
		try {
			System.out.println("Waiting for the next packet");
			socket.receive(incomingDatagram);
			System.out.println("Datagram received");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return incomingDatagram;
	}

	public void close() {
		socket.close();
	}
}
