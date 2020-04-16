package queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

import communicationProtocols.Protocol;
import header.HeaderConstructor;
import header.HeaderParser;
import remaking.Session;
import time.TimeKeeper;

public class DatagramReceiver implements Runnable {

	private DatagramSocket socket;
	private TimeKeeper keeper;
	private Session session;
	private HeaderParser parser;

	public DatagramReceiver(DatagramSocket socketArg, Session sessionArg, TimeKeeper keeperArg) {
		socket = socketArg;
		session = sessionArg;
		keeper = keeperArg;
		parser = new HeaderParser();
	}

	@Override
	public void run() {
		try {
			DatagramPacket firstPacket = receiveDatagram();
			keeper.processIncomingAck(firstPacket.getData());
			session.setUpContact(firstPacket.getAddress(), firstPacket.getPort());
			while (!socket.isClosed()) {
				DatagramPacket receivedPacket = receiveDatagram();
				Random random = new Random();
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				if (random.nextInt(100) < 100) {
//					printInformation(receivedPacket);
					keeper.processIncomingAck(receivedPacket.getData());
					session.giveDatagramToManager(receivedPacket);
				} else {
					// System.out.println("----- PACKET LOSS ------");
//					try {
//						Thread.sleep(2000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
			}
		} catch (IOException e) {
			System.out.println("Reader closed");
		}
		System.out.println("Stopped");
	}

	private void printInformation(DatagramPacket receivedPacket) {
		if (parser.getCommand(receivedPacket.getData()) != HeaderConstructor.P
				|| parser.getCommand(receivedPacket.getData()) != HeaderConstructor.R) {
			System.out.println("Received packet with: " + parser.getSequenceNumber(receivedPacket.getData()));
			System.out.println("Received packet with: " + parser.getAcknowledgementNumber(receivedPacket.getData()));
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
