package session;

import java.util.Random;

import communicationProtocols.Protocol;
import header.HeaderParser;
import time.PauseTimer;
import time.ResumeTimer;

public abstract class Paused {

	private ResumeTimer resumeTimer;
	private PauseTimer pauseTimer;
	private Random random;

	public Paused() {
		resumeTimer = new ResumeTimer(this);
		pauseTimer = new PauseTimer(this);
		random = new Random();
	}

	public abstract void resumeOperation();

	public abstract void pauseOperation();

	protected void resumeOrPause(byte[] incomingDatagram) {
		if (containsResume(HeaderParser.getStatus(incomingDatagram))) {
			resume(incomingDatagram);
		} else if (containsPause(HeaderParser.getStatus(incomingDatagram))) {
			pause(incomingDatagram);
		}
	}

	protected void pause(byte[] incomingDatagram) {
		if (incomingDatagram.length == Protocol.TRIGGERLENGTH) {
			sendMessage(Protocol.P, random.nextInt(Integer.MAX_VALUE),
					HeaderParser.getSequenceNumber(incomingDatagram));
		} else if (HeaderParser.getStatus(incomingDatagram) == Protocol.P) {
			sendMessage(Protocol.PAUSEACK, random.nextInt(Integer.MAX_VALUE),
					HeaderParser.getSequenceNumber(incomingDatagram));

			new Thread(pauseTimer).start();
			pauseTimer.increaseTimers();
		} else {
			pauseOperation();
		}
	}

	protected void resume(byte[] incomingDatagram) {
		if (incomingDatagram.length == Protocol.TRIGGERLENGTH) {
			sendMessage(Protocol.R, random.nextInt(Integer.MAX_VALUE),
					HeaderParser.getSequenceNumber(incomingDatagram));
		} else if (HeaderParser.getStatus(incomingDatagram) == Protocol.R) {
			sendMessage(Protocol.RESUMEACK, random.nextInt(Integer.MAX_VALUE),
					HeaderParser.getSequenceNumber(incomingDatagram));
			new Thread(resumeTimer).start();
			resumeTimer.increaseTimers();
		} else {
			resumeOperation();
		}
	}

	protected abstract void sendMessage(byte status, int seqNo, int ackNo);

	private boolean containsResume(byte status) {
		return status == Protocol.R || status == Protocol.RESUMEACK;
	}

	private boolean containsPause(byte status) {
		return status == Protocol.P || status == Protocol.PAUSEACK;
	}
}
