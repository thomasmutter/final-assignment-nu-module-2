package transferInformation;

import java.util.HashSet;
import java.util.Set;

public class Metrics {

	private long startTime;
	private int packetsSent;

	private Set<Integer> uniquePacketsSent;

	private double averagePacketLength;

	public Metrics() {
		startTime = System.currentTimeMillis();
		uniquePacketsSent = new HashSet<>();
	}

	private String calculateUpSpeed() {
		double speed = 1e-3 * packetsSent * averagePacketLength / (System.currentTimeMillis() - startTime);
		String speedString = String.format("The transfer speed is: %.2f MB/s", speed);
		return speedString;
	}

//	private double calculateDownSpeed() {
//		double speed = packetsReceived * averagePacketLength / (System.currentTimeMillis() - startTime);
//		return speed;
//	}

	private String getTimePassed() {
		long time = System.currentTimeMillis() - startTime;
		long minutes = time / (60 * 1000);
		long seconds = (time / 1000) % 60;
		String timeString = String.format("Time passed: %d:%02d", minutes, seconds);
		return timeString;
	}

	private String lostPackets() {
		int lostPackets = packetsSent - uniquePacketsSent.size();
		double percentage = 100 * lostPackets / packetsSent;
		String lostString = String.format("The package loss percentage is: %.1f%%", percentage);
		return lostString;
	}

	public void updatePacketsSent(int packetId, int size) {
		packetsSent++;
		averagePacketLength = averagePacketLength + size / packetsSent;
		if (!uniquePacketsSent.contains(packetId)) {
			uniquePacketsSent.add(packetId);
		}
	}

	@Override
	public String toString() {
		return getTimePassed() + "\n" + calculateUpSpeed() + "\n" + lostPackets() + "\n";
	}

}
