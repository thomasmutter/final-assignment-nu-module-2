package queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DatagramSender implements Runnable {

	private InetAddress address;
	private int port;
	private BlockingQueue<DatagramPacket> queue;
	private DatagramSocket socket;

	public DatagramSender(DatagramSocket socketArg) {
		port = 8888;
		try {
			address = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		queue = new LinkedBlockingQueue<>();
		socket = socketArg;
	}

	@Override
	public void run() {
		while (!socket.isClosed()) {
			try {
				DatagramPacket packetToSend = queue.take();
				socket.send(packetToSend);
			} catch (IOException e) {
				System.out.println("Sender closed");
				// e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
