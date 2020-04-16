package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputListener implements Runnable {

	private BufferedReader in;
	private Client client;

	private boolean cont;

	public InputListener(Client clientArg) {
		client = clientArg;
		cont = true;
		in = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public void run() {
		String input = readMessage();
		while (cont) {
			try {
				handleUserInput(input);
				input = readMessage();
			} catch (IOException e) {
				System.out.println("Listener closed");
			}
		}
	}

	private String readMessage() {
		String input = null;
		try {
			input = in.readLine();
		} catch (IOException e) {
			System.out.println("Listener closed");
		}

		return input;
	}

	private void handleUserInput(String input) throws IOException {
		String[] inputArray = input.split("\\s+");
		String command = inputArray[0];
		String fileName = null;
		if (inputArray.length > 1) {
			fileName = inputArray[1];
		}
		switch (command) {
		case "p":
			client.pauseSession(fileName);
			break;
		case "r":
			client.resumeSession(fileName);
			break;
		case "close":
			cont = false;
			in.close();
			break;
		case "stats":
			System.out.println(client.getMetricsFromSession(fileName).toString());
			break;
		default:
			System.out.println("S T A R T I N G   S E S S I O N");
			client.startSession(input);
			break;
		}
	}

}
