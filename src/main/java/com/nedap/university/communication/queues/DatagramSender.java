package queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import time.TimeKeeper;

public class DatagramSender {

	private InetAddress address;
	private int port;
	private DatagramSocket socket;
	private TimeKeeper keeper;

	public DatagramSender(DatagramSocket socketArg, TimeKeeper keeperArg) {
		socket = socketArg;
		keeper = keeperArg;
	}

	public void sendPacket(byte[] packet) {
		try {
			DatagramPacket datagram = new DatagramPacket(packet, packet.length, address, port);
			socket.send(datagram);
			keeper.setRetransmissionTimer(packet);
		} catch (IOException e) {
			System.out.println("Sender closed");
		}
	}

//	private void printInformation(DatagramPacket receivedPacket) {
//		if (HeaderParser.getCommand(receivedPacket.getData()) != HeaderConstructor.P
//				|| HeaderParser.getCommand(receivedPacket.getData()) != HeaderConstructor.R) {
//			System.out.println("Received packet with: " + HeaderParser.getSequenceNumber(receivedPacket.getData()));
//			System.out.println("Received packet with: " + HeaderParser.getAcknowledgementNumber(receivedPacket.getData()));
//			System.out.println("This packet has windowSize " + HeaderParser.getWindowSize(receivedPacket.getData()));
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
