package com.sihuatech.sensetime.demo;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class GreetingQueue {
	private static Queue<Person> queue = new ConcurrentLinkedDeque<Person>();
	
	private static GreetingQueue instance = new GreetingQueue();
	
	private GreetingQueue(){
		
	}
	
	public static final GreetingQueue getInstance(){
		return instance;
	}
	
	public void add(Person person){
		queue.add(person);
	}
	
	public Queue<Person> getQueue(){
		return queue;
	}
}
