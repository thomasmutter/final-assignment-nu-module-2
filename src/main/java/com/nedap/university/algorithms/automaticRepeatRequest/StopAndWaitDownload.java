package automaticRepeatRequest;

import header.HeaderConstructor;
import managers.DownloadManager;

public class StopAndWaitDownload extends StopAndWait {

	private DownloadManager manager;

	public StopAndWaitDownload(DownloadManager managerArg) {
		manager = managerArg;
	}

	@Override
	public void handleIncomingDatagram(byte[] datagram) {
		byte[] header = formHeader(datagram, 1);
		if (parser.getStatus(datagram) == HeaderConstructor.ACK
				&& parser.getSequenceNumber(datagram) != lastAckedSeqNo) {
			manager.writeToByteArray(parser.getData(datagram));
		}
		manager.processOutgoingData(header);
	}

}
