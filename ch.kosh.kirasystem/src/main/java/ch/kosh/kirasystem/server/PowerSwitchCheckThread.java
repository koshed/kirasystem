package ch.kosh.kirasystem.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.kosh.kirasystem.server.IKiraPhoneScanResultCallback.SwitchIds;

public class PowerSwitchCheckThread implements Runnable {
	private static final Logger log4j = LogManager.getLogger();
	private IKiraPhoneScanResultCallback callback;
	private SwitchIds id;

	public PowerSwitchCheckThread(IKiraPhoneScanResultCallback callback, SwitchIds id) {
		this.callback = callback;
		this.id = id;
	}

	private static final long UPDATE_INTERVALL = 500;

	@Override
	public void run() {
		for (;;) {
			try {
				Thread.sleep(UPDATE_INTERVALL);
				callback.updatePowerSwitch(
						id,
						System.currentTimeMillis());
			} catch (InterruptedException e) {
				log4j.error(e);
			}
		}
	}

}
