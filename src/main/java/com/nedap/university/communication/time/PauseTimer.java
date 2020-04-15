package time;

import remaking.Paused;

public class PauseTimer implements Runnable {

	private Paused pause;
	private int timersActive;

	public PauseTimer(Paused pausedState) {
		pause = pausedState;
	}

	@Override
	public void run() {

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		pauseIfOkay();

	}

	public synchronized void increaseTimers() {
		timersActive++;
	}

	public synchronized void pauseIfOkay() {
		if (timersActive == 1) {
			pause.pauseOperation();
		} else {
			timersActive--;
		}
	}

}
