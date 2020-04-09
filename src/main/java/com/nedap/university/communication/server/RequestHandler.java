package server;

import header.HeaderConstructor;
import header.HeaderParser;
import managers.DownloadManager;
import managers.ListManager;
import managers.PacketManager;
import managers.RemoveManager;
import managers.UploadManager;
import remaking.SessionV2;

public class RequestHandler {

	private static final String PATH = "test-folder";
	private HeaderParser parser;

	public RequestHandler() {
		parser = new HeaderParser();
	}

	public PacketManager getPacketManagerFromRequest(byte[] data, SessionV2 session) {
		switch (parser.getCommand(parser.getHeader(data))) {
		case HeaderConstructor.UL:
			return new DownloadManager(session, PATH + getFileNameFromDatagram(data));
		case HeaderConstructor.DL:
			return new UploadManager(session, PATH + getFileNameFromDatagram(data));
		case HeaderConstructor.RM:
			return new RemoveManager(session);
		default:
			return new ListManager(session);
		}
	}

	private String getFileNameFromDatagram(byte[] data) {
		byte[] payload = parser.getData(parser.trimEmptyData(data));
		return new String(payload);
	}
}
