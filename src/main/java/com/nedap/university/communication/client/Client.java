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
import transferInformation.Metrics;

public class Client {

	private DatagramSocket socket;
	private Map<String, Session> sessionMap;

	private InetAddress serverAddress;

	public Client() throws SocketException {
		socket = new DatagramSocket();
		sessionMap = new HashMap<>();
	}

	public void addSessionToMap(String command, Session session) {
		String[] split = command.split("\\s+");
		if (split.length > 1) {
			System.out.println("SESSION ADDED TO MAP");
			sessionMap.put(split[1], session);
		}
	}

	public void pauseSession(String fileName) {
		Session sessionToPause = sessionMap.get(fileName);
		sessionToPause.pause();
	}

	public void resumeSession(String fileName) {
		Session sessionToResume = sessionMap.get(fileName);
		sessionToResume.resume();
	}

	public Metrics getMetricsFromSession(String fileName) {
		Session session = sessionMap.get(fileName);
		return session.getMetricsFromSender();
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
		System.out.println("_-----------STARTING NEW LISTENER-----------");
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

		Session session = new Session();

		InputInterpreter input = new InputInterpreter(command, this);
		addSessionToMap(command, session);
		byte[] inputDatagram = input.setUpSession(session);
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
