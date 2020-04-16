package otherCommands;

import java.io.File;

import communicationProtocols.Protocol;
import download.DownloadManager;
import header.HeaderConstructor;
import header.HeaderParser;
import session.Session;
import sessionTermination.SenderTermination;
import sessionTermination.Terminator;

public class RemoveManager implements PacketManager {

	private Session session;
	private static final String PATH = System.getProperty("user.dir");

	public RemoveManager(Session sessionArg) {
		session = sessionArg;
	}

	@Override
	public void processIncomingData(byte[] data) {
		if (HeaderParser.getStatus(data) != Protocol.ACK) {
			removeOrReplace(data);
		} else {
			shutdownSession(HeaderParser.getSequenceNumber(data), HeaderParser.getAcknowledgementNumber(data));
		}
	}

	private void removeOrReplace(byte[] data) {
		String path = getFileNameFromDatagram(data);
		removeFile(path);
		if (HeaderParser.getCommand(data) == Protocol.RP) {
			System.out.println("---- REPLACING ---- ");
			setUpDownloadAfterRemove(PATH + File.separator + path, data);
			session.addToSendQueue(prepareDatagramToSend(Protocol.RP, data));
		} else {
			session.addToSendQueue(prepareDatagramToSend(Protocol.RM, data));
		}

	}

	private byte[] prepareDatagramToSend(byte status, byte[] data) {
		byte[] header = headerToSend(status, data);
		byte[] payload = getFileNameFromDatagram(data).getBytes();
		byte[] datagram = new byte[payload.length + Protocol.HEADERLENGTH];
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
//		download.setManagerState(new DownloadEstablished(download));
//		download.processIncomingData(data);
	}

	private String getFileNameFromDatagram(byte[] data) {
		byte[] payload = HeaderParser.getData(data);
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
		byte status = Protocol.ACK;
		int seqNo = 0;// (new Random()).nextInt(Integer.MAX_VALUE);
//		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = HeaderParser.getSequenceNumber(oldHeader);
		int offset = 0;
//		System.out.println("The payload size is: " + windowSize);
		return HeaderConstructor.constructHeader(flags, status, seqNo, ackNo, 0);
	}

	private void shutdownSession(int seqNo, int ackNo) {
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new SenderTermination(cleanUp, session);
		session.setManager(cleanUp);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(Protocol.FIN, seqNo, ackNo);
	}

}
