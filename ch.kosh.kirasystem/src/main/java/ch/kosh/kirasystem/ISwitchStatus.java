package ch.kosh.kirasystem;

import java.util.List;

import ch.kosh.kirasystem.server.SwitchState;
import ch.kosh.kirasystem.server.SwitchStateLogEntry;

public interface ISwitchStatus {
	public static final String CAM_STATUS = "Cam status: ";

	public SwitchState getSwitchState();

	public long getLastCheckTimestamp();

	public long getLastSwitchTimestamp();

	public List<SwitchStateLogEntry> getSwitchStateLog();

	void setSwitchState(SwitchState newState, String reasonOfChange);

	public void updateSwitchStatus();
}
