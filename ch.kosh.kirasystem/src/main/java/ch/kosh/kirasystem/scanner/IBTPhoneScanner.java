package ch.kosh.kirasystem.scanner;

public interface IBTPhoneScanner {

	public abstract void setL2pinger(IL2PingRunner l2pinger);

	public abstract void setMqttClient(MQTTSendClient mqttClient);

	public abstract void scanAndSendResult(String macAddress);

}