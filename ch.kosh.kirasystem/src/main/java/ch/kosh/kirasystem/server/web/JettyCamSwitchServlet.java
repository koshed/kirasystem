package ch.kosh.kirasystem.server.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.kosh.kirasystem.KiraConstants;
import ch.kosh.kirasystem.server.IKiraPhoneScanResultCallback;
import ch.kosh.kirasystem.server.IKiraPhoneScanResultCallback.SwitchIds;
import ch.kosh.kirasystem.server.SwitchState;

public class JettyCamSwitchServlet extends HttpServlet {

	private static final long serialVersionUID = 8598223976350836809L;

	private IKiraPhoneScanResultCallback callbackServer;

	private SwitchState newState;

	private String reasonOfChange;

	public JettyCamSwitchServlet(IKiraPhoneScanResultCallback callbackServer, SwitchState newState,
			String reasonOfChange) {
		this.callbackServer = callbackServer;
		this.newState = newState;
		this.reasonOfChange = reasonOfChange;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter()
				.println("<html><head>"
						+ JettyWebServerHandler.getHTMLRefreshString(KiraConstants.WEB_AFTER_SWITCHED_WAITING_SECONDS)
						+ "</head>");
		response.getWriter().println("<body><font size=\"8\"><b>Media Center wird geschaltet...</b><br/>");
		response.getWriter().println("<br/>Zielzustand: " + newState);
		callbackServer.setPowerSwitch(SwitchIds.POWERSWITCH_2_ID, newState, reasonOfChange);
		response.getWriter().println(
				"<br/>Neuer Zustand: " + callbackServer.getSwitchStatus(SwitchIds.POWERSWITCH_2_ID).getSwitchState());
		response.getWriter().println("</font></body>");
	}

}
