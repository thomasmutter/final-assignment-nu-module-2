package time;

import java.util.TimerTask;

import remaking.SessionV2;

public class FinTimer extends TimerTask {

	private TimeKeeper keeper;
	private SessionV2 session;

	public FinTimer(TimeKeeper manager, SessionV2 sessionArg) {
		keeper = manager;
		session = sessionArg;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("-----TIMER EXECUTING-----");
		if (keeper.getFinTimersSet() == 1) {
			System.out.println("reached");
			session.shutdown();
		} else {
			keeper.decrementFinTimers();
		}

	}

}
