package queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import header.HeaderParser;
import time.TimeKeeper;

public class DatagramSender {

	private InetAddress address;
	private int port;
	private DatagramSocket socket;
	private TimeKeeper keeper;

	private HeaderParser parser;

	public DatagramSender(DatagramSocket socketArg, TimeKeeper keeperArg) {
		socket = socketArg;
		keeper = keeperArg;
		parser = new HeaderParser();
	}

	public void sendPacket(byte[] packet) {
		try {
			DatagramPacket datagram = new DatagramPacket(packet, packet.length, address, port);
			socket.send(datagram);
		} catch (IOException e) {
			System.out.println("Sender closed");
		}
	}

//	private void printInformation(DatagramPacket receivedPacket) {
//		if (parser.getCommand(receivedPacket.getData()) != HeaderConstructor.P
//				|| parser.getCommand(receivedPacket.getData()) != HeaderConstructor.R) {
//			System.out.println("Received packet with: " + parser.getSequenceNumber(receivedPacket.getData()));
//			System.out.println("Received packet with: " + parser.getAcknowledgementNumber(receivedPacket.getData()));
//			System.out.println("This packet has windowSize " + parser.getWindowSize(receivedPacket.getData()));
//			System.out.println("");
//		}
//	}

	public void setContactInformation(int portArg, InetAddress addressArg) {
		port = portArg;
		address = addressArg;
	}

	public void toPauseTimer(boolean toPause) {
		keeper.pauseRetransmissionTimer(toPause);
	}

	public void close() {
		socket.close();
	}

}
