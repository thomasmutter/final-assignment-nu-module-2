package queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import remaking.Session;
import time.TimeKeeper;

public class DatagramReceiver implements Runnable {

	private DatagramSocket socket;
	private TimeKeeper keeper;
	private Session session;

	public DatagramReceiver(DatagramSocket socketArg, Session sessionArg, TimeKeeper keeperArg) {
		socket = socketArg;
		session = sessionArg;
		keeper = keeperArg;
	}

	@Override
	public void run() {
		try {
			session.startUp(receiveDatagram());
			while (!socket.isClosed()) {
				DatagramPacket receivedPacket = receiveDatagram();
				session.giveDatagramToManager(receivedPacket);
			}
		} catch (IOException e) {
			System.out.println("Reader closed");
		}
		System.out.println("Stopped");
	}

	private DatagramPacket receiveDatagram() throws IOException {
		byte[] buffer = new byte[512];
		DatagramPacket incomingDatagram = new DatagramPacket(buffer, buffer.length);
		System.out.println("Blocking at receive");
		socket.receive(incomingDatagram);
		System.out.println("Receiving succesfull");
		keeper.processIncomingAck(incomingDatagram.getData());
		return incomingDatagram;
	}

	public void close() {
		socket.close();
	}
}
