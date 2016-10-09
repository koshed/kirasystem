package ch.kosh.kirasystem.server;

import ch.kosh.kirasystem.ISwitchStatus;
import ch.kosh.kirasystem.KiraPhoneScanResult;
import ch.kosh.kirasystem.PhoneList;

public interface IKiraPhoneScanResultCallback {
	public enum SwitchIds {
		CAMPOWERSWITCH_ID, POWERSWITCH_2_ID
	};

	void gotResultUpdate(KiraPhoneScanResult kiraPhoneScanResult);

	ISwitchStatus getSwitchStatus(SwitchIds switchId);

	PhoneList getPhoneList();

	// void updateCamState(long now);
	void updatePowerSwitch(SwitchIds switchId, long now);

	PhoneScannerList getPhoneScannerList();

	void setPowerSwitch(SwitchIds switchId, SwitchState on,
			String reasonOfChange);
}
