package otherCommands;

import communicationProtocols.Protocol;
import header.HeaderConstructor;
import header.HeaderParser;
import remaking.Session;
import sessionTermination.ReceiverTermination;
import sessionTermination.Terminator;
import upload.UploadManager;

public class ReadDataManager implements PacketManager {

	private Session session;

	public ReadDataManager(Session sessionArg) {
		session = sessionArg;
	}

	@Override
	public void processIncomingData(byte[] data) {
		if (HeaderParser.getCommand(data) == Protocol.RP) {
			UploadManager upload = new UploadManager(session,
					"src/main/java/com/nedap/university/resources/" + getDataAsString(data));
			session.setManager(upload);
			System.out.println("GOING TO UPLOAD");
			upload.processIncomingData(data);
		} else if (HeaderParser.getStatus(data) == Protocol.FIN) {
			shutdownSession(HeaderParser.getSequenceNumber(data), HeaderParser.getAcknowledgementNumber(data));
			return;
		} else {
			System.out.println(getDataAsString(data));
			sendAck(data);
		}
	}

	private void sendAck(byte[] data) {
		byte[] header = headerToSend(HeaderParser.getHeader(data));
		byte[] datagram = new byte[header.length];
		System.arraycopy(header, 0, datagram, 0, header.length);
		session.addToSendQueue(datagram);
	}

	private String getDataAsString(byte[] datagram) {
		byte[] data = new byte[datagram.length - Protocol.HEADERLENGTH];
		int j = 0;
		for (int i = Protocol.HEADERLENGTH; i < datagram.length; i++) {
			data[j] = datagram[i];
			j++;
		}
		return new String(data);
	}

	public byte[] headerToSend(byte[] oldHeader) {
		byte flags = Protocol.LS;
		byte status = Protocol.ACK;
		int seqNo = HeaderParser.getAcknowledgementNumber(oldHeader) + 1;
//		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = HeaderParser.getSequenceNumber(oldHeader);
		int offset = 0;
		return HeaderConstructor.constructHeader(flags, status, seqNo, ackNo, offset);
	}

	private void shutdownSession(int seqNo, int ackNo) {
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new ReceiverTermination(cleanUp);
		session.setManager(cleanUp);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(Protocol.FIN, seqNo, ackNo);
	}

}
