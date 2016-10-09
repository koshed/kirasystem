package ch.kosh.kirasystem.server;

public class SwitchStateLogEntry {

	private SwitchState switchState;
	private long timestamp;
	private String reason;

	public SwitchStateLogEntry(SwitchState switchState, long timestamp, String reason) {
		this.switchState = switchState;
		this.timestamp = timestamp;
		this.reason = reason;
	}

	public SwitchState getSwitchState() {
		return switchState;
	}

	public void setSwitchState(SwitchState switchState) {
		this.switchState = switchState;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getLastSwitchReason() {
		return reason;
	}
}