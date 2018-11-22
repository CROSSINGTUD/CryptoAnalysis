package main;

import com.google.common.base.Stopwatch;

public class Main {
	private static void correct() {
		Stopwatch watch = Stopwatch.createStarted();
		watch.isRunning();
		watch.stop();
		watch.start();
		if(watch.isRunning()) {
			watch.stop();
		}
	}
	private static void wrong() {
		Stopwatch watch = Stopwatch.createStarted();
		watch.stop();
		watch.stop();
	}
	
	private static void wrongWithContext() {
		Stopwatch watch = Stopwatch.createStarted();
		context(watch);
		context(watch);
	}


	private static void wrongWithTwoContexts() {
		Stopwatch watch = Stopwatch.createStarted();
		context2(watch);
		context(watch);
	}

	private static void context2(Stopwatch watch) {
		watch.stop();
	}

	private static void context(Stopwatch watch) {
		watch.stop();
	}
}
