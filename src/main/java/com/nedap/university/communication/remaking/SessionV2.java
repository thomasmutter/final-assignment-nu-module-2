package remaking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import header.HeaderConstructor;
import managers.CleanUpManager;
import managers.PacketManager;
import queues.DatagramReceiver;
import queues.DatagramSender;

public class SessionV2 {

	private PacketManager manager;
	private DatagramSender sender;
	private DatagramReceiver receiver;

	public SessionV2() throws SocketException {
		initialize();
	}

	private void initialize() throws SocketException {
		DatagramSocket socket = new DatagramSocket();
		receiver = new DatagramReceiver(socket, this);
		sender = new DatagramSender(socket);
		new Thread(receiver).start();
		new Thread(sender).start();
	}

	public void startUp(DatagramPacket datagram) {
		sender.setContactInformation(datagram.getPort(), datagram.getAddress());
		giveDatagramToManager(datagram);
	}

	public void addToSendQueue(byte[] packet) {
		DatagramPacket datagram = sender.wrapPacketInUDP(packet);
		sender.addDatagramToQueue(datagram);
	}

	public void setManager(PacketManager managerArg) {
		manager = managerArg;
	}

	public void finalizeSession() {
		System.out.println("Finalization steps requested by packet manager");
		manager = new CleanUpManager(this);
		((CleanUpManager) manager).sendFin(HeaderConstructor.FIN);
	}

	public void giveDatagramToManager(DatagramPacket datagram) {
		byte[] data = datagram.getData();
		manager.processIncomingData(data);
		System.out.println("Packet given to manager for processing");
	}

	public void shutdown() {
		sender.close();
	}

}
