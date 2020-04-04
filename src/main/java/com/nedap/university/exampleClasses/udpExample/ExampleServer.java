package udpExample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This program demonstrates how to implement a UDP server program.
 *
 *
 * @author www.codejava.net
 */
public class ExampleServer {
	private static final String DIR = "src/main/java/com/nedap/university/resources";
	private DatagramSocket socket;
	private List<String> listQuotes = new ArrayList<String>();
	private Random random;

	public ExampleServer(int port) throws SocketException {
		socket = new DatagramSocket(port);
		random = new Random();
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Syntax: QuoteServer <file> <port>");
			return;
		}

		String quoteFile = args[0];
		int port = Integer.parseInt(args[1]);

		try {
			ExampleServer server = new ExampleServer(port);
			server.loadQuotesFromFile(quoteFile);
			server.service();
		} catch (SocketException ex) {
			System.out.println("Socket error: " + ex.getMessage());
		} catch (IOException ex) {
			System.out.println("I/O error: " + ex.getMessage());
			System.out.println("The current directory for files is " + System.getProperty("user.dir") + DIR);
		}
	}

	private void service() throws IOException {
		while (true) {
			DatagramPacket request = new DatagramPacket(new byte[1], 1);
			socket.receive(request);

			String quote = getRandomQuote();
			byte[] buffer = quote.getBytes();

			InetAddress clientAddress = request.getAddress();
			int clientPort = request.getPort();

			DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
			System.out.println("Sending quote...");
			socket.send(response);
		}
	}

	private void loadQuotesFromFile(String quoteFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(DIR + File.separator + quoteFile));
		String aQuote;

		while ((aQuote = reader.readLine()) != null) {
			listQuotes.add(aQuote);
		}

		reader.close();
	}

	private String getRandomQuote() {
		int randomIndex = random.nextInt(listQuotes.size());
		String randomQuote = listQuotes.get(randomIndex);
		return randomQuote;
	}
}
