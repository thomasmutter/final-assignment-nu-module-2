package queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import header.HeaderConstructor;
import header.HeaderParser;
import time.TimeKeeper;

public class DatagramSender implements Runnable {

	private InetAddress address;
	private int port;
	private BlockingQueue<DatagramPacket> queue;
	private DatagramSocket socket;
	private TimeKeeper keeper;

	private HeaderParser parser;

	public DatagramSender(DatagramSocket socketArg, TimeKeeper keeperArg) {
//		port = 8888;
//		try {
//			address = InetAddress.getByName("127.0.0.1");
//		} catch (UnknownHostException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		queue = new LinkedBlockingQueue<>();
		socket = socketArg;
		keeper = keeperArg;
		parser = new HeaderParser();
	}

	@Override
	public void run() {
		while (!socket.isClosed()) {
			try {
				DatagramPacket packetToSend = queue.take();
				socket.send(packetToSend);
//				printInformation(packetToSend);
				if (parser.getStatus(packetToSend.getData()) == (HeaderConstructor.P ^ HeaderConstructor.ACK))
					System.out.println("sending packet with status: " + parser.getStatus(packetToSend.getData()));
				keeper.setRetransmissionTimer(packetToSend.getData());
			} catch (IOException e) {
				System.out.println("Sender closed");
				// e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void printInformation(DatagramPacket receivedPacket) {
		if (parser.getCommand(receivedPacket.getData()) != HeaderConstructor.P
				|| parser.getCommand(receivedPacket.getData()) != HeaderConstructor.R) {
			System.out.println("Received packet with: " + parser.getSequenceNumber(receivedPacket.getData()));
			System.out.println("Received packet with: " + parser.getAcknowledgementNumber(receivedPacket.getData()));
			System.out.println("This packet has windowSize " + parser.getWindowSize(receivedPacket.getData()));
			System.out.println("");
		}
	}

	public DatagramPacket wrapPacketInUDP(byte[] packet) {
		DatagramPacket datagram = new DatagramPacket(packet, packet.length, address, port);
		return datagram;
	}

	public void addDatagramToQueue(DatagramPacket datagram) {
		queue.offer(datagram);
	}

	public void setContactInformation(int portArg, InetAddress addressArg) {
		port = portArg;
		address = addressArg;
	}

	public void close() {
		socket.close();
		queue.offer(new DatagramPacket(new byte[1], 1));
	}

}
