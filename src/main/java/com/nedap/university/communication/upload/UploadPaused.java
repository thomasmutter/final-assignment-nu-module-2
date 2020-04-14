package upload;

import header.HeaderConstructor;
import header.HeaderParser;
import managerStates.ManagerState;

public class UploadPaused implements ManagerState {

	private UploadManager manager;
	private UploadEstablished upload;
	private HeaderParser parser;

	public UploadPaused(UploadManager managerArg, UploadEstablished previousState) {
		manager = managerArg;
		upload = previousState;
		parser = new HeaderParser();
	}

	@Override
	public void translateIncomingHeader(byte[] incomingDatagram) {
		if (parser.getCommand(incomingDatagram) == HeaderConstructor.R) {
			nextState();
			byte[] lastHeader = upload.getLastHeader();
			byte[] header = manager.formHeader(HeaderConstructor.R, parser.getSequenceNumber(lastHeader),
					parser.getAcknowledgementNumber(lastHeader), parser.getWindowSize(lastHeader));
			byte[] data = upload.getLastData();
			manager.processOutgoingData(header, data);
		}
	}

	private void nextState() {
		manager.setManagerState(upload);
	}

}
