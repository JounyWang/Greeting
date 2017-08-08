package com.sihuatech.sensetime.demo.holder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Sessions {

	private static final Logger logger = LoggerFactory.getLogger(Sessions.class);

	private Map<String, Session> sessions = new HashMap<String, Session>();

	public static Sessions getInstance() {
		return Holder.instance;
	}

	private Sessions() {

	}

	public Map<String, Session> getSessions() {
		return sessions;
	}

	public void put(String id, Session session) {
		sessions.put(id, session);
		logger.info("size: " + sessions.size());
	}

	public void remove(String id) {
		sessions.remove(id);
		logger.info("size: " + sessions.size());
	}

	private static class Holder {
		private static final Sessions instance = new Sessions();
	}

}
