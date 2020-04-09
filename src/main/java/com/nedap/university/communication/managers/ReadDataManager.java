package managers;

import header.HeaderConstructor;
import header.HeaderParser;
import remaking.Session;

public class ReadDataManager implements PacketManager {

	private Session session;
	private HeaderConstructor headerConstructor;
	private HeaderParser parser;

	public ReadDataManager(Session sessionArg) {
		session = sessionArg;
		headerConstructor = new HeaderConstructor();
		parser = new HeaderParser();
	}

	@Override
	public void processIncomingData(byte[] data) {
		if (parser.getStatus(data) != HeaderConstructor.FIN) {
			sendAck(data);
		} else {
			session.finalizeSession(data);
		}

	}

	private void sendAck(byte[] data) {
		printData(data);
		byte[] header = headerToSend(parser.getHeader(data));

		byte[] datagram = new byte[header.length];

		System.arraycopy(header, 0, datagram, 0, header.length);

		session.addToSendQueue(datagram);
	}

	private void printData(byte[] datagram) {
		byte[] data = new byte[datagram.length - HeaderConstructor.HEADERLENGTH];
		int j = 0;
		for (int i = HeaderConstructor.HEADERLENGTH; i < datagram.length; i++) {
			data[j] = datagram[i];
			j++;
		}
		System.out.println(new String(data));
	}

	public byte[] headerToSend(byte[] oldHeader) {
		byte flags = HeaderConstructor.LS;
		byte status = HeaderConstructor.ACK;
		int seqNo = parser.getAcknowledgementNumber(oldHeader) + 1;
//		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = parser.getSequenceNumber(oldHeader);
		int checksum = 0;
		int windowSize = 0;
		return headerConstructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

}
