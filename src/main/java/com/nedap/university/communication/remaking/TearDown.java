package remaking;

public class TearDown {

	private TearDownState state;
	private TearDownState activeClose;
	private TearDownState passiveClose;

	public TearDown(boolean finReceived) {
		activeClose = new ActiveClose();
		passiveClose = new PassiveClose();
		setInitialState(finReceived);
	}

	public void tearDown() {
		state.takeFinalizationStep();
	}

	private void setInitialState(boolean finReceived) {
		if (finReceived) {
			state = passiveClose;
		} else {
			state = activeClose;
		}
	}

}
