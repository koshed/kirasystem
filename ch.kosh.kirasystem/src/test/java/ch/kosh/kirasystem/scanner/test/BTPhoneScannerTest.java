package ch.kosh.kirasystem.scanner.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

import ch.kosh.kirasystem.scanner.BTPhoneScanner;
import ch.kosh.kirasystem.scanner.IL2PingRunner;

public class BTPhoneScannerTest {
	String macAddress = "11:22:33:44:55:66:77";

	@Test
	public void testScannerScanMacAddress() throws IOException, InterruptedException {
		BTPhoneScanner scanner = new BTPhoneScanner();

		IL2PingRunner l2pingerMock = mock(IL2PingRunner.class);
		when(l2pingerMock.pingBTAddress(macAddress)).thenReturn(true);
		scanner.setL2pinger(l2pingerMock);
		boolean result = scanner.scanMacAddress(macAddress);
		assertEquals("Ping successful", result, true);
	}
}