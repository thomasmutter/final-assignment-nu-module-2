package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;

import communicationProtocols.Protocol;
import download.DownloadManager;
import header.HeaderParser;
import otherCommands.CleanUpManager;
import otherCommands.ListManager;
import otherCommands.PacketManager;
import otherCommands.RemoveManager;
import session.Session;
import sessionTermination.SenderTermination;
import sessionTermination.Terminator;
import upload.UploadManager;

public class RequestHandler {

	private static final String PATH = System.getProperty("user.dir");

	public PacketManager getPacketManagerFromRequest(DatagramPacket data, Session session) {
		switch (HeaderParser.getCommand(data.getData())) {
		case Protocol.UL:
			return new DownloadManager(session, PATH + File.separator + getFileNameFromDatagram(data));
		case Protocol.DL:
			try {
				return new UploadManager(session, PATH + File.separator + getFileNameFromDatagram(data));
			} catch (FileNotFoundException e) {
				return handleFileNotFound(session);
			}
		case Protocol.LS:
			return new ListManager(session);
		default:
			return new RemoveManager(session);
		}
	}

	// TO DOOOOOOOOOOOOO

	private String getFileNameFromDatagram(DatagramPacket data) {
		byte[] fulldata = Session.stripBufferRemainder(data);
		byte[] payload = new byte[fulldata.length - 2 - Protocol.HEADERLENGTH];
		System.arraycopy(HeaderParser.getData(fulldata), 0, payload, 0, payload.length);
		return new String(payload);
	}

	private CleanUpManager handleFileNotFound(Session session) {
		CleanUpManager cleanUp = new CleanUpManager(session);
		Terminator terminator = new SenderTermination(cleanUp, session);
		cleanUp.setTerminator(terminator);
		terminator.terminateSession(Protocol.FIN, 0, 0);
		return cleanUp;
	}
}
