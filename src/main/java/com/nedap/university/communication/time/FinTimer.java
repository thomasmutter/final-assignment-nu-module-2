package time;

import java.util.TimerTask;

import remaking.Session;

public class FinTimer extends TimerTask {

	private TimeKeeper keeper;
	private Session session;

	public FinTimer(TimeKeeper manager, Session sessionArg) {
		keeper = manager;
		session = sessionArg;
	}

	@Override
	public void run() {
		System.out.println("-----TIMER EXECUTING-----");
		if (keeper.getFinTimersSet() == 1) {
			System.out.println("reached");
			session.shutdown();
		} else {
			keeper.decrementFinTimers();
		}
		this.cancel();
		System.out.println(Thread.currentThread().getName() + " is done");
	}

}
