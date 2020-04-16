package queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

import communicationProtocols.Protocol;
import header.HeaderParser;
import session.Session;
import time.TimeKeeper;

public class DatagramReceiver implements Runnable {

	private DatagramSocket socket;
	private TimeKeeper keeper;
	private Session session;
	private Random random;

	public DatagramReceiver(DatagramSocket socketArg, Session sessionArg, TimeKeeper keeperArg) {
		socket = socketArg;
		session = sessionArg;
		keeper = keeperArg;
		random = new Random();
	}

	@Override
	public void run() {
		try {
			DatagramPacket firstPacket = receiveDatagram();
			session.setUpContact(firstPacket.getAddress(), firstPacket.getPort());
			System.out.println("-- SET UP NEW CONTACT INFO AT " + firstPacket.getPort());
			keeper.processIncomingAck(firstPacket.getData());
			session.giveDatagramToManager(firstPacket);
//			printInformation(firstPacket);
			while (!socket.isClosed()) {
				DatagramPacket receivedPacket = receiveDatagram();

				if (random.nextInt(100) < 100) {
//					printInformation(receivedPacket);
					keeper.processIncomingAck(receivedPacket.getData());
					session.giveDatagramToManager(receivedPacket);
				}
//				else {
//					System.out.println("----- PACKET LOSS ------");
//				}
			}
		} catch (IOException e) {
			System.out.println("Reader closed");
		}
		System.out.println("Stopped");
	}

	private void printInformation(DatagramPacket receivedPacket) {
		if (HeaderParser.getCommand(receivedPacket.getData()) != Protocol.P
				|| HeaderParser.getCommand(receivedPacket.getData()) != Protocol.R) {
			System.out.println("Received packet with: " + HeaderParser.getSequenceNumber(receivedPacket.getData()));
			System.out.println(
					"Received packet with: " + HeaderParser.getAcknowledgementNumber(receivedPacket.getData()));
		}
	}

	private DatagramPacket receiveDatagram() throws IOException {
		byte[] buffer = new byte[Protocol.PACKETSIZE + 2];
		DatagramPacket incomingDatagram = new DatagramPacket(buffer, buffer.length);
		socket.receive(incomingDatagram);
//		System.out.println("Receiving succesfull");

		return incomingDatagram;
	}

	public void close() {
		socket.close();
	}
}
