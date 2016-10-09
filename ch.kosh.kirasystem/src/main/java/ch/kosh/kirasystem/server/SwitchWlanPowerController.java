package ch.kosh.kirasystem.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;

public class SwitchWlanPowerController {
	public static final String xmlCheck = "<SMARTPLUG id=";

	private static final Logger log4j = LogManager
			.getLogger();

	private String camIp = null;

	private final String URLpartOne = "http://admin:52e5@10.10.10.";
	private final String URLpartTwo = ":10000/smartplug.cgi";
	private final String URLWithoutCredpartOne = "http://10.10.10.";
	private final String URLWithoutCredpartTwo = ":10000/smartplug.cgi";

	public SwitchWlanPowerController(String ip) {
		this.camIp = ip;
	}

	public boolean switchPower(boolean turnOn) throws IOException,
			InterruptedException {
		final String searchString = "OK";
		String lineFeedback = sendEdimaxSwitchHtmlCommand(turnOn);
		if (lineFeedback != null && lineFeedback.contains(searchString)
				&& lineFeedback.contains(xmlCheck))
			return true;
		log4j.warn("expected " + searchString + " and " + xmlCheck
				+ " in Feedback failed: " + lineFeedback);
		return false;
	}

	public boolean checkIfSwitchIsAvailable() throws IOException,
			InterruptedException {
		String lineFeedback = sendEdimaxCheckHtmlCommand();
		log4j.info("check feedback: " + lineFeedback);
		if (lineFeedback != null && lineFeedback.contains("ON"))
			return true;
		return false;
	}

	private String sendEdimaxCheckHtmlCommand() throws IOException,
			InterruptedException {
		String lineFeedback = "";
		// String checkState =
		// "\"<?xml version='1.0' encoding='UTF8'?><SMARTPLUG id='edimax'><CMD id='get'><Device.System.Power.State></Device.System.Power.State></CMD></SMARTPLUG>\"";
		// Todo geht so ned, daher workaround via file
		// String command = "curl -d " + checkState
		// + " http://admin:52e5@10.10.10.56:10000/smartplug.cgi";
		String command = "curl -d @/home/pi/javaStuff/check.xml " + URLpartOne
				+ camIp + URLpartTwo;

		log4j.debug("execute: " + command);

		lineFeedback = executeCurlCommand(lineFeedback, command);
		return lineFeedback;
	}

	private String sendEdimaxSwitchHtmlCommand(boolean turnOn)
			throws IOException, InterruptedException {
		String lineFeedback = "";
		String offOn = "off";
		if (turnOn) {
			offOn = "on";
		}
		String command = "curl -d @/home/pi/javaStuff/" + offOn + ".xml "
				+ URLpartOne + camIp + URLpartTwo;

		lineFeedback = executeCurlCommand(lineFeedback, command);
		return lineFeedback;
	}

	private String executeCurlCommand(String lineFeedback, String command)
			throws IOException, InterruptedException {
		String line;
		Process p = Runtime.getRuntime().exec(command);

		BufferedReader bri = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		BufferedReader bre = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));
		while ((line = bri.readLine()) != null) {
			lineFeedback += line;
		}
		bri.close();
		lineFeedback += "\n";
		while ((line = bre.readLine()) != null) {
			lineFeedback += line;
		}
		bre.close();

		p.waitFor();
		p.destroyForcibly();
		return lineFeedback;
	}

	public void webrequest() throws Exception {
		HttpClient client = new HttpClient();
		// client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		client.start();

		// HttpDestination des = new HttpDestination(client, null, false, null);

		ContentExchange exchange = new ContentExchange(true);

		exchange.setURL(URLWithoutCredpartOne + camIp + URLWithoutCredpartTwo);
		exchange.setMethod("POST");
		client.send(exchange);

		// Waits until the exchange is terminated

		int exchangeState = exchange.waitForDone();
		log4j.info(exchangeState);
		// HttpExchange.STATUS_SENDING_PARSING_CONTENT

	}
}

