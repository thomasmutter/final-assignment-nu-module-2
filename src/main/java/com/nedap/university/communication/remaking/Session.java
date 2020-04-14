package remaking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import otherCommands.PacketManager;
import queues.DatagramReceiver;
import queues.DatagramSender;
import time.TimeKeeper;

public class Session {

	private PacketManager manager;
	private DatagramSender sender;
	private DatagramReceiver receiver;

	public Session() throws SocketException {
		initialize();
	}

	protected void initialize() throws SocketException {
		DatagramSocket socket = new DatagramSocket();
		TimeKeeper keeper = new TimeKeeper(this);
		receiver = new DatagramReceiver(socket, this, keeper);
		sender = new DatagramSender(socket, keeper);
		new Thread(receiver).start();
		new Thread(sender).start();
	}

	public void setUpContact(DatagramPacket datagram) {
		sender.setContactInformation(datagram.getPort(), datagram.getAddress());
	}

	public void addToSendQueue(byte[] packet) {
		DatagramPacket datagram = sender.wrapPacketInUDP(packet);
		sender.addDatagramToQueue(datagram);
	}

	public void setManager(PacketManager managerArg) {
		manager = managerArg;
	}

	public void giveDatagramToManager(DatagramPacket datagram) {
		byte[] data = stripBufferRemainder(datagram);
		manager.processIncomingData(data);
	}

	public void shutdown() {
		sender.close();
	}

	private byte[] stripBufferRemainder(DatagramPacket datagram) {
		byte[] buffer = datagram.getData();
		byte[] data = new byte[datagram.getLength()];
		for (int i = 0; i < data.length; i++) {
			data[i] = buffer[i];
		}
		return data;
	}

}
