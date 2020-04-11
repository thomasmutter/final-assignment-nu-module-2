package timerTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import time.OwnTimer;

class RetransmissionTimerTest {

	private MockKeeper keeper;
	private OwnTimer timer;

	@BeforeEach
	void setUp() throws Exception {
		keeper = new MockKeeper(new MockSession());
		timer = keeper.getTimer();
	}

	@Test
	void testAckedBeforeTimeOut() {
		keeper.setRetransmissionTimer(80);
		keeper.processIncomingAck(80);
		assertTrue(timer.getTimerMap().isEmpty());
	}

	@Test
	void testAckedAfterTimeOut() {
		keeper.setRetransmissionTimer(80);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(timer.getTimerMap().containsValue(80));
		keeper.processIncomingAck(80);
		assertFalse(timer.getTimerMap().containsValue(80));
	}

}
