package remaking;

import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import managers.PacketManager;
import queues.DatagramReceiver;
import queues.DatagramSender;

public class SessionV2 {

	private PacketManager manager;
	private BlockingQueue<DatagramPacket> incomingQueue;
	private boolean sessionOngoing;
	private DatagramSender sender;

	public SessionV2() {
		incomingQueue = new LinkedBlockingQueue<>();
		sender = new DatagramSender();
		initialize();
	}

	private void initialize() {
		new Thread(new DatagramReceiver()).start();
		new Thread(sender).start();
	}

	public void startUp() {
		sessionOngoing = true;

		DatagramPacket incomingPacket = readDatagram();
		sender.setContactInformation(incomingPacket.getPort(), incomingPacket.getAddress());
		giveDatagramToManager(incomingPacket);
		start();
	}

	public void addToSendQueue(byte[] packet) {
		System.out.println("Packet added to queue");
		DatagramPacket datagram = sender.wrapPacketInUDP(packet);
		sender.addDatagramToQueue(datagram);
	}

	public void addToReadQueue(DatagramPacket datagram) {
		System.out.println("Adding packet to reading queue");
		incomingQueue.offer(datagram);
	}

	public void setManager(PacketManager managerArg) {
		manager = managerArg;
	}

	public void finalizeSession() {
		System.out.println("Finalization steps requested by packet manager");
	}

	private DatagramPacket readDatagram() {
		DatagramPacket incomingDatagram = null;
		try {
			incomingDatagram = incomingQueue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return incomingDatagram;
	}

	private void giveDatagramToManager(DatagramPacket datagram) {
//		DatagramPacket datagram = readDatagram();
		byte[] data = datagram.getData();
		manager.processIncomingData(data);
		System.out.println("Packet given to manager for processing");
	}

	private void start() {
		System.out.println("Session started");
		while (sessionOngoing) {
			giveDatagramToManager(readDatagram());
		}
	}

}
