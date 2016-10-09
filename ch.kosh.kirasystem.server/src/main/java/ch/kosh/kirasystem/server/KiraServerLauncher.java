package ch.kosh.kirasystem.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import ch.kosh.kirasystem.ISwitchStatus;
import ch.kosh.kirasystem.KiraConstants;
import ch.kosh.kirasystem.PhoneList;
import ch.kosh.kirasystem.server.IKiraPhoneScanResultCallback.SwitchIds;
import ch.kosh.kirasystem.server.web.JettyCamSwitchServlet;
import ch.kosh.kirasystem.server.web.JettyWebServerHandler;

public class KiraServerLauncher {

	private static final String MEDIASWITCH = "mediaswitch";

	public static void main(String[] args) throws Exception {
		PhoneList phoneList = new PhoneList();

		ISwitchStatus camStatus = new SwitchStatus(
				new SwitchWlanPowerController(KiraConstants.CAM_SWITCH_IP));
		ISwitchStatus power2Status = new SwitchStatus(
				new SwitchWlanPowerController(KiraConstants.MEDIA_SWITCH_IP));

		IKiraPhoneScanResultCallback server = new KiraServer(phoneList,
				camStatus, power2Status);

		Server webserver = new Server(8088);
		setupWebserver(server, webserver);

		PowerSwitchCheckThread camUpdate = new PowerSwitchCheckThread(server,
				SwitchIds.CAMPOWERSWITCH_ID);
		Thread camStatusUpdateThread = new Thread(camUpdate);
		camStatusUpdateThread.start();

		PowerSwitchCheckThread mediaSwitchUpdate = new PowerSwitchCheckThread(
				server, SwitchIds.POWERSWITCH_2_ID);
		Thread mediaSwitchUpdateThread = new Thread(mediaSwitchUpdate);
		mediaSwitchUpdateThread.start();

		MQTTReceiveClient mqttClient = new MQTTReceiveClient();
		String serverId = MQTTReceiveClient.generateMqttId();
		mqttClient.startup(KiraConstants.topic, KiraConstants.url, serverId);
		mqttClient.setCallback(server);

		webserver.join();

	}

	private static void setupWebserver(IKiraPhoneScanResultCallback server,
			Server webserver) throws Exception {

		ServletContextHandler contextCamSwitch = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		contextCamSwitch.setContextPath("/" + MEDIASWITCH);
		contextCamSwitch.setResourceBase(".");
		contextCamSwitch.setClassLoader(Thread.currentThread()
				.getContextClassLoader());
		String mediaSwitchreasonOfChange = "GET /" + MEDIASWITCH;
		contextCamSwitch.addServlet(new ServletHolder(
				new JettyCamSwitchServlet(server, SwitchState.ON,
						mediaSwitchreasonOfChange)), "/on/*");
		contextCamSwitch.addServlet(new ServletHolder(
				new JettyCamSwitchServlet(server, SwitchState.OFF,
						mediaSwitchreasonOfChange)), "/off/*");

		ContextHandler context = new ContextHandler();
		context.setContextPath("/");
		context.setResourceBase(".");
		context.setClassLoader(Thread.currentThread().getContextClassLoader());
		context.setHandler(new JettyWebServerHandler(server));

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { contextCamSwitch, context });

		webserver.setHandler(contexts);

		webserver.start();
	}
}
