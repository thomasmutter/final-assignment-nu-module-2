package time;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class OwnTimer implements Runnable {

	private SortedMap<Long, Integer> timerMap;
	private TimeKeeper keeper;

	private static final long SHORTSLEEP = 10;
	private static final long LONGSLEEP = 10;

	public OwnTimer(TimeKeeper keeperArg) {
		timerMap = Collections.synchronizedSortedMap(new TreeMap<>());
		keeper = keeperArg;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (timerMap) {
				if (!timerMap.isEmpty()) {
					long time = timerMap.firstKey();
					if (System.currentTimeMillis() - time > LONGSLEEP) {
						keeper.retransmit(timerMap.get(time));
						timerMap.remove(time);
					}
				}
			}
			try {
				Thread.sleep(getSleepTime());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//			Set<Entry<Long, Integer>> entrySet = timerMap.entrySet();
//			Iterator<Entry<Long, Integer>> timeIterator = entrySet.iterator();
//			while (timeIterator.hasNext()) {
//				Entry<Long, Integer> entry = timeIterator.next();
//				long time = entry.getKey();
//				if (System.currentTimeMillis() - time > 1000) {
//					keeper.retransmit(entry.getValue());
//					timeIterator.remove();
//				}
//			}
//			try {
//				Thread.sleep(getSleepTime());
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
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
				return LONGSLEEP;
			}
		}
	}

	public Map<Long, Integer> getTimerMap() {
		return timerMap;
	}

}
