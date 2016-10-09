package ch.kosh.kirasystem.server;

public class CamStateLogEntry {

	public enum CamState {
		UNKNOWN, OFF, ON;
	}

	private CamState camState;
	private long timestamp;
	private String reason;

	public CamStateLogEntry(CamState camState, long timestamp, String reason) {
		this.camState = camState;
		this.timestamp = timestamp;
		this.reason = reason;
	}

	public CamState getCamState() {
		return camState;
	}

	public void setCamState(CamState camState) {
		this.camState = camState;
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