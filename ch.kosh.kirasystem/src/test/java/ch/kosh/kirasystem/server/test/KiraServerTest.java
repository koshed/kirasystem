package ch.kosh.kirasystem.server.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import ch.kosh.kirasystem.KiraConstants;
import ch.kosh.kirasystem.KiraPhoneScanResult;
import ch.kosh.kirasystem.Phone;
import ch.kosh.kirasystem.PhoneList;
import ch.kosh.kirasystem.server.IKiraPhoneScanResultCallback.SwitchIds;
import ch.kosh.kirasystem.server.KiraServer;
import ch.kosh.kirasystem.server.SwitchState;
import ch.kosh.kirasystem.server.SwitchStatus;
import ch.kosh.kirasystem.server.SwitchWlanPowerController;

public class KiraServerTest {

	@Test
	public void testSwitchToAvailable() throws IOException, InterruptedException {
		PhoneList phoneList = new PhoneList();
		KiraServer server = new KiraServer(phoneList, null, null);
		KiraPhoneScanResult kiraPhoneScanResultMock = mock(KiraPhoneScanResult.class);
		when(kiraPhoneScanResultMock.isAvailable()).thenReturn(true);
		when(kiraPhoneScanResultMock.getTimestamp()).thenReturn(System.currentTimeMillis() + 200);
		when(kiraPhoneScanResultMock.getMacAddress()).thenReturn(KiraConstants.macAddressMark);
		server.gotResultUpdate(kiraPhoneScanResultMock);
		assertEquals("Phone is now available", true,
				server.getPhoneList().isAvailable(KiraConstants.macAddressMark, 0));
	}

	@Test
	public void testGetPhoneList() throws IOException, InterruptedException {
		PhoneList phoneList = mock(PhoneList.class);
		KiraServer server = new KiraServer(phoneList, null, null);
		KiraPhoneScanResult kiraPhoneScanResultMock = mock(KiraPhoneScanResult.class);
		when(kiraPhoneScanResultMock.isAvailable()).thenReturn(true);
		when(kiraPhoneScanResultMock.getTimestamp()).thenReturn(System.currentTimeMillis() + 200);
		when(kiraPhoneScanResultMock.getMacAddress()).thenReturn(KiraConstants.macAddressMark);
		server.gotResultUpdate(kiraPhoneScanResultMock);
		assertEquals("ScannerAddedToScannerList", 1, server.getPhoneScannerList().getPhoneScanners().size());
	}

	// unit test prüft shell command -> falsch!
	@Ignore
	@Test
	public void testupdateCamState() {
		PhoneList phoneListMock = mock(PhoneList.class);
		when(phoneListMock.isAvailable(KiraConstants.macAddressMark, 500)).thenReturn(true);
		when(phoneListMock.getLastSeenPhone()).thenReturn(new Phone(null, ""));
		SwitchWlanPowerController switchMock = mock(SwitchWlanPowerController.class);
		SwitchStatus camStatus = new SwitchStatus(switchMock);
		KiraServer ks = new KiraServer(phoneListMock, camStatus, null);
		assertEquals(SwitchState.UNKNOWN, ks.getSwitchStatus(SwitchIds.CAMPOWERSWITCH_ID).getSwitchState());
		ks.updateToAwayTimeOfPhones(500);
		assertEquals(SwitchState.OFF, ks.getSwitchStatus(SwitchIds.CAMPOWERSWITCH_ID).getSwitchState());
		ks.updateToAwayTimeOfPhones(99887766);
		assertEquals(SwitchState.ON, ks.getSwitchStatus(SwitchIds.CAMPOWERSWITCH_ID).getSwitchState());
	}

	@Test
	public void testupdateCamStateReason() {
		PhoneList phoneListMock = mock(PhoneList.class);
		when(phoneListMock.isAvailable(KiraConstants.macAddressMark, 500)).thenReturn(true);
		when(phoneListMock.getLastSeenPhone()).thenReturn(new Phone(null, "Zedd"));
		SwitchWlanPowerController switchMock = mock(SwitchWlanPowerController.class);
		SwitchStatus camStatus = new SwitchStatus(switchMock);
		KiraServer ks = new KiraServer(phoneListMock, camStatus, null);
		assertEquals(SwitchState.UNKNOWN, ks.getSwitchStatus(SwitchIds.CAMPOWERSWITCH_ID).getSwitchState());
		// ks.updateCamState(500);
		// assertEquals("Zedd got home.",
		// ks.getSwitchStatus(SwitchIds.CAMPOWERSWITCH_ID).getSwitchStateLog().get(0)
		// .getLastSwitchReason());
	}

	@Test
	public void testgetPhoneScannerList() {
		PhoneList phoneList = mock(PhoneList.class);
		SwitchStatus camStatus = mock(SwitchStatus.class);
		KiraServer server = new KiraServer(phoneList, camStatus, null);
		server.getPhoneScannerList();
	}
}
