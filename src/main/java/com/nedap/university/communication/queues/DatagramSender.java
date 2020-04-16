package queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import communicationProtocols.Protocol;
import header.HeaderParser;
import time.TimeKeeper;
import transferInformation.Metrics;

public class DatagramSender {

	private InetAddress address;
	private int port;
	private DatagramSocket socket;
	private TimeKeeper keeper;
	private Metrics metrics;

	public DatagramSender(DatagramSocket socketArg, TimeKeeper keeperArg) {
		socket = socketArg;
		keeper = keeperArg;
		metrics = new Metrics();
	}

	public void sendPacket(byte[] packet) {
		try {
			DatagramPacket datagram = new DatagramPacket(packet, packet.length, address, port);
//			printInformation(datagram);
			socket.send(datagram);
			metrics.updateMetrics(HeaderParser.getSequenceNumber(packet), packet.length, keeper.getRttFromTimer());
			keeper.setRetransmissionTimer(packet);
		} catch (IOException e) {
			System.out.println("Sender closed");
		}
	}

	private void printInformation(DatagramPacket receivedPacket) {
		if (HeaderParser.getCommand(receivedPacket.getData()) != Protocol.P
				|| HeaderParser.getCommand(receivedPacket.getData()) != Protocol.R) {
			System.out.println("Sending packet with seq: " + HeaderParser.getSequenceNumber(receivedPacket.getData()));
			System.out.println(
					"Sending packet with ack: " + HeaderParser.getAcknowledgementNumber(receivedPacket.getData()));
			System.out.println("This packet has windowSize " + HeaderParser.getOffset(receivedPacket.getData()));
			System.out.println("");
		}
	}

	public void setContactInformation(int portArg, InetAddress addressArg) {
		port = portArg;
		address = addressArg;
	}

	public Metrics getMetrics() {
		return metrics;
	}

	public void toPauseTimer(boolean toPause) {
		keeper.pauseRetransmissionTimer(toPause);
	}

	public void close() {
		System.out.println(metrics.toString());
		keeper.closeTimer();
		System.out.println("Timer closed");
		socket.close();
		System.out.println("Sender closed too");
	}

}
