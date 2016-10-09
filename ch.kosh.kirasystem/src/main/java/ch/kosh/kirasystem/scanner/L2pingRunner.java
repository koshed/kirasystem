package ch.kosh.kirasystem.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L2pingRunner implements IL2PingRunner {
	private static final Logger log4j = LogManager.getLogger();

	@Override
	public boolean pingBTAddress(String macAddress) throws IOException, InterruptedException {
		String output = pingAdress(macAddress);
		int indexOfReceivedText = output.indexOf("1 received");
		return indexOfReceivedText > 0;
	}

	private String pingAdress(String adress) throws IOException,
			InterruptedException {
		String command = "sudo l2ping -c 1 " + adress;
		return runExternalShellCommand(command);
	}

	public static String runExternalShellCommand(String command) throws IOException, InterruptedException {
		String lineFeedback = "";
		String line;

		Process p = Runtime.getRuntime().exec(command);
		
		BufferedReader bri = new BufferedReader
		        (new InputStreamReader(p.getInputStream()));
		BufferedReader bre = new BufferedReader
		    (new InputStreamReader(p.getErrorStream()));
		while ((line = bri.readLine()) != null) {
			lineFeedback += line;
		    log4j.trace(line);
		}
		bri.close();
		while ((line = bre.readLine()) != null) {
			lineFeedback += line;
			log4j.trace(line);
		}
		bre.close();
		p.waitFor();
		log4j.trace("Done.");
		p.destroy();
		
		return lineFeedback;
	}
	
	
}
