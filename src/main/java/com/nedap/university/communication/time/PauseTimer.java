package time;

import session.Paused;

public class PauseTimer extends BasicTimer {

	private Paused pause;

	public PauseTimer(Paused pausedState) {
		pause = pausedState;
	}

	@Override
	public synchronized void timerExpiredAction() {
		if (timersActive == 1) {
			pause.pauseOperation();
		} else {
			timersActive--;
		}
	}
}
