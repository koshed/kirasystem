package ch.kosh.kirasystem;

import java.util.ArrayList;
import java.util.Date;

public class PhoneList {
	private ArrayList<Phone> phones;

	public PhoneList() {
		this.phones = new ArrayList<Phone>();
		Phone markPhoneOld = new Phone(KiraConstants.macAddressMarkOld, "ZeddBefore");
		phones.add(markPhoneOld);
		Phone markPhone = new Phone(KiraConstants.macAddressMark, "Zedd");
		phones.add(markPhone);
		Phone helenePhone = new Phone(KiraConstants.heleneiPhone6Address, "Kahlan");
		phones.add(helenePhone);
	}

	public boolean isAvailable(String macAddress, long now) {
		for (Phone phone : phones) {
			if (phone.getMacAddress().equals(macAddress)) {
				return phone.isAvailable(now);
			}
		}
		throw new RuntimeException("macAddress unknown");
	}

	public void setLastSeen(String macAddress, long timestamp) {
		for (Phone phone : phones) {
			if (phone.getMacAddress().equals(macAddress)) {
				if (phone.getLastAvailableTimestamp() < timestamp)
					phone.setLastAvailableTimestamp(timestamp);
				return;
			}
		}
	}

	public long getLastSeenTimestamp(String macAddress) {
		for (Phone phone : phones) {
			if (phone.getMacAddress().equals(macAddress)) {
				return phone.getLastAvailableTimestamp();
			}
		}
		throw new RuntimeException("macAddress unknown");
	}

	public Date getLastSeenTimestampAsDate(String macAddress) {
		return new Date(getLastSeenTimestamp(macAddress));
	}

	public Phone getLastSeenPhone() {
		long newest = -1;
		Phone returner = null;
		for (Phone phone : phones) {
			if (phone.getLastAvailableTimestamp() > newest) {
				returner = phone;
				newest = phone.getLastAvailableTimestamp();
			}

		}
		return returner;
	}
}
