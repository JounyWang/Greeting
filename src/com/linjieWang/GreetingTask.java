package com.sihuatech.sensetime.demo;

public class GreetingTask implements Runnable {

	@Override
	public void run() {
		Person person;
		while (true) {
			person = GreetingQueue.getInstance().getQueue().poll();
			if (person != null) {
			} else {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
