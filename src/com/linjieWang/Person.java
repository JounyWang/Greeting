package com.sihuatech.sensetime.demo;

public class Person {

	private String repository;
	private String id;
	private String name;
	private String nickname;
	private String originalImageName;
	private String feature;
	private String[] greetings;
	private long lastGreetingTime;

	public long getLastGreetingTime() {
		return lastGreetingTime;
	}

	public void setLastGreetingTime(long lastGreetingTime) {
		this.lastGreetingTime = lastGreetingTime;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getOriginalImageName() {
		return originalImageName;
	}

	public void setOriginalImageName(String originalImageName) {
		this.originalImageName = originalImageName;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String[] getGreetings() {
		return greetings;
	}

	public void setGreetings(String[] greetings) {
		this.greetings = greetings;
	}

}
