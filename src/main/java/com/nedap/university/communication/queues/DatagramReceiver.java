package queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import remaking.SessionV2;

public class DatagramReceiver implements Runnable {

	private DatagramSocket socket;
	private SessionV2 session;

	public DatagramReceiver(DatagramSocket socketArg, SessionV2 sessionArg) {
		socket = socketArg;
		session = sessionArg;
	}

	@Override
	public void run() {
		try {
			session.startUp(receiveDatagram());
			while (!socket.isClosed()) {
				DatagramPacket receivedPacket = receiveDatagram();
				System.out.println(receivedPacket);
				session.giveDatagramToManager(receivedPacket);
			}
		} catch (IOException e) {
			System.out.println("Reader closed");
		}
	}

	private DatagramPacket receiveDatagram() throws IOException {
		byte[] buffer = new byte[512];
		DatagramPacket incomingDatagram = new DatagramPacket(buffer, buffer.length);
		socket.receive(incomingDatagram);
		return incomingDatagram;
	}

	public void close() {
		socket.close();
	}
}
