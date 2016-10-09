package ch.kosh.kirasystem.server;

import java.util.ArrayList;
import java.util.List;

import ch.kosh.kirasystem.KiraConstants;

public class PhoneScannerList {

	private List<PhoneScanner> knownDevices;

	public PhoneScannerList() {
		this.knownDevices = new ArrayList<PhoneScanner>();
	}

	public List<PhoneScanner> getPhoneScanners() {
		return knownDevices;
	}

	public void updateScanner(String scannerId, long timestamp) {
		// check if device known
		for (PhoneScanner scanner : knownDevices) {
			if (scanner.getDeviceId().equals(scannerId)) {
				scanner.setLastSeenTimestamp(timestamp);
				return;
			}
		}
		// add new device
		PhoneScanner newScanner = new PhoneScanner(scannerId);
		newScanner.setLastSeenTimestamp(timestamp);
		addNewScanner(newScanner);
	}

	private void addNewScanner(PhoneScanner newScanner) {
		while (knownDevices.size() > KiraConstants.MAX_SCANNER_COUNT) {
			knownDevices.remove(getOldestLastSeenDevice());
			knownDevices.remove(getOldestLastSeenDevice());
		}
		this.knownDevices.add(newScanner);
	}
	
	public PhoneScanner getOldestLastSeenDevice() {
		PhoneScanner returner = null;
		long oldest = Long.MAX_VALUE;
		for (PhoneScanner current : knownDevices) {
			if (current.getLastSeenTimestamp() <= oldest) {
				returner = current;
				oldest = returner.getLastSeenTimestamp();
			}
		}
		return returner;
	}

	public PhoneScanner getNewestDevice() {
		PhoneScanner returner = null;
		long newest = -1;
		for (PhoneScanner current : knownDevices) {
			if (current.getLastSeenTimestamp() > newest) {
				returner = current;
				newest = returner.getLastSeenTimestamp();
			}
		}
		return returner;
	}
}