/*
 * c# code: using System; using System.Collections.Generic; using System.Linq;
 * using System.Text; using System.Threading.Tasks; using System.IO; using
 * System.Net;
 * 
 * namespace Edimax.Smartplug { public class Smartplug { #region Private
 * properties private string host; private string uname; private string pw;
 * 
 * private static readonly Encoding encoding = Encoding.UTF8; private static
 * string postContent = "{1}"; #endregion
 * 
 * #region Public properties public bool On; #endregion
 * 
 * #region Constructor public Smartplug(string hostaddress, string username,
 * string password) { host = hostaddress; uname = username; pw = password;
 * 
 * GetDeviceState(); } #endregion
 * 
 * #region Public methods public bool SetDeviceOn() { bool result = false; if
 * (!On) result = SetDeviceState(true);
 * 
 * GetDeviceState();
 * 
 * return result; }
 * 
 * public bool SetDeviceOff() { bool result = false; if (On) result =
 * SetDeviceState(false);
 * 
 * GetDeviceState();
 * 
 * return result; } #endregion
 * 
 * #region Private methods private void GetDeviceState() { string result =
 * SendCommand("get", null);
 * 
 * if (result.Contains("OFF")) On = false; else if (result.Contains("ON")) On =
 * true; else throw new Exception("Unknown smartplug status"); }
 * 
 * private bool SetDeviceState(bool on) { bool result = false;
 * 
 * string reply = string.Empty; if (on) reply = SendCommand("setup", "ON"); else
 * reply = SendCommand("setup", "OFF");
 * 
 * if (reply.Contains("OK")) result = true;
 * 
 * return result; }
 * 
 * private string SendCommand(string cmd, string val) { string result =
 * string.Empty;
 * 
 * // Create web request and disable 100 continue
 * ServicePointManager.Expect100Continue = false; HttpWebRequest wr =
 * (HttpWebRequest)HttpWebRequest.Create(host); // Add credentials as basic
 * authentication string credentials =
 * Convert.ToBase64String(Encoding.ASCII.GetBytes(uname + ":" + pw));
 * wr.Headers[HttpRequestHeader.Authorization] = "Basic " + credentials;
 * 
 * // Add post values as multipart form data wr.Method = "POST"; string
 * formDataBoundary = String.Format("{0:N}", Guid.NewGuid()); wr.ContentType =
 * "multipart/form-data; boundary=" + formDataBoundary;
 * 
 * // Add post parameters Dictionary<string, object> postParameters = new
 * Dictionary<string, object>(); string pContent = string.Format(postContent,
 * cmd, val); byte[] prams = encoding.GetBytes(pContent);
 * postParameters.Add("file", new FileParameter(prams, "file")); byte[] formData
 * = GetMultipartFormData(postParameters, formDataBoundary);
 * 
 * // Write the postdata to the request wr.ContentLength = formData.Length;
 * using (Stream writer = wr.GetRequestStream()) writer.Write(formData, 0,
 * formData.Length);
 * 
 * // Perform the request and get data using (HttpWebResponse response =
 * (HttpWebResponse)wr.GetResponse()) { StreamReader rs = new
 * StreamReader(response.GetResponseStream()); result = rs.ReadToEnd(); }
 * 
 * return result; }
 * 
 * private static byte[] GetMultipartFormData(Dictionary<string, object>
 * postParameters, string boundary) { Stream formDataStream = new
 * System.IO.MemoryStream(); bool needsCLRF = false;
 * 
 * foreach (var param in postParameters) { // Thanks to feedback from
 * commenters, add a CRLF to allow multiple parameters to be added. // Skip it
 * on the first parameter, add it to subsequent parameters. if (needsCLRF)
 * formDataStream.Write(encoding.GetBytes("\r\n"), 0,
 * encoding.GetByteCount("\r\n"));
 * 
 * needsCLRF = true;
 * 
 * if (param.Value is FileParameter) { FileParameter fileToUpload =
 * (FileParameter)param.Value;
 * 
 * // Add just the first part of this param, since we will write the file data
 * directly to the Stream string header = string.Format(
 * "--{0}\r\nContent-Disposition: form-data; name=\"{1}\"; filename=\"{2}\"\r\n\r\n"
 * , boundary, param.Key, fileToUpload.FileName ?? param.Key);
 * 
 * formDataStream.Write(encoding.GetBytes(header), 0,
 * encoding.GetByteCount(header));
 * 
 * // Write the file data directly to the Stream, rather than serializing it to
 * a string. formDataStream.Write(fileToUpload.File, 0,
 * fileToUpload.File.Length); } else { string postData =
 * string.Format("--{0}\r\nContent-Disposition: form-data; name=\"{1}\"\r\n\r\n{2}"
 * , boundary, param.Key, param.Value);
 * formDataStream.Write(encoding.GetBytes(postData), 0,
 * encoding.GetByteCount(postData)); } }
 * 
 * // Add the end of the request. Start with a newline string footer = "\r\n--"
 * + boundary + "--\r\n"; formDataStream.Write(encoding.GetBytes(footer), 0,
 * encoding.GetByteCount(footer));
 * 
 * // Dump the Stream into a byte[] formDataStream.Position = 0; byte[] formData
 * = new byte[formDataStream.Length]; formDataStream.Read(formData, 0,
 * formData.Length); formDataStream.Close();
 * 
 * return formData; } #endregion } } string host =
 * "http://xxx.xxx.xxx.xxx:10000/smartplug.cgi"; // Ip address of the smartplug
 * string username = "username"; string pw = "password";
 * 
 * Smartplug plug = new Smartplug(host, username, pw); if (plug.On)
 * plug.SetDeviceOff(); else plug.SetDeviceOn();
 */

