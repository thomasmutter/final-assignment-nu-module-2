package time;

import session.Paused;

public class ResumeTimer extends BasicTimer {

	private Paused pause;

	public ResumeTimer(Paused pausedState) {
		pause = pausedState;
	}

	public synchronized void increaseTimers() {
		timersActive++;
	}

	@Override
	public synchronized void timerExpiredAction() {
		if (timersActive == 1) {
			System.out.println("---RESUMING---");
			pause.resumeOperation();
		} else {
			timersActive--;
		}
	}

}
