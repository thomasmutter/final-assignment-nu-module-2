package remaking;

import java.util.Random;

import communicationProtocols.Protocol;
import header.HeaderConstructor;
import header.HeaderParser;
import time.PauseTimer;
import time.ResumeTimer;

public abstract class Paused {

	protected HeaderParser parser;
	private ResumeTimer resumeTimer;
	private PauseTimer pauseTimer;
	private Random random;

	public Paused() {
		parser = new HeaderParser();
		resumeTimer = new ResumeTimer(this);
		pauseTimer = new PauseTimer(this);
		random = new Random();
	}

	public abstract void resumeOperation();

	public abstract void pauseOperation();

	protected void resumeOrPause(byte[] incomingDatagram) {
		if (containsResume(parser.getStatus(incomingDatagram))) {
			resume(incomingDatagram);
		} else if (containsPause(parser.getStatus(incomingDatagram))) {
			pause(incomingDatagram);
		}
	}

	protected void pause(byte[] incomingDatagram) {
		if (incomingDatagram.length == Protocol.TRIGGERLENGTH) {
			sendMessage(HeaderConstructor.P, random.nextInt(Integer.MAX_VALUE),
					parser.getSequenceNumber(incomingDatagram));
		} else if (parser.getStatus(incomingDatagram) == HeaderConstructor.P) {
			sendMessage(HeaderConstructor.PAUSEACK, random.nextInt(Integer.MAX_VALUE),
					parser.getSequenceNumber(incomingDatagram));

			new Thread(pauseTimer).start();
			pauseTimer.increaseTimers();
		} else {
			pauseOperation();
		}
	}

	protected void resume(byte[] incomingDatagram) {
		if (incomingDatagram.length == Protocol.TRIGGERLENGTH) {
			sendMessage(HeaderConstructor.R, random.nextInt(Integer.MAX_VALUE),
					parser.getSequenceNumber(incomingDatagram));
		} else if (parser.getStatus(incomingDatagram) == HeaderConstructor.R) {
			sendMessage(HeaderConstructor.RESUMEACK, random.nextInt(Integer.MAX_VALUE),
					parser.getSequenceNumber(incomingDatagram));
			new Thread(resumeTimer).start();
			resumeTimer.increaseTimers();
		} else {
			resumeOperation();
		}
	}

	protected abstract void sendMessage(byte status, int seqNo, int ackNo);

	private boolean containsResume(byte status) {
		return status == HeaderConstructor.R || status == HeaderConstructor.RESUMEACK;
	}

	private boolean containsPause(byte status) {
		return status == HeaderConstructor.P || status == HeaderConstructor.PAUSEACK;
	}
}
