package ch.kosh.kirasystem.server.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ch.kosh.kirasystem.ISwitchStatus;
import ch.kosh.kirasystem.server.SwitchState;
import ch.kosh.kirasystem.server.SwitchStateLogEntry;
import ch.kosh.kirasystem.server.SwitchStatus;
import ch.kosh.kirasystem.server.SwitchWlanPowerController;

public class SwitchStatusTest {

	@Ignore
	@Test(expected = IOException.class)
	public void testCheckCamIOException() throws IOException,
			InterruptedException {
		SwitchWlanPowerController con = new SwitchWlanPowerController(null);
		con.checkIfSwitchIsAvailable();
	}

	@Test
	public void testStateLogSize() throws IOException, InterruptedException {
		SwitchWlanPowerController switchMock = mock(SwitchWlanPowerController.class);
		when(switchMock.checkIfSwitchIsAvailable()).thenReturn(true);
		ISwitchStatus cam = new SwitchStatus(switchMock);
		cam.setSwitchState(SwitchState.ON, "");
		cam.setSwitchState(SwitchState.OFF, "");
		List<SwitchStateLogEntry> SwitchStateLog = cam.getSwitchStateLog();
		assertEquals("2 changes => 0 curl success => 0 entries", 2,
				SwitchStateLog.size());
	}

	@Test
	public void testupdateCamStatus() throws IOException, InterruptedException {
		SwitchWlanPowerController switchMock = mock(SwitchWlanPowerController.class);
		when(switchMock.checkIfSwitchIsAvailable()).thenReturn(true);
		when(switchMock.switchPower(true)).thenReturn(true);
		ISwitchStatus cam = new SwitchStatus(switchMock);
		cam.updateSwitchStatus();
		assertEquals(SwitchState.ON, cam.getSwitchState());
	}

	@Test
	public void testgetLastSwitchReason() throws IOException,
			InterruptedException {
		SwitchWlanPowerController switchMock = mock(SwitchWlanPowerController.class);
		when(switchMock.switchPower(true)).thenReturn(true);
		ISwitchStatus cam = new SwitchStatus(switchMock);
		String testobject = "tested reason";
		cam.setSwitchState(SwitchState.ON, testobject);
		assertEquals("Expected Zedd as Reason", testobject, cam
				.getSwitchStateLog().get(0).getLastSwitchReason());
	}

}
