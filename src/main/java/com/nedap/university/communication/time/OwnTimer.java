package time;

import java.util.Map;
import java.util.TreeMap;

public class OwnTimer implements Runnable {

	private TreeMap<Long, Integer> timerMap;
	private TimeKeeper keeper;

	private static final long SHORTSLEEP = 10;
	private static final long LONGSLEEP = 1000;

	public OwnTimer(TimeKeeper keeperArg) {
		timerMap = new TreeMap<>();
		keeper = keeperArg;
	}

	@Override
	public void run() {
		while (true) {
			for (Long time : timerMap.keySet()) {
				if (System.currentTimeMillis() - time > 1000) {
					keeper.retransmit(timerMap.get(time));
					timerMap.remove(time);
				}
			}
			try {
				Thread.sleep(getSleepTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private long getSleepTime() {
		long goingToSleepTime = System.currentTimeMillis();
		long sleepTime = 0;
		if (!timerMap.isEmpty()) {
			sleepTime = timerMap.firstKey() - goingToSleepTime;
			if (sleepTime > 0) {
				return sleepTime;
			} else {
				return SHORTSLEEP;
			}
		} else {
			return LONGSLEEP;
		}
	}

	public Map<Long, Integer> getTimerMap() {
		return timerMap;
	}

}
