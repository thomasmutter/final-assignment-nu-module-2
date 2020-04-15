package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import communicationProtocols.Protocol;
import remaking.Session;

public class Client {

	private DatagramSocket socket;
	private Map<String, Session> sessionMap;

	private InetAddress serverAddress;

	public Client() throws SocketException {
		socket = new DatagramSocket();
		sessionMap = new HashMap<>();
	}

	public void addSessionToMap(String fileName, Session session) {
		sessionMap.put(fileName, session);
	}

	public void pauseSession(String fileName) {
		Session sessionToPause = sessionMap.get(fileName);
		sessionToPause.pause();
	}

	public void resumeSession(String fileName) {
		Session sessionToResume = sessionMap.get(fileName);
		sessionToResume.resume();
	}

	private void lookForServer() throws IOException {
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
		serverAddress = signOfLife.getAddress();
		InputListener listener = new InputListener(this);
		System.out.println("_-----------STARTING NEW LISTNER-----------");
		new Thread(listener).start();
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

	public void startSession(String command) throws SocketException {
//		String command = tui.getCommand();

		Session session = null;
		session = new Session();

		InputInterpreter input = new InputInterpreter(command, this);
		byte[] inputDatagram = input.getDatagramFromInput();
		session.setManager(input.getPacketManagerFromInput(session));
		System.out.println("Address: " + serverAddress);
		session.setUpContact(serverAddress, Protocol.PORT);
		session.addToSendQueue(inputDatagram);
		System.out.println("Session has been started up");

	}

	public static void main(String[] args) {

		try {
			Client client = new Client();
			client.lookForServer();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
