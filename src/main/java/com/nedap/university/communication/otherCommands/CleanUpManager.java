package otherCommands;

import communicationProtocols.Protocol;
import header.HeaderConstructor;
import header.HeaderParser;
import remaking.Session;
import sessionTermination.Terminator;

public class CleanUpManager implements PacketManager {

	private Session session;
	private Terminator terminator;

	public CleanUpManager(Session sessionArg) {
		session = sessionArg;
	}

	@Override
	public void processIncomingData(byte[] data) {
		byte status = HeaderParser.getStatus(data);
		int seqNo = HeaderParser.getSequenceNumber(data);
		int ackNo = HeaderParser.getAcknowledgementNumber(data);
		terminator.terminateSession(status, ackNo, seqNo);
	}

	public void sendFin(byte statusArg, int seqNo, int ackNo) {
		String dataString = "FIN";
		byte[] finDatagram = formHeader(statusArg, seqNo, ackNo);
		if (statusArg == (byte) (Protocol.FIN + Protocol.ACK)) {
			dataString = dataString + " ACK";
		}
		byte[] payload = dataString.getBytes();
		System.out.println(dataString + " sent");

		byte[] datagram = new byte[finDatagram.length + payload.length];

		System.arraycopy(finDatagram, 0, datagram, 0, finDatagram.length);
		System.arraycopy(payload, 0, datagram, finDatagram.length, payload.length);

		session.addToSendQueue(datagram);
	}

	private byte[] formHeader(byte statusArg, int seqNoArg, int ackNoArg) {
		byte flags = 0;
		byte status = statusArg;
		int seqNo = seqNoArg + Protocol.ACKSIZE;
		int ackNo = ackNoArg;
		int checksum = 0;
		int windowSize = 1;
		return HeaderConstructor.constructHeader(flags, status, seqNo, ackNo, windowSize, checksum);
	}

	public void setTerminator(Terminator terminatorArg) {
		terminator = terminatorArg;
	}

	public void shutdownSession() {
		session.shutdown();
	}

}
