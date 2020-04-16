package time;

public abstract class BasicTimer implements Runnable {

	protected int timersActive;
	private static final int WAIT = 200;

	@Override
	public void run() {

		try {
			Thread.sleep(WAIT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timerExpiredAction();

	}

	public synchronized void increaseTimers() {
		timersActive++;
	}

	public abstract void timerExpiredAction();

}
