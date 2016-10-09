package ch.kosh.kirasystem.server;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import ch.kosh.kirasystem.KiraConstants;
import ch.kosh.kirasystem.KiraPhoneScanResult;

public class MQTTReceiveClient implements MqttCallback {
	private static final Logger log4j = LogManager.getLogger();

	private MqttAsyncClient client;

	private boolean startingUp;

	private String topic;

	private IKiraPhoneScanResultCallback callback;

	public boolean startup(String topic, String url, String serverId) {
		this.topic = topic;
		try {
			log4j.debug("Starting MQTT clientid=" + serverId);
			client = new MqttAsyncClient(url, serverId, new MemoryPersistence());
			client.setCallback(this);
			startingUp = true;

			connectAndSubscribe();

			return true;
		} catch (MqttException | InterruptedException e) {
			log4j.error(e);
		}
		return false;
	}

	private void connectAndSubscribe() throws InterruptedException {
		startConnect();
		waitStartUp();
		log4j.debug("connected successful");

		startSubscriber();
		waitStartUp();
		log4j.debug("subscribed successful");
	}

	private void startSubscriber() {
		startingUp = true;
		int qos = 2;
		log4j.debug("Subscribing to topic \"" + topic + "\" qos " + qos);

		IMqttActionListener subListener = new IMqttActionListener() {
			@Override
			public void onSuccess(IMqttToken asyncActionToken) {
				log4j.debug("Subscribe Completed");
				startingUp = false;
			}

			@Override
			public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
				log4j.error("Subscribe failed" + exception);
				startingUp = false;
			}
		};

		try {
			client.subscribe(topic, qos, "Subscribe kira context", subListener);
		} catch (MqttException e) {
			log4j.error(e);
			startingUp = false;
		}
	}

	private void startConnect() {
		IMqttActionListener conListener = new IMqttActionListener() {

			@Override
			public void onSuccess(IMqttToken asyncActionToken) {
				log4j.debug("Connected");
				startingUp = false;
				// carryOn();
			}

			@Override
			public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
				log4j.debug("connect failed: " + exception);
				startingUp = false;
				// carryOn();
			}
		};

		try {
			client.connect("Connect kira context", conListener);
		} catch (MqttException e) {
			log4j.error(e);
		}
	}

	private void waitStartUp() throws InterruptedException {
		while (startingUp) {
			Thread.sleep(300);
		}
	}

	public void setCallback(IKiraPhoneScanResultCallback callback) {
		this.callback = callback;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		log4j.error("Connection lost: " + arg0.getMessage());
		log4j.error(arg0);
		try {
			Thread.sleep(1000);
			connectAndSubscribe();
		} catch (InterruptedException e) {
			log4j.error(e);
		}

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		log4j.warn("deliveryComplete in a server??");
		log4j.warn(arg0);
	}

	@Override
	public void messageArrived(String arg0, MqttMessage message) throws Exception {
		byte[] payload = message.getPayload();
		String messageText = new String(payload);
		log4j.trace("Got Message: " + messageText, "UTF-8");
		KiraPhoneScanResult kiraPhoneScanResult = new KiraPhoneScanResult(messageText);
		this.callback.gotResultUpdate(kiraPhoneScanResult);
	}

	public static String generateMqttId() {
		return UUID.randomUUID().toString().substring(0, KiraConstants.mqttClientIdMaxLength);
	}
}
