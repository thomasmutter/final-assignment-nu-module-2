package managers;

import fileConversion.ConversionHandler;
import remaking.SessionV2;

public class DownloadManager implements PacketManager {

	private SessionV2 session;
	private String path;
	private byte[] fileAsBytes;

	public DownloadManager(SessionV2 sessionArg, String pathArg) {
		session = sessionArg;
		path = pathArg;
	}

	@Override
	public void processIncomingData(byte[] data) {
		// TODO Auto-generated method stub

	}

	public void processOutgoingData(byte[] header) {

	}

	private void finalizeFileTransfer() {
		ConversionHandler converter = new ConversionHandler();
		converter.writeBytesToFile(fileAsBytes, path);
	}

}
