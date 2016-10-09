package ch.kosh.kirasystem.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import ch.kosh.kirasystem.server.PhoneScanner;
import ch.kosh.kirasystem.server.PhoneScannerList;

public class PhoneScannerListTest {
	@Test
	public void testupdateScanner() {
		PhoneScannerList list = new PhoneScannerList();
		list.updateScanner("test", 500);
		assertEquals(500, list.getPhoneScanners().get(0).getLastSeenTimestamp());
		list.updateScanner("test", 1000);
		assertEquals(1000, list.getPhoneScanners().get(0).getLastSeenTimestamp());
		list.updateScanner("test2", 400);
		assertEquals(2, list.getPhoneScanners().size());
	}
	
	@Test
	public void test_getNewestDevice() {
		PhoneScannerList list = new PhoneScannerList();
		list.updateScanner("an old one", 10);
		list.updateScanner("the new", 50000);
		list.updateScanner("an other one", 1001);
		list.updateScanner("the not newest", 40000);

		assertEquals("the new", list.getNewestDevice().getDeviceId());
	}
	
	@Test
	public void test_getOldestLastSeenDevice() {
		PhoneScannerList list = new PhoneScannerList();
		list.updateScanner("an old one", 10);
		list.updateScanner("the new", 50000);
		for (int i = 0; i < 200; i++) {
			list.updateScanner("test" + i, i);
		}
		list.updateScanner("an other one", 11);
		list.updateScanner("the not newest", 40000);
		assertEquals(4, list.getPhoneScanners().size());
		for (PhoneScanner scanner : list.getPhoneScanners()) {
			assertNotEquals("an old one", scanner.getDeviceId());
			assertNotEquals("an old one", scanner.getDeviceId());
			assertNotEquals("test11", scanner.getDeviceId());
		}
		assertEquals("the new", list.getNewestDevice().getDeviceId());
	}

	@Test
	public void testMaxPhoneListSize() {
		PhoneScannerList list = new PhoneScannerList();
		list.updateScanner("an old one", 10);
		list.updateScanner("the new", 50000);
		for (int i = 0; i < 200; i++) {
			list.updateScanner("test" + i, i);
		}
		assertEquals(4, list.getPhoneScanners().size());
		for (PhoneScanner scanner : list.getPhoneScanners()) {
			assertNotEquals("an old one", scanner.getDeviceId());
		}
		assertEquals("the new", list.getNewestDevice().getDeviceId());
	}
}