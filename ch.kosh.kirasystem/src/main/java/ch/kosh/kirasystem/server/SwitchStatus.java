package ch.kosh.kirasystem.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.kosh.kirasystem.ISwitchStatus;

public class SwitchStatus implements ISwitchStatus {
	private static final Logger log4j = LogManager.getLogger();

	public static final long CHECK_STATUS_INTERVALL = 5 * 60 * 1000;

	private SwitchWlanPowerController powerSwitch;

	private long lastCheckTimestamp;
	private long lastSwitchTimestamp;
	private SwitchState currentSwitchState;

	public boolean ON = true;

	private List<SwitchStateLogEntry> switchStateLogEntryList;

	public SwitchStatus(SwitchWlanPowerController switchWlanPowerController) {
		this.currentSwitchState = SwitchState.UNKNOWN;
		this.lastCheckTimestamp = 0;
		this.lastSwitchTimestamp = 0;
		this.switchStateLogEntryList = new ArrayList<SwitchStateLogEntry>();
		this.powerSwitch = switchWlanPowerController;
	}

	@Override
	public SwitchState getSwitchState() {
		return this.currentSwitchState;
	}

	@Override
	public void setSwitchState(SwitchState newState, String reasonOfChange) {
		if (this.currentSwitchState != newState) {
			try {
				if (powerSwitch.switchPower(newState == SwitchState.ON)) {
					this.currentSwitchState = newState;
					addEventToEventLogList(currentSwitchState, reasonOfChange);
				} else {
					String errorMessage = "State switch failed! Device not reachable?";
					log4j.error(errorMessage);
					this.currentSwitchState = SwitchState.UNKNOWN;
					addEventToEventLogList(currentSwitchState, errorMessage);
				}
				this.lastSwitchTimestamp = System.currentTimeMillis();

			} catch (IOException | InterruptedException e) {
				log4j.warn("Switching state failed");
				log4j.error(e);
			}
		}
	}

	private void addEventToEventLogList(SwitchState newState,
			String reasonOfChange) {
		this.switchStateLogEntryList.add(new SwitchStateLogEntry(newState,
				lastSwitchTimestamp, reasonOfChange));
		while (this.switchStateLogEntryList.size() > 100)
			this.switchStateLogEntryList.remove(0);
	}

	/*
	 * check state
	 */
	@Override
	public void updateSwitchStatus() {
		try {
			boolean newState = powerSwitch.checkIfSwitchIsAvailable();
			SwitchState newSwitchState = SwitchState.OFF;
			if (newState)
				newSwitchState = SwitchState.ON;
			this.lastCheckTimestamp = System.currentTimeMillis();
			setSwitchState(newSwitchState, "New power switch state detected");
		} catch (IOException | InterruptedException e) {
			log4j.warn("check power switch failed");
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
	public List<SwitchStateLogEntry> getSwitchStateLog() {
		return switchStateLogEntryList;
	}

}