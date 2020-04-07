package server;

import java.util.Random;

import header.HeaderConstructor;
import header.HeaderParser;
import sessionStates.Session;
import sessionStates.SetupManager;

public class ServerSetupHandler implements SetupManager {

	private HeaderConstructor headerConstructor;
	private HeaderParser headerParser;
	private byte flags;

	public ServerSetupHandler() {
		headerConstructor = new HeaderConstructor();
		headerParser = new HeaderParser();
	}

	@Override
	public byte[] setupSession(Session session, byte[] incomingData) {
		flags = (byte) headerParser.getCommand(incomingData);
		switch (flags) {
		case HeaderConstructor.DL:
			session.setSessionState(session.getUploading());
			return session.getUploading().composeDatagram(incomingData);
		case HeaderConstructor.UL:
			session.setSessionState(session.getDownloading());
			return session.getDownloading().composeDatagram(incomingData);
		case HeaderConstructor.RM:
			session.setSessionState(session.getRemoving());
			return session.getRemoving().composeDatagram(incomingData);
		case HeaderConstructor.RP:
			session.setSessionState(session.getReplacing());
			return session.getReplacing().composeDatagram(incomingData);
		case HeaderConstructor.LS:
			session.setSessionState(session.getListing());
			return session.getListing().composeDatagram(incomingData);
		default:
			session.setSessionState(session.getFinalizing());
			return session.getFinalizing().composeDatagram(incomingData);
		}
	}

//	@Override
//	public byte[] composeData(byte[] buffer) {
//		
//		session.
//		
//		
//
//		byte[] dataToSend = reduceBufferToRelevantData(buffer);
//		byte[] headerToSend = headerToSend(headerParser.getHeader(buffer));
//
//		byte[] datagram = new byte[headerToSend.length + dataToSend.length];
//
//		System.arraycopy(headerToSend, 0, datagram, 0, headerToSend.length);
//		System.arraycopy(dataToSend, 0, datagram, headerToSend.length, dataToSend.length);
//
//		System.out.println("The header of the packet to be sent: ");
//		byte[] header = headerParser.getHeader(datagram);
//		System.out.println("Flags: " + headerParser.getCommand(header));
//		System.out.println("Status: " + headerParser.getStatus(header));
//		System.out.println("seqNo: " + headerParser.getSequenceNumber(header));
//		System.out.println("ackNo: " + headerParser.getAcknowledgementNumber(header));
//		System.out.println("windowSize: " + headerParser.getWindowSize(header));
//		System.out.println("checkSum: " + headerParser.getChecksum(header));
//
//		return datagram;
//	}

	private byte[] headerToSend(byte[] oldHeader) {
		byte status = 1;

		int seqNo = (new Random()).nextInt(Integer.MAX_VALUE);
		int ackNo = headerParser.getSequenceNumber(oldHeader) + headerParser.getWindowSize(oldHeader);
		int checksum = 0;
		int windowSize = oldHeader.length;
		return headerConstructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

	private byte[] reduceBufferToRelevantData(byte[] buffer) {
		byte[] incomingHeader = headerParser.getHeader(buffer);
		int dataSize = headerParser.getWindowSize(incomingHeader);
		byte[] dataToSend = new byte[dataSize];

		System.out.println(dataSize);
		for (int i = 0; i < dataSize; i++) {
			dataToSend[i] = buffer[i + HeaderConstructor.HEADERLENGTH];
		}
		String s = new String(dataToSend);
		System.out.println(s);
		return dataToSend;
	}

}
