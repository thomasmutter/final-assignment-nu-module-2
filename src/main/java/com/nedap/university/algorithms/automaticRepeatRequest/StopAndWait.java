package automaticRepeatRequest;

import header.HeaderConstructor;
import header.HeaderParser;

public abstract class StopAndWait implements ArqAlgorithm {

	protected HeaderParser parser;
	private HeaderConstructor constructor;

	protected int lastAckedSeqNo;

	public StopAndWait() {
		parser = new HeaderParser();
		constructor = new HeaderConstructor();
	}

	@Override
	public boolean datagramInWindow(byte[] datagram) {
		int incomingSeqNo = parser.getSequenceNumber(datagram);
		int payloadSize = parser.getAcknowledgementNumber(datagram);
		if (incomingSeqNo - payloadSize == lastAckedSeqNo) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public abstract void handleIncomingDatagram(byte[] datagram);

	public byte[] formHeader(byte[] datagram, int payloadSize) {
		byte flags = HeaderConstructor.DL;
		byte status = HeaderConstructor.ACK;
		int seqNo = parser.getAcknowledgementNumber(datagram) + payloadSize;
		int ackNo = parser.getSequenceNumber(datagram);
		int checksum = 0;
		int windowSize = payloadSize;
		return constructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

}
