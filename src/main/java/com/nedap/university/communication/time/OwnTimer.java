package time;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class OwnTimer implements Runnable {

	private SortedMap<Long, Integer> timerMap;
	private TimeKeeper keeper;
	private boolean paused;
	private boolean isStopped;
	private Object lock = new Object();

	private static final long SHORTSLEEP = 10;
	private long rtt;

	public OwnTimer(TimeKeeper keeperArg) {
		timerMap = Collections.synchronizedSortedMap(new TreeMap<>());
		keeper = keeperArg;
		rtt = 100;
	}

	@Override
	public void run() {
		while (!isStopped) {
//			System.out.println("RTT is: " + rtt + " ms");
			synchronized (lock) {
				if (paused) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			checkRetransmissions();
		}
	}

	private void checkRetransmissions() {
		synchronized (timerMap) {
			if (!timerMap.isEmpty()) {
				long time = timerMap.firstKey();
				if (System.currentTimeMillis() - time > rtt) {
					keeper.retransmit(timerMap.get(time));
					timerMap.remove(time);
				}
			}
		}
		try {
			Thread.sleep(getSleepTime() + 1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private long getSleepTime() {
		long goingToSleepTime = System.currentTimeMillis();
		long sleepTime = 0;
		synchronized (timerMap) {
			if (!timerMap.isEmpty()) {
				sleepTime = timerMap.firstKey() - goingToSleepTime;

				if (sleepTime > 0) {
					return sleepTime;
				} else {
					return SHORTSLEEP;
				}
			} else {

				return rtt;
			}
		}
	}

	public void setPaused(boolean isPaused) {
		paused = isPaused;
		synchronized (lock) {
			if (!isPaused) {
				lock.notify();
			}
		}
	}

	public void setSleepTimer(long time) {
		rtt = time;
	}

	public long getRtt() {
		return rtt;
	}

	public Map<Long, Integer> getTimerMap() {
		return timerMap;
	}

	public void setStopped(boolean stop) {
		isStopped = stop;
	}

}
