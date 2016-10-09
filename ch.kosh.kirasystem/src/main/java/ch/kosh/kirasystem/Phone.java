package ch.kosh.kirasystem;

public class Phone {

	public static final int AWAY_TIME = 90 * 1000;

	private String macaddress;
	private long lastAvailableTimestamp;

	private String ownerName;

	public Phone(String macaddress, String ownerName) {
		this.macaddress = macaddress;
		this.ownerName = ownerName;
		this.lastAvailableTimestamp = 0;
	}

	public String getMacAddress() {
		return this.macaddress;
	}

	public boolean isAvailable(long currentTime) {
		if (lastAvailableTimestamp < currentTime - AWAY_TIME) {
			return false;
		}
		return true;
	}

	public long getLastAvailableTimestamp() {
		return this.lastAvailableTimestamp;
	}

	public void setLastAvailableTimestamp(long timestamp) {
		this.lastAvailableTimestamp = timestamp;
	}

	public String getOwnerName() {
		return ownerName;
	}

}
