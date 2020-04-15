package time;

import remaking.Paused;

public class ResumeTimer implements Runnable {

	private Paused pause;
	private int timersActive;

	public ResumeTimer(Paused pausedState) {
		pause = pausedState;
	}

	@Override
	public void run() {

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		resumeIfOkay();

	}

	public synchronized void increaseTimers() {
		timersActive++;
	}

	public synchronized void resumeIfOkay() {
		if (timersActive == 1) {
			System.out.println("---RESUMING---");
			pause.resumeOperation();
		} else {
			timersActive--;
		}
	}

}
