package ch.kosh.kirasystem;

public class KiraPhoneScanResult {
	private final String scannerId;
	private final String macAddress;
	private final boolean isAvailable;
	private final long timestamp;

	public KiraPhoneScanResult(String scannerId, String macAddress, boolean isAvailable,
			long timestamp) {
		this.scannerId = scannerId;
		this.macAddress = macAddress;
		this.isAvailable = isAvailable;
		this.timestamp = timestamp;
	}

	public KiraPhoneScanResult(String kiraPhoneScanResultAsString) {
		String[] elements = kiraPhoneScanResultAsString.split(";");
		scannerId = elements[0];
		macAddress = elements[1];
		isAvailable = Boolean.parseBoolean(elements[2]);
		timestamp = Long.parseLong(elements[3]);
	}

	@Override
	public String toString() {
		return scannerId + ";" + macAddress + ";" + isAvailable + ";" + timestamp;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public String getMacAddress() {
		return this.macAddress;
	}

	public String getScannerId() {
		return this.scannerId;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

}
