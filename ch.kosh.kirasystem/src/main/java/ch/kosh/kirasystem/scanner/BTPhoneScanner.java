package ch.kosh.kirasystem.scanner;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.kosh.kirasystem.KiraPhoneScanResult;

public class BTPhoneScanner implements IBTPhoneScanner {

	private static final Logger log4j = LogManager.getLogger();
	private IL2PingRunner l2pinger;
	private MQTTSendClient mqttClient;

	public boolean scanMacAddress(String macAddress) {
		try {
			return this.l2pinger.pingBTAddress(macAddress);
		} catch (IOException | InterruptedException e) {
			log4j.error(e);
		}
		return false;
	}

	@Override
	public void setL2pinger(IL2PingRunner l2pinger) {
		this.l2pinger = l2pinger;
	}

	@Override
	public void setMqttClient(MQTTSendClient mqttClient) {
		this.mqttClient = mqttClient;
	}

	public void sendResult(boolean result, String macAddress, long timestamp) {
		KiraPhoneScanResult resultTO = new KiraPhoneScanResult(mqttClient.client.getClientId(),
				macAddress, result, timestamp);
		this.mqttClient.send(resultTO.toString());
	}

	@Override
	public void scanAndSendResult(String macAddress) {
		long timestamp = System.currentTimeMillis();
		boolean result = scanMacAddress(macAddress);
		log4j.debug(timestamp + " - Scanning " + macAddress + ": " + result);
		sendResult(result, macAddress, timestamp);
	}
}
