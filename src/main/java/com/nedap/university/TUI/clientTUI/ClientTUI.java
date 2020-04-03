package clientTUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class ClientTUI {

	private static final List<String> commandList = Arrays.asList("dl", "ul", "rm", "rp", "ls", "p", "r", "q");
	private BufferedReader inputReader;

	public ClientTUI() {
		inputReader = new BufferedReader(new InputStreamReader(System.in));
	}

	public String[] getCommand() {
		printMenu();
		boolean isNotCorrectInput = true;
		System.out.println("Please enter a command.");
		String[] commandSplit = null;
		while (isNotCorrectInput) {
			String command = readString();
			commandSplit = command.split("\\s+");
			if (commandSplit.length > 1 && commandList.contains(commandSplit[1])) {
				isNotCorrectInput = false;
			} else {
				System.out.println("Input not recognized, please try again: ");
				System.out.println("Syntax: <filename> <command>");
			}
		}
		return commandSplit;
	}

	public String readString() {
		String input = null;
		try {
			input = inputReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}

	public boolean getBoolean(String question) {
		System.out.println(question);
		String input = "";
		while (!(input.contentEquals("yes")) && !(input.contentEquals("no"))) {
			try {
				System.out.println("Please answer yes or no");
				input = inputReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (input.contentEquals("yes")) {
			return true;
		} else {
			return false;
		}
	}

	public InetAddress getIp() {
		System.out.println("Please provide a valid IP");
		InetAddress ip = null;
		while (ip == null) {
			try {
				String inputIP = inputReader.readLine();
				ip = InetAddress.getByName(inputIP);
			} catch (UnknownHostException u) {
				System.out.println("This IP is invalid. Please provide a valid IP.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ip;
	}

	public int getInt(String question) {
		System.out.println(question);
		while (true) {
			try {
				return Integer.parseInt(inputReader.readLine());
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid integer");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void printMenu() {
		System.out.println("File transfer commands: ");
		System.out.println("Download: dl");
		System.out.println("Upload: ul");
		System.out.println("Remove: rm");
		System.out.println("Replace: rp");
		System.out.println("List available files: ls");
		System.out.println("Pause: p");
		System.out.println("Resume: r");
		System.out.println("Quit: q");
	}

}
