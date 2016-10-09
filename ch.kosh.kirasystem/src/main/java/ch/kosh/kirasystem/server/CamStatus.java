package ch.kosh.kirasystem.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.kosh.kirasystem.ICamStatus;
import ch.kosh.kirasystem.server.CamStateLogEntry.CamState;

public class CamStatus implements ICamStatus {
	private static final Logger log4j = LogManager.getLogger();

	public static final long CHECK_STATUS_INTERVALL = 5 * 60 * 1000;

	private SwitchWlanPowerController camPowerSwitch;

	private long lastCheckTimestamp;
	private long lastSwitchTimestamp;
	private CamState currentCamState;

	private List<CamStateLogEntry> camStateLogEntryList;

	public CamStatus(SwitchWlanPowerController switchWlanPowerController) {
		this.currentCamState = CamState.UNKNOWN;
		this.lastCheckTimestamp = 0;
		this.lastSwitchTimestamp = 0;
		this.camStateLogEntryList = new ArrayList<CamStateLogEntry>();
		this.camPowerSwitch = switchWlanPowerController;
		// updateCamStatus();
	}

	@Override
	public CamState getCamState() {
		return this.currentCamState;
	}

	@Override
	public void setCamState(CamState newState, String reasonOfChange) {
		if (this.currentCamState != newState) {
			try {
				camPowerSwitch.switchPower(newState == CamState.ON);

				this.currentCamState = newState;
				this.lastSwitchTimestamp = System.currentTimeMillis();

				addEventToEventLogList(newState, reasonOfChange);

			} catch (IOException | InterruptedException e) {
				log4j.warn("Switching state failed");
				log4j.error(e);
			}
		}
	}

	private void addEventToEventLogList(CamState newState, String reasonOfChange) {
		this.camStateLogEntryList.add(new CamStateLogEntry(newState, lastSwitchTimestamp,
				reasonOfChange));
		while (this.camStateLogEntryList.size() > 100)
			this.camStateLogEntryList.remove(0);
	}

	/*
	 * check state
	 */
	@Override
	public void updateCamStatus() {
		try {
			boolean newState = camPowerSwitch.checkIfSwitchIsAvailable();
			CamState newCamState = CamState.OFF;
			if (newState)
				newCamState = CamState.ON;
			this.lastCheckTimestamp = System.currentTimeMillis();
			setCamState(newCamState, "New cam state detected");
		} catch (IOException | InterruptedException e) {
			log4j.warn("check cam failed");
			log4j.error(e);
		}
	}

	@Override
	public long getLastCheckTimestamp() {
		return this.lastCheckTimestamp;
	}

	@Override
	public long getLastSwitchTimestamp() {
		return this.lastSwitchTimestamp;
	}

	@Override
	public List<CamStateLogEntry> getCamStateLog() {
		return camStateLogEntryList;
	}

	public CamState getCurrentCamState() {
		return currentCamState;
	}

	public void setCurrentCamState(CamState currentCamState) {
		this.currentCamState = currentCamState;
	}

}
