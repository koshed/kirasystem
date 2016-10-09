package ch.kosh.kirasystem.server.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ch.kosh.kirasystem.ICamStatus;
import ch.kosh.kirasystem.server.CamStateLogEntry;
import ch.kosh.kirasystem.server.CamStateLogEntry.CamState;
import ch.kosh.kirasystem.server.CamStatus;
import ch.kosh.kirasystem.server.SwitchWlanPowerController;

public class CamStatusTest {

	@Ignore
	@Test(expected = IOException.class)
	public void testCheckCamIOException() throws IOException, InterruptedException {
		SwitchWlanPowerController con = new SwitchWlanPowerController(null);
		con.checkIfSwitchIsAvailable();
	}

	@Test
	public void testStateLogSize() throws IOException, InterruptedException {
		SwitchWlanPowerController switchMock = mock(SwitchWlanPowerController.class);
		when(switchMock.checkIfSwitchIsAvailable()).thenReturn(true);
		ICamStatus cam = new CamStatus(switchMock);
		cam.setCamState(CamState.ON, "");
		cam.setCamState(CamState.OFF, "");
		List<CamStateLogEntry> camStateLog = cam.getCamStateLog();
		assertEquals("2 changes => 0 curl success => 0 entries", 2, camStateLog.size());
	}

	@Test
	public void testupdateCamStatus() throws IOException, InterruptedException {
		SwitchWlanPowerController switchMock = mock(SwitchWlanPowerController.class);
		when(switchMock.checkIfSwitchIsAvailable()).thenReturn(true);
		CamStatus cam = new CamStatus(switchMock);
		cam.updateCamStatus();
		assertEquals(CamState.ON, cam.getCamState());
	}

	@Test
	public void testgetLastSwitchReason() throws IOException, InterruptedException {
		SwitchWlanPowerController switchMock = mock(SwitchWlanPowerController.class);
		when(switchMock.checkIfSwitchIsAvailable()).thenReturn(true);
		ICamStatus cam = new CamStatus(switchMock);
		String testobject = "tested reason";
		cam.setCamState(CamState.ON, testobject);
		assertEquals("Expected Zedd as Reason", testobject, cam.getCamStateLog().get(0)
				.getLastSwitchReason());
	}

}
