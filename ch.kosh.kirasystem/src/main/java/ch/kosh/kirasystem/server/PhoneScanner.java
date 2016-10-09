package ch.kosh.kirasystem.server;

public class PhoneScanner {
	private String deviceId;
	private long lastSeenTimestamp;

	public PhoneScanner(String deviceId) {
		this.deviceId = deviceId;
		this.lastSeenTimestamp = 0;
	}

	public void setLastSeenTimestamp(long timestamp) {
		this.lastSeenTimestamp = timestamp;

	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public long getLastSeenTimestamp() {
		return this.lastSeenTimestamp;
	}

}
