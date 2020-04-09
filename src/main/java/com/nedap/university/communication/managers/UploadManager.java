package managers;

import fileConversion.ConversionHandler;
import remaking.Session;

public class UploadManager implements PacketManager {

	private Session session;
	private byte[] fileAsBytes;

	public UploadManager(Session sessionArg, String pathArg) {
		session = sessionArg;
		initialize(pathArg);
	}

	@Override
	public void processIncomingData(byte[] data) {
		// TODO Auto-generated method stub

	}

	public void processOutgoingData(byte[] header) {

	}

	private void initialize(String path) {
		ConversionHandler fileToByteConverter = new ConversionHandler();
		fileAsBytes = fileToByteConverter.readFileToBytes(path);
	}

}
