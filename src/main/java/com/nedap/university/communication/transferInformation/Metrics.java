package transferInformation;

import java.net.DatagramPacket;

import header.HeaderParser;

public class Metrics {

	private HeaderParser parser;

	private long startTime;
	private int packetsSent;
	private int uniquePacketsSent;
	private int packetsReceived;
	private int uniquePacketsReceived;

	private double averagePacketLength;

	public Metrics() {
		startTime = System.currentTimeMillis();
		parser = new HeaderParser();
	}

	private double calculateUpSpeed() {
		double speed = packetsSent * averagePacketLength / (System.currentTimeMillis() - startTime);
		return speed;
	}

	private double calculateDownSpeed() {
		double speed = packetsReceived * averagePacketLength / (System.currentTimeMillis() - startTime);
		return speed;
	}

	private long getTimePassed() {
		return System.currentTimeMillis() - startTime;
	}

	private void updateUniquePackets() {

	}

	public void getMetricsFromPacket(DatagramPacket data) {
		packetsReceived++;

	}

}
