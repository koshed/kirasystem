package ch.kosh.kirasystem.scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.kosh.kirasystem.KiraConstants;
import ch.kosh.kirasystem.server.MQTTReceiveClient;

public class BTPhoneScannerLauncher {
	private static final Logger log4j = LogManager.getLogger();
	private static int sleepTime = 30 * 1000;

	public void start(String args[]) {
		IBTPhoneScanner btscanner = new BTPhoneScanner();
		IL2PingRunner l2pinger = new L2pingRunner();
		btscanner.setL2pinger(l2pinger);
		MQTTSendClient mqttClient = new MQTTSendClient();
		String clientId = MQTTReceiveClient.generateMqttId();
		log4j.info("Starting mqtt client with id: " + clientId);
		mqttClient.startup(KiraConstants.topic, KiraConstants.url, clientId);
		btscanner.setMqttClient(mqttClient);

		if (args.length > 0 && !args[0].isEmpty()) {
			try {
				sleepTime = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		try {
			for (;;) {
				btscanner.scanAndSendResult(KiraConstants.macAddressMark);
				btscanner.scanAndSendResult(KiraConstants.heleneiPhone6Address);
//				btscanner.scanAndSendResult(KiraConstants.macAddressMarkOld);
				Thread.sleep(sleepTime);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			mqttClient.stop();
		}
	}

	public static void main(String args[]) {
		new BTPhoneScannerLauncher().start(args);
	}

}
