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

	private HeaderParser parser;

	public RequestHandler() {
		parser = new HeaderParser();
	}

	public PacketManager getPacketManagerFromRequest(byte[] data, SessionV2 session) {
		switch (parser.getCommand(parser.getHeader(data))) {
		case HeaderConstructor.UL:
			return new DownloadManager(session);
		case HeaderConstructor.DL:
			return new UploadManager(session);
		case HeaderConstructor.RM:
			return new RemoveManager(session);
		default:
			return new ListManager(session);
		}
	}
}
