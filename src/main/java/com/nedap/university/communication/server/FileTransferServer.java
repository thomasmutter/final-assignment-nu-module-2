package server;

import java.net.DatagramSocket;
import java.net.SocketException;

public class FileTransferServer {

	private DatagramSocket socket;

	public FileTransferServer(int port) throws SocketException {
		socket = new DatagramSocket(port);
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
		} catch (SocketException s) {
			System.out.println("Socket error: " + s.getMessage());
			s.printStackTrace();
		}
	}
}
