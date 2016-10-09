package ch.kosh.kirasystem.scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTSendClient {
	private static final Logger log4j = LogManager.getLogger();
	MqttClient client;
	private String topic;

	public void startup(String topic, String url, String clientId) {
		this.topic = topic;
		try {
			log4j.info("Starting up MqttClient...");
			MqttConnectOptions conOpt = new MqttConnectOptions();
			conOpt.setCleanSession(false);

			log4j.debug("Starting MQTT clientid=" + clientId);
			client = new MqttClient(url, clientId, new MemoryPersistence());

			client.connect(conOpt);
			boolean connected = client.isConnected();
			log4j.debug(connected);

		} catch (MqttException e) {
			log4j.error(e);
			throw new RuntimeException(e);
		}
	}

	public void send(String message) {
		MqttMessage mqttMessage = new MqttMessage(message.getBytes());
		try {
			client.publish(topic, mqttMessage);
		} catch (MqttException e) {
			log4j.error(e);
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		try {
			client.disconnect();
			client.close();
		} catch (MqttException e) {
			log4j.error(e);
			throw new RuntimeException(e);
		}
	}

}
