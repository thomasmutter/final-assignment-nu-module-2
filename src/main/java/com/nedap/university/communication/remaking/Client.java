package remaking;

import java.net.InetAddress;
import java.net.UnknownHostException;

import client.InputInterpreter;
import clientTUI.ClientTUI;

public class Client {

	private ClientTUI tui;

	public Client(ClientTUI tuiArg) {
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

		SessionV2 session = new SessionV2();
		InputInterpreter input = new InputInterpreter(command);
		session.setManager(input.getPacketManagerFromInput());
		session.addToSendQueue(input.getDatagramFromInput());
		session.startUp();
	}

	public static void main(String[] args) {
		ClientTUI tui = new ClientTUI();
		Client client = new Client(tui);
		client.start();
	}

}
