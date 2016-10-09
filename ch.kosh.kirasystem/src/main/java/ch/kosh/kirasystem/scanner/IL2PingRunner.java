package ch.kosh.kirasystem.scanner;

import java.io.IOException;

public interface IL2PingRunner {

	public abstract boolean pingBTAddress(String macAddress) throws IOException,
			InterruptedException;

}