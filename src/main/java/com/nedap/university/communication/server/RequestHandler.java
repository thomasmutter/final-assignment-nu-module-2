package server;

import header.HeaderConstructor;
import header.HeaderParser;
import managers.DownloadManager;
import managers.ListManager;
import managers.PacketManager;
import managers.RemoveManager;
import managers.UploadManager;

public class RequestHandler {

	private HeaderParser parser;

	public RequestHandler() {
		parser = new HeaderParser();
	}

	public PacketManager getPacketManagerFromRequest(byte[] data) {
		switch (parser.getCommand(parser.getHeader(data))) {
		case HeaderConstructor.UL:
			return new DownloadManager();
		case HeaderConstructor.DL:
			return new UploadManager();
		case HeaderConstructor.RM:
			return new RemoveManager();
		default:
			return new ListManager();
		}
	}
}
