package server;

import java.io.File;

import communicationProtocols.Protocol;
import download.DownloadManager;
import header.HeaderParser;
import otherCommands.ListManager;
import otherCommands.PacketManager;
import otherCommands.RemoveManager;
import remaking.Session;
import upload.UploadManager;

public class RequestHandler {

	private static final String PATH = System.getProperty("user.dir");

	public PacketManager getPacketManagerFromRequest(byte[] data, Session session) {
		switch (HeaderParser.getCommand(data)) {
		case Protocol.UL:
			return new DownloadManager(session, PATH + File.separator + getFileNameFromDatagram(data));
		case Protocol.DL:
			return new UploadManager(session, PATH + File.separator + getFileNameFromDatagram(data));
		case Protocol.LS:
			return new ListManager(session);
		default:
			return new RemoveManager(session);
		}
	}

	// TO DOOOOOOOOOOOOO

	private String getFileNameFromDatagram(byte[] data) {
		byte[] payload = HeaderParser.getData(HeaderParser.trimEmptyData(data));
		return new String(payload);
	}
}
