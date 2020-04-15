package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import communicationProtocols.Protocol;
import remaking.Session;

public class Server implements Runnable {

	private DatagramSocket socket;

	public Server(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	private void handleRequest(DatagramPacket request) throws IOException {
		Session session = new Session();
		RequestHandler handler = new RequestHandler();
		session.setManager(handler.getPacketManagerFromRequest(request.getData(), session));
		session.setUpContact(request.getAddress(), request.getPort());
		session.giveDatagramToManager(request);
	}

	private DatagramPacket receiveRequest() throws IOException {
		byte[] buffer = new byte[512];
		DatagramPacket sessionRequest = new DatagramPacket(buffer, buffer.length);
		socket.receive(sessionRequest);

		return sessionRequest;
	}

	private void listenForContact() throws IOException {

		DatagramPacket contact = receiveRequest();
		if (contact.getLength() == 1) {
			System.out.println("Contact request received");
			giveSignOfLife(contact);
		} else {
			handleRequest(contact);
		}
	}

	private void giveSignOfLife(DatagramPacket contactRequest) throws IOException {
		byte[] datagramSign = new byte[1];
		DatagramPacket signOfLife = new DatagramPacket(datagramSign, datagramSign.length, contactRequest.getAddress(),
				contactRequest.getPort());
		socket.send(signOfLife);
	}

	public static void main(String[] arg) {
		int port = Protocol.PORT;

		try {
			Server server = new Server(port);
			new Thread(server).start();
		} catch (SocketException s) {
			System.out.println("Socket error: " + s.getMessage());
			s.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				listenForContact();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
