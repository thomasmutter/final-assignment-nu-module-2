package server;

import java.io.File;

import download.DownloadManager;
import header.HeaderConstructor;
import header.HeaderParser;
import otherCommands.ListManager;
import otherCommands.PacketManager;
import otherCommands.RemoveManager;
import remaking.Session;
import upload.UploadManager;

public class RequestHandler {

	private static final String PATH = System.getProperty("user.dir");
	private HeaderParser parser;

	public RequestHandler() {
		parser = new HeaderParser();
	}

	public PacketManager getPacketManagerFromRequest(byte[] data, Session session) {
		switch (parser.getCommand(parser.getHeader(data))) {
		case HeaderConstructor.UL:
			return new DownloadManager(session, PATH + File.separator + getFileNameFromDatagram(data));
		case HeaderConstructor.DL:
			return new UploadManager(session, PATH + File.separator + getFileNameFromDatagram(data));
		case HeaderConstructor.LS:
			return new ListManager(session);
		default:
			return new RemoveManager(session);
		}
	}

	private String getFileNameFromDatagram(byte[] data) {
		byte[] payload = parser.getData(parser.trimEmptyData(data));
		return new String(payload);
	}
}
