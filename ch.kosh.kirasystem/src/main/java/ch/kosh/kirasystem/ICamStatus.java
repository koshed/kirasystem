package ch.kosh.kirasystem;

import java.util.List;

import ch.kosh.kirasystem.server.CamStateLogEntry;
import ch.kosh.kirasystem.server.CamStateLogEntry.CamState;

public interface ICamStatus {
	public static final String CAM_STATUS = "Cam status: ";

	public CamState getCamState();

	public long getLastCheckTimestamp();

	public long getLastSwitchTimestamp();

	public List<CamStateLogEntry> getCamStateLog();

	void setCamState(CamState newState, String reasonOfChange);

	public void updateCamStatus();

}
