package otherCommands;

import java.io.File;

import download.DownloadEstablished;
import download.DownloadManager;
import header.HeaderConstructor;
import header.HeaderParser;
import remaking.Session;
import sessionTermination.SenderTermination;
import sessionTermination.Terminator;
import time.TimeKeeper;

public class RemoveManager implements PacketManager {

	private Session session;
	private HeaderConstructor headerConstructor;
	private HeaderParser parser;
	private static final String PATH = "test-folder";

	public RemoveManager(Session sessionArg) {
		session = sessionArg;
		headerConstructor = new HeaderConstructor();
		parser = new HeaderParser();
	}

	@Override
	public void processIncomingData(byte[] data) {
		if (parser.getStatus(data) != HeaderConstructor.ACK) {
			removeOrReplace(data);
		} else {
			shutdownSession(parser.getSequenceNumber(data), parser.getAcknowledgementNumber(data));
		}
	}

	private void removeOrReplace(byte[] data) {
		String path = getFileNameFromDatagram(data);
		removeFile(path);
		if (parser.getCommand(data) == HeaderConstructor.RP) {
			System.out.println("---- REPLACING ---- ");
			setUpDownloadAfterRemove(PATH + File.separator + path, data);
			session.addToSendQueue(prepareDatagramToSend(HeaderConstructor.RP, data));
		} else {
			session.addToSendQueue(prepareDatagramToSend(HeaderConstructor.RM, data));
		}

	}

	private byte[] prepareDatagramToSend(byte status, byte[] data) {
		byte[] header = headerToSend(status, data);
		byte[] payload = getFileNameFromDatagram(data).getBytes();
		byte[] datagram = new byte[payload.length + HeaderConstructor.HEADERLENGTH];
		System.out.println("length of payload: " + payload.length);
		System.arraycopy(header, 0, datagram, 0, header.length);
		System.arraycopy(payload, 0, datagram, header.length, payload.length);
		System.out.println("length of outgoing bytes:  " + datagram.length);
		System.out.println("header length is " + header.length);
		return datagram;
	}

	private void setUpDownloadAfterRemove(String path, byte[] data) {
		DownloadManager download = new DownloadManager(session, path);
		session.setManager(download);
		download.setManagerState(new DownloadEstablished(download, parser.getSequenceNumber(data)));
//		download.processIncomingData(data);
	}

	private String getFileNameFromDatagram(byte[] data) {
		byte[] payload = parser.getData(data);
		return new String(payload);
	}

	private void removeFile(String fileName) {
		File here = new File(".");
		System.out.println(fileName.length());
		File file = new File(PATH + File.separator + fileName);
		System.out.println(here.getAbsolutePath());
		System.out.println(file.exists());
		System.out.println("The name of the file is: " + fileName);

		boolean succes = file.delete();
		System.out.println(succes);
	}

	private byte[] headerToSend(byte command, byte[] oldHeader) {
		byte flags = command;
		byte status = HeaderConstructor.ACK;
		int seqNo = 0;// (new Random()).nextInt(Integer.MAX_VALUE);
//		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = parser.getSequenceNumber(oldHeader);
		int checksum = 0;
		int windowSize = 1;
//		System.out.println("The payload size is: " + windowSize);
		return headerConstructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

	private void shutdownSession(int seqNo, int ackNo) {
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new SenderTermination(cleanUp, new TimeKeeper(session));
		session.setManager(cleanUp);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(seqNo, ackNo);
	}

}
