package time;

import java.util.Timer;

import remaking.SessionV2;

public class TimeKeeper {

	private int finTimersSet;
	private SessionV2 session;

	public TimeKeeper(SessionV2 sessionArg) {
		session = sessionArg;
	}

	public int getFinTimersSet() {
		return finTimersSet;
	}

	public void setFinTimer() {
		finTimersSet++;
		Timer timer = new Timer();
		timer.schedule(new FinTimer(this, session), 0);
	}

	public void decrementFinTimers() {
		finTimersSet--;
	}
}
