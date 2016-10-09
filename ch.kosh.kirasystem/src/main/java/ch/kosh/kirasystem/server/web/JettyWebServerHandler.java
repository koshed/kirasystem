package ch.kosh.kirasystem.server.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import ch.kosh.kirasystem.ISwitchStatus;
import ch.kosh.kirasystem.KiraConstants;
import ch.kosh.kirasystem.PhoneList;
import ch.kosh.kirasystem.server.IKiraPhoneScanResultCallback;
import ch.kosh.kirasystem.server.IKiraPhoneScanResultCallback.SwitchIds;
import ch.kosh.kirasystem.server.PhoneScanner;
import ch.kosh.kirasystem.server.PhoneScannerList;
import ch.kosh.kirasystem.server.SwitchState;
import ch.kosh.kirasystem.server.SwitchStateLogEntry;

public class JettyWebServerHandler extends AbstractHandler {
	private static final Logger log4j = LogManager.getLogger();

	public static final String CAM_STATUS = "Cam Status: ";
	public static final String POWER2SWITCH_STATUS = "Media System: ";

	private IKiraPhoneScanResultCallback server;

	public JettyWebServerHandler(IKiraPhoneScanResultCallback server) {
		log4j.debug("setting up jetty handler");
		this.server = server;
	}

	private String printHTMLTitle() {
		StringBuilder s = new StringBuilder();
		s.append("<head>");
		s.append(getHTMLRefreshString(KiraConstants.WEB_RELOAD_SECONDS));
		s.append("<title>Kira Control System 0.2</title>");
		// TODO geht nicht
		s.append("<link rel=\"icon\" href=\"/pinguin.png\" type=\"image/png\"/>");
		s.append("<meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>");
		s.append("<meta name=\"mobile-web-app-capable\" content=\"yes\"/>");
		// TODO geht nicht
		s.append("<link rel=\"apple-touch-icon\" href=\"/static/kira.jpg\" type=\"image/jpg\"/>");
		// TODO geht nicht
		s.append("<link rel=\"touch-icon\" href=\"/static/kira.jpg\" type=\"image/jpg\"/>");
		s.append("</head>");
		return s.toString();
	}

	public static String getHTMLRefreshString(int seconds) {
		return "<meta http-equiv=\"refresh\" content=\"" + seconds + "; URL=. \">";
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		{
			long now = System.currentTimeMillis();
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			response.getWriter().println("<html>" + printHTMLTitle());
			response.getWriter().println("<body>");

			response.getWriter().println("<h1>Kira System</h1>");

			response.getWriter().println("<table border='1' width='100%'>");
			response.getWriter()
					.println(power2SwitchHTMLTable(server.getSwitchStatus(SwitchIds.POWERSWITCH_2_ID), now));
			response.getWriter().println(camLogHTMLTable(server.getSwitchStatus(SwitchIds.CAMPOWERSWITCH_ID)));
			response.getWriter().println(switchCamHTMLTable(server.getSwitchStatus(SwitchIds.CAMPOWERSWITCH_ID), now));
			response.getWriter().println(phoneScannerHTMLTable(server.getPhoneScannerList()));
			response.getWriter().println(phoneStatesHTMLTable(server.getPhoneList(), now));

			response.getWriter().println("</td></tr>");
			response.getWriter().println("</table>");

			response.getWriter().println("</body></html>");
		}
	}

	private String phoneScannerHTMLTable(PhoneScannerList phoneScannerList) {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr><td>Scanners:<br/>");
		List<PhoneScanner> scanners = phoneScannerList.getPhoneScanners();
		for (PhoneScanner scanner : scanners) {

			sb.append(scanner.getDeviceId());
			sb.append(": ");
			sb.append(formatTimestamp(scanner.getLastSeenTimestamp()));
			sb.append("<br/>");
		}
		sb.append("</td></tr>");
		return sb.toString();
	}

	private String formatTimestamp(long timestamp) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss z");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("CET"));
		return simpleDateFormat.format(new Date(timestamp));
	}

	private String camLogHTMLTable(ISwitchStatus camStatus) {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr><td><font size='6'><b>");
		List<SwitchStateLogEntry> list = camStatus.getSwitchStateLog();
		for (int index = list.size() - 1; index >= 0 && index >= list.size() - 15; index--) {
			SwitchStateLogEntry entry = list.get(index);
			sb.append(formatTimestamp(entry.getTimestamp()));
			sb.append(": ");
			sb.append(entry.getSwitchState());
			sb.append(", ");
			sb.append(entry.getLastSwitchReason());
			sb.append("<br/>");
		}
		sb.append("<font size='6'></b></td></tr>");
		return sb.toString();
	}

	private String phoneStatesHTMLTable(PhoneList phoneList, long now) {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr><td>");
		sb.append("Zedd: " + phoneList.isAvailable(KiraConstants.macAddressMark, now));
		long lastSeenTimestampMark = phoneList.getLastSeenTimestamp(KiraConstants.macAddressMark);
		long lastSeenTimestampHelene = phoneList.getLastSeenTimestamp(KiraConstants.heleneiPhone6Address);
		sb.append("<br/>Last seen: " + formatTimestamp(lastSeenTimestampMark));
		sb.append(" (" + printDiffTime(now - lastSeenTimestampMark));
		sb.append(")<br/>");
		sb.append("Kahlan: " + phoneList.isAvailable(KiraConstants.heleneiPhone6Address, now));
		sb.append("<br/>Last seen: " + formatTimestamp(lastSeenTimestampHelene));
		sb.append(" (" + printDiffTime(now - lastSeenTimestampHelene));
		sb.append(")</td></tr>");
		return sb.toString();
	}

	private String printDiffTime(long diffms) {
		return (diffms / 1000) + "s";
	}

	private String switchCamHTMLTable(ISwitchStatus camStatus, long now) {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr><td>");
		sb.append(CAM_STATUS);
		sb.append(camStatus.getSwitchState());
		sb.append("<br/>Last check: " + printDiffTime(now - camStatus.getLastCheckTimestamp()));
		sb.append("<br/>Last switched: " + printDiffTime(now - camStatus.getLastSwitchTimestamp()));
		sb.append("</td></tr>");
		return sb.toString();
	}

	private String power2SwitchHTMLTable(ISwitchStatus mediaStatus, long now) {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr><td><font size='7'>");
		sb.append(POWER2SWITCH_STATUS);
		SwitchState switchState = mediaStatus.getSwitchState();
		// sb.append(switchState);
		if (switchState != SwitchState.ON) {
			sb.append(
					"<button style='font-size:60px; background-color: #F78181' onclick=\"window.location.href='/mediaswitch/on'\">Einschalten</button>");
		} else {
			sb.append(
					"<button style='font-size:60px; background-color: #81F781' onclick=\"window.location.href='/mediaswitch/off'\">Ausschalten</button>");
		}
		sb.append("</font><br/>Last check: " + printDiffTime(now - mediaStatus.getLastCheckTimestamp()));
		sb.append("<br/>Last switched: " + printDiffTime(now - mediaStatus.getLastSwitchTimestamp()));
		sb.append("</td></tr>");
		return sb.toString();
	}
}
