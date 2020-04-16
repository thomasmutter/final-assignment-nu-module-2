package time;

import session.Session;

public class FinTimer extends BasicTimer {

	private Session session;

	public FinTimer(Session sessionArg) {
		session = sessionArg;
	}

	@Override
	public synchronized void timerExpiredAction() {
		if (timersActive == 1) {
			session.shutdown();
		} else {
			System.out.println(timersActive);
			timersActive--;
		}
	}

}
