package upload;

import automaticRepeatRequest.UploadWindow;
import header.HeaderParser;
import managerStates.ManagerState;

public class UploadInitialize implements ManagerState {

	private UploadManager manager;
	private String path;
	private HeaderParser parser;

	public UploadInitialize(UploadManager managerArg, String pathArg) {
		manager = managerArg;
		path = pathArg;
		parser = new HeaderParser();
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		int fileLength = manager.fetchFileFromDisk(path);
		nextState(incomingDatagram, fileLength);
	}

	private void nextState(byte[] incomingDatagram, int fileLength) {
		UploadWindow window = new UploadWindow(parser.getSequenceNumber(incomingDatagram));
		UploadEstablished established = new UploadEstablished(manager, fileLength, window);
		manager.setManagerState(established);
		established.translateIncomingHeader(incomingDatagram);
	}

}
