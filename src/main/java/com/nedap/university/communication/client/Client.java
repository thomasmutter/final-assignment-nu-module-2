package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import clientTUI.ClientTUI;
import communicationProtocols.Protocol;
import remaking.Session;

public class Client {

	private ClientTUI tui;
	private DatagramSocket socket;

	public Client(ClientTUI tuiArg) throws SocketException {
		tui = tuiArg;
		socket = new DatagramSocket();
	}

	private DatagramPacket lookForServer() throws IOException {
		socketConfig();
		sendBroadcast();
		byte[] buffer = new byte[2];
		DatagramPacket signOfLife = new DatagramPacket(buffer, buffer.length);
		while (signOfLife.getLength() > Protocol.SIGNOFLIFESIZE) {
			try {
				System.out.println("RECEIVING");
				socket.receive(signOfLife);
			} catch (SocketTimeoutException e) {
				System.out.println("The broadcast message has timed out, sending a new one.");
				sendBroadcast();
				System.out.println(signOfLife.getLength());
			}
		}
		return signOfLife;
	}

	private void socketConfig() throws IOException {
		socket.setBroadcast(true);
		socket.setSoTimeout(3000);
	}

	private void sendBroadcast() throws IOException {
		byte[] contactRequest = new byte[1];
		InetAddress address = InetAddress.getByName(Protocol.BROADCAST);

		System.out.println("Looking for server");
		DatagramPacket contactDatagram = new DatagramPacket(contactRequest, contactRequest.length, address,
				Protocol.PORT);
		socket.send(contactDatagram);

	}

	private void start(DatagramPacket signOfLife) throws SocketException {
		String command = tui.getCommand();

		Session session = null;
		session = new Session();

		InputInterpreter input = new InputInterpreter(command);
		byte[] inputDatagram = input.getDatagramFromInput();
		session.setManager(input.getPacketManagerFromInput(session));
		System.out.println("Address: " + signOfLife.getAddress());
		session.setUpContact(signOfLife);
		session.addToSendQueue(inputDatagram);

	}

	public static void main(String[] args) {
		ClientTUI tui = new ClientTUI();
		Client client;
		try {
			client = new Client(tui);
			DatagramPacket signOfLife = client.lookForServer();
			client.start(signOfLife);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
