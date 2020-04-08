package managers;

import java.util.Random;

import header.HeaderConstructor;
import header.HeaderParser;
import remaking.SessionV2;

public class ReadDataManager implements PacketManager {

	private SessionV2 session;
	private HeaderConstructor headerConstructor;
	private HeaderParser parser;

	public ReadDataManager(SessionV2 sessionArg) {
		session = sessionArg;
		headerConstructor = new HeaderConstructor();
		parser = new HeaderParser();
	}

	@Override
	public void processIncomingData(byte[] data) {
		if (parser.getStatus(data) != HeaderConstructor.FIN) {
			sendAck(data);
		} else {
			session.finalizeSession();
		}

	}

	private void sendAck(byte[] data) {
		printData(data);
		byte[] oldHeader = parser.getHeader(data);
		int dataLength = data.length - oldHeader.length;

		byte[] header = headerToSend(parser.getHeader(data), dataLength);

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

	public byte[] headerToSend(byte[] oldHeader, int receivedPayloadLength) {
		byte flags = HeaderConstructor.LS;
		byte status = HeaderConstructor.ACK;
		int seqNo = (new Random()).nextInt(Integer.MAX_VALUE);
		System.out.println("Sending packet with seqNo: " + seqNo);
		int ackNo = parser.getSequenceNumber(oldHeader) + receivedPayloadLength;
		int checksum = 0;
		int windowSize = 0;
		return headerConstructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

}
