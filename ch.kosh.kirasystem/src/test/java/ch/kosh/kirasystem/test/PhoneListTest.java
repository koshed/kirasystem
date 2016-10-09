package ch.kosh.kirasystem.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.kosh.kirasystem.KiraConstants;
import ch.kosh.kirasystem.Phone;
import ch.kosh.kirasystem.PhoneList;

public class PhoneListTest {
	@Test
	public void testCalcOfAway() {
		PhoneList phoneList = new PhoneList();
		phoneList.setLastSeen(KiraConstants.macAddressMark, 100);
		assertEquals(true, phoneList.isAvailable(KiraConstants.macAddressMark, 1000));
		assertEquals(false, phoneList.isAvailable(KiraConstants.macAddressMark, Phone.AWAY_TIME + 5000));
	}

	@Test(expected = RuntimeException.class)
	public void testUnknownAddress() {
		PhoneList phoneList = new PhoneList();
		phoneList.isAvailable("dummy", 0);
	}

	@Test
	public void testPhoneIsAvailable() {
		PhoneList list = new PhoneList();
		assertEquals(list.isAvailable(KiraConstants.macAddressMark, Phone.AWAY_TIME + 5000), false);
		list.setLastSeen(KiraConstants.macAddressMark, 50001);
		assertEquals(true, list.isAvailable(KiraConstants.macAddressMark, 50005));
	}

	@Test
	public void getLastSeenPhoneMac() {
		PhoneList list = new PhoneList();
		list.setLastSeen(KiraConstants.heleneiPhone6Address, 42);
		String newerOne = KiraConstants.macAddressMark;
		list.setLastSeen(newerOne, 1042);
		assertEquals("Zedd", list.getLastSeenPhone().getOwnerName());
	}
}
