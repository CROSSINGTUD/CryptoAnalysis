package main;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.base.Stopwatch;

public class Main {
	public static void main(String...args) throws GeneralSecurityException{
		a();
		b();
	}

	private static void a() {
		Stopwatch watch = Stopwatch.createStarted();
		watch.isRunning();
		watch.stop();
		watch.start();
		if(watch.isRunning()) {
			watch.stop();
		}
	}
	private static void b() {
		Stopwatch watch = Stopwatch.createStarted();
		watch.stop();
		watch.stop();
	}
}
