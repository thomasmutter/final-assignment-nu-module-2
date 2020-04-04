package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import sessionStates.Initializing;
import sessionStates.Session;

public class FileTransferServer {

	private DatagramSocket socket;

	public FileTransferServer(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	private DatagramPacket handleRequest() throws IOException {
		byte[] buffer = new byte[512];
		DatagramPacket sessionRequest = new DatagramPacket(buffer, buffer.length);
		socket.receive(sessionRequest);

		return sessionRequest;
	}

	private void createSession(DatagramPacket datagram) {
		Session session = new Session(datagram.getPort());
		((Initializing) session.getInitializing()).setSetupManager(new ServerSetupHandler());
		session.initiateSession(datagram.getAddress(), datagram.getData());
	}

	private void start() throws IOException {
		while (true) {
			createSession(handleRequest());
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
			FileTransferServer server = new FileTransferServer(port);
			server.start();
		} catch (SocketException s) {
			System.out.println("Socket error: " + s.getMessage());
			s.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
