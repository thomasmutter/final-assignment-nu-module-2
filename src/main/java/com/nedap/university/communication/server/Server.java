package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import remaking.SessionV2;

public class Server {

	private DatagramSocket socket;

	public Server(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	private void handleRequest(SessionV2 session) throws IOException {
		DatagramPacket request = receiveRequest();
		System.out.println("Request received");
		RequestHandler handler = new RequestHandler();
		session.setManager(handler.getPacketManagerFromRequest(request.getData()));
		session.addToReadQueue(request);
		session.startUp();
	}

	private DatagramPacket receiveRequest() throws IOException {
		byte[] buffer = new byte[512];
		DatagramPacket sessionRequest = new DatagramPacket(buffer, buffer.length);
		socket.receive(sessionRequest);

		return sessionRequest;
	}

	private void startSessionFromRequest() throws IOException {
		while (true) {
			handleRequest(new SessionV2());
		}
	}

	public static void main(String[] arg) {
		if (arg.length < 1) {
			System.out.println("Not enough arguments provided.");
			System.out.println("Syntax: FileTransferServer <port>");
			return;
		}

		int port;
		try {
			port = Integer.parseInt(arg[0]);
		} catch (NumberFormatException n) {
			System.out.println("The provided argument is not an integer");
			return;
		}

		try {
			Server server = new Server(port);
			server.startSessionFromRequest();
		} catch (SocketException s) {
			System.out.println("Socket error: " + s.getMessage());
			s.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
