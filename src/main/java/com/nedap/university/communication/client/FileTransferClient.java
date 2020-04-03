package client;

import java.net.InetAddress;

import clientTUI.ClientTUI;
import sessionStates.Session;

public class FileTransferClient {

	private ClientTUI tui;

	public FileTransferClient(ClientTUI tuiArg) {
		tui = tuiArg;
	}

	private void start() {
		String[] command = tui.getCommand();
		int port = tui.getInt("Please enter the port number of the server");
		InetAddress address = tui.getIp();
		Session session = new Session(command[0], port, command[1]);
		session.initiateSession(address);
	}

	public static void main(String[] args) {
		ClientTUI tui = new ClientTUI();
		FileTransferClient client = new FileTransferClient(tui);
		client.start();
	}

}
