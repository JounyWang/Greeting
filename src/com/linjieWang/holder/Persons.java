package com.sihuatech.sensetime.demo.holder;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sihuatech.sensetime.demo.Person;

public final class Persons {

	private Persons() {
		//
	}

	public static final Persons getInstance() {
		return Holder.instance;
	}

	private Map<String, Person> persons = new HashMap<String, Person>();

	public Map<String, Person> list() {
		return persons;
	}

	public void put(String key, Person person) {
		persons.put(key, person);
	}

	public void remove(String id) {
		persons.remove(id);
	}

	public Person get(String id) {
		return persons.get(id);
	}

	private static final class Holder {
		private static Persons instance = new Persons();
	}
}
