package ch.kosh.kirasystem.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.kosh.kirasystem.ISwitchStatus;
import ch.kosh.kirasystem.KiraConstants;
import ch.kosh.kirasystem.KiraPhoneScanResult;
import ch.kosh.kirasystem.PhoneList;

public class KiraServer implements IKiraPhoneScanResultCallback {
	// private static long CAMPOWERSWITCH_ID = 1;
	// private static long POWERSWITCH_2_ID = 2;
	private ISwitchStatus camStatus;
	private PhoneScannerList phoneScannerList;
	private PhoneList phoneList;
	private ISwitchStatus power2Status;

	public KiraServer(PhoneList phoneList, ISwitchStatus camStatus,
			ISwitchStatus power2Status) {
		this.phoneList = phoneList;
		this.camStatus = camStatus;	
		this.power2Status = power2Status;
		this.phoneScannerList = new PhoneScannerList();
		log4j.info("KiraServer started.");
	}

	private static final Logger log4j = LogManager.getLogger();

	@Override
	public void gotResultUpdate(KiraPhoneScanResult kiraPhoneScanResult) {
		log4j.trace("Status update: " + kiraPhoneScanResult.toString());
		if (kiraPhoneScanResult.isAvailable()) {
			log4j.trace("Phone " + kiraPhoneScanResult.getMacAddress()
					+ " is here!");
			this.phoneList.setLastSeen(kiraPhoneScanResult.getMacAddress(),
					kiraPhoneScanResult.getTimestamp());
		}
		phoneScannerList.updateScanner("" + kiraPhoneScanResult.getScannerId(),
				kiraPhoneScanResult.getTimestamp());
	}

	@Override
	public ISwitchStatus getSwitchStatus(SwitchIds switchId) {
		switch (switchId) {
		case CAMPOWERSWITCH_ID:
			return this.camStatus;
		case POWERSWITCH_2_ID:
			return this.power2Status;
		}
		return null;
	}

	@Override
	public PhoneList getPhoneList() {
		return phoneList;
	}

	public void updateToAwayTimeOfPhones(long now) {
		String lastSeenOwnerName = phoneList.getLastSeenPhone().getOwnerName();
		boolean zeddIsHere = phoneList.isAvailable(KiraConstants.macAddressMark,
				now);
		boolean kahlanIsHere = phoneList.isAvailable(
				KiraConstants.heleneiPhone6Address, now);
		if (!zeddIsHere && !kahlanIsHere) {
			this.camStatus.setSwitchState(SwitchState.ON, lastSeenOwnerName
					+ " has also left.");
		} else {
			this.camStatus.setSwitchState(SwitchState.OFF, lastSeenOwnerName
					+ " got home.");
		}
	}

	@Override
	public PhoneScannerList getPhoneScannerList() {
		return this.phoneScannerList;
	}

	@Override
	public void setPowerSwitch(SwitchIds switchId, SwitchState newState,
			String reasonOfChange) {
		switch (switchId) {
		case CAMPOWERSWITCH_ID:
			this.camStatus.setSwitchState(newState, reasonOfChange);
			break;
		case POWERSWITCH_2_ID:
			this.power2Status.setSwitchState(newState, reasonOfChange);
			break;
		}
	}

	@Override
	public void updatePowerSwitch(SwitchIds switchId, long now) {
		switch (switchId) {
		case CAMPOWERSWITCH_ID:
			updateCamState(now);
			break;
		case POWERSWITCH_2_ID:
			updatePowerState(now);
			break;
		default:
			throw new RuntimeException("Unknown Switch Id: " + switchId);
		}

	}

	private void updatePowerState(long now) {
		long lastCheckTimestamp = power2Status.getLastCheckTimestamp();
		if (now - lastCheckTimestamp > SwitchStatus.CHECK_STATUS_INTERVALL) {
			this.power2Status.updateSwitchStatus();
		}
	}

	private void updateToCheckSwitchState(long now) {
		long lastCheckTimestamp = camStatus.getLastCheckTimestamp();
		if (now - lastCheckTimestamp > SwitchStatus.CHECK_STATUS_INTERVALL) {
			this.camStatus.updateSwitchStatus();
		}
	}

	private void updateCamState(long now) {
		updateToAwayTimeOfPhones(now);
		updateToCheckSwitchState(now);
	}
}
