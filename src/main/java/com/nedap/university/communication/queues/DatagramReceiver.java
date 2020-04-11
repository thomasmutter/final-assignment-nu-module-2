package queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

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
			DatagramPacket firstPacket = receiveDatagram();
			keeper.processIncomingAck(firstPacket.getData());
			session.startUp(firstPacket);
			while (!socket.isClosed()) {
				DatagramPacket receivedPacket = receiveDatagram();
				Random random = new Random();
				if (random.nextInt(100) < 10) {
					keeper.processIncomingAck(receivedPacket.getData());
					session.giveDatagramToManager(receivedPacket);
				} else {
					System.out.println("Packet lost");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Reader closed");
		}
		System.out.println("Stopped");
	}

	private DatagramPacket receiveDatagram() throws IOException {
		byte[] buffer = new byte[512];
		DatagramPacket incomingDatagram = new DatagramPacket(buffer, buffer.length);
		socket.receive(incomingDatagram);
		System.out.println("Receiving succesfull");

		return incomingDatagram;
	}

	public void close() {
		socket.close();
	}
}
