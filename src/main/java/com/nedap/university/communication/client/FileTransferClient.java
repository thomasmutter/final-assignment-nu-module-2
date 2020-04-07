package client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import clientTUI.ClientTUI;
import sessionStates.Initializing;
import sessionStates.Session;

public class FileTransferClient {

	private ClientTUI tui;

	public FileTransferClient(ClientTUI tuiArg) {
		tui = tuiArg;
	}

	private void start() {
		String command = tui.getCommand();
		int port = 8888;// tui.getInt("Please enter the port number of the server");
		InetAddress address = null;
		try {
			address = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // tui.getIp();
		Session session = new Session(port);
		ClientSetupHandler handler = new ClientSetupHandler();
		((Initializing) session.getInitializing()).setSetupManager(handler);
		handler.setFlagsFromCommand(command.split("\\s+")[0]);
		session.initiateSession(address, command.getBytes());
	}

	public static void main(String[] args) {
		ClientTUI tui = new ClientTUI();
		FileTransferClient client = new FileTransferClient(tui);
		client.start();
	}

}
