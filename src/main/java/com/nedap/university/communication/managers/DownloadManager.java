package managers;

import java.util.Arrays;

import automaticRepeatRequest.ArqAlgorithm;
import automaticRepeatRequest.StopAndWaitDownload;
import fileConversion.ConversionHandler;
import header.HeaderConstructor;
import header.HeaderParser;
import remaking.Session;

public class DownloadManager implements PacketManager {

	private Session session;
	private String path;
	private byte[] fileAsBytes = new byte[0];
	private ArqAlgorithm algorithm;
	private HeaderParser parser;

	public DownloadManager(Session sessionArg, String pathArg) {
		session = sessionArg;
		path = pathArg;
		algorithm = new StopAndWaitDownload(this);
		parser = new HeaderParser();
	}

	@Override
	public void processIncomingData(byte[] data) {
		if (parser.getStatus(data) != HeaderConstructor.FIN) {
			algorithm.handleIncomingDatagram(data);
		} else {
			finalizeFileTransfer();
			session.finalizeSession(data);
		}
	}

	public void processOutgoingData(byte[] datagram) {
		session.addToSendQueue(datagram);
	}

	public void writeToByteArray(byte[] data) {
		int oldLength = fileAsBytes.length;
		fileAsBytes = Arrays.copyOf(fileAsBytes, fileAsBytes.length + data.length);
		// String content = new String(fileAsBytes);
		// System.out.println(content);
		System.arraycopy(data, 0, fileAsBytes, oldLength, data.length);
	}

	private void finalizeFileTransfer() {
		ConversionHandler converter = new ConversionHandler();
		converter.writeBytesToFile(fileAsBytes, path);
	}

}
