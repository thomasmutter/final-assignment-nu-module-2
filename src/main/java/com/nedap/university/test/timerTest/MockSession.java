package timerTest;

import java.net.SocketException;

import remaking.Session;

public class MockSession extends Session {

	public MockSession() throws SocketException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initialize() {
		System.out.println("Mock initialization");
	}

}
