package com.sihuatech.sensetime.demo.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sihuatech.sensetime.demo.Demo;
import com.sihuatech.sensetime.demo.GreetingQueue;
import com.sihuatech.sensetime.demo.Person;
import com.sihuatech.sensetime.demo.Speaker;
import com.sihuatech.sensetime.demo.holder.Persons;
import com.sihuatech.sensetime.demo.holder.Sessions;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CallbackServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(CallbackServlet.class);
	private static final long serialVersionUID = -5283917516399628000L;
	private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Date now = new Date();
		broadcast("a http Get request on " + formatter.format(now));
		resp.setStatus(HttpStatus.SC_OK);
		PrintWriter pw = resp.getWriter();
		pw.write("<h1>Callback service is running!</h1>");
		pw.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// logger.info("doPost()");
		StringWriter sw = new StringWriter();
		IOUtils.copy(req.getInputStream(), sw);
		String jsonString = sw.toString();
		if (jsonString != null) {
			JSONObject json = JSONObject.fromObject(jsonString);
			int type = json.getInt("type");
			switch (type) {
			case 0:
				// TODO://
				break;
			case 1:
				logger.info(jsonString);
				parseRecongnizition(json);
				break;
			case 2:
				// TODO://
				break;
			}
			resp.setStatus(HttpStatus.SC_OK);

		} else {
			resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

	DateFormat df = new SimpleDateFormat("MM:ss.SSS");

	private void parseRecongnizition(JSONObject json) {
		JSONObject result = json.getJSONObject("recogResult");
		if (result != null) {
			if (result.containsKey("similars")) {
				Set<String> personIds = new HashSet<String>();
				JSONArray repositories = result.getJSONArray("similars");
				if (repositories.isArray()) {
					JSONObject repository;
					for (int i = 0; i < repositories.size(); i++) {
						repository = (JSONObject) repositories.get(i);
						String repositoryId = repository.getString("dbId");
						logger.info("repository: " + repositoryId);
						JSONArray similars = repository.getJSONArray("users");
						for (int j = 0; j < similars.size(); j++) {
							JSONObject similar = similars.getJSONObject(j);
							if (similar != null) {
								String personId = similar.getString("user_idx");
								Double score = similar.getDouble("score");
								if (score > 0.8) {
									logger.info("!!! Found:" + personId + ", score: " + score);
									personIds.add(personId);
								}
							}
						}
					}
				} else {
					logger.info(result.toString());
				}
				Map<String, Person> persons = Persons.getInstance().list();
				for (String personId : personIds) {
					if (!persons.containsKey(personId)) {
						logger.error("*** " + personId);
					}
					Person person = persons.get(personId);
					if (person != null) {
						long now = System.currentTimeMillis();
						long silenceTime = 1000 * 10;
						logger.info("person: " + person.getName());
						String[] greetings = person.getGreetings();
						String greeting = null;
						if (greetings.length != 0) {
							int random = (int) Math.floor(Math.random() * greetings.length);
							greeting = greetings[random];
						} else {
							logger.info("no greeting");
						}
						synchronized (person) {
							long lastGreetingTime = person.getLastGreetingTime();
							if (now - lastGreetingTime > silenceTime) {
								if (greeting != null) {
									logger.info("sayGreeting:" + greeting);
									sayGreeting(greeting, 100, +2);
								}
								person.setLastGreetingTime(now);
							} else {
								logger.info("Silence time, lastGreetingTime: " + person.getLastGreetingTime());
							}
						}
					}
				}
			}
		} else {
			logger.info("result=null");
		}
	}

	private void sayGreeting(String greeting, int volume, int rate) {
		broadcast(greeting);
//		Speaker voice = new Speaker();
//		try {
//			voice.speak(greeting, volume, rate);
//		} catch (IOException e) {
//			e.printStackTrace();
//			logger.info("Greetings failed.");
//		}
	}

	private void broadcast(String message) {
		Map<String, Session> sessions = Sessions.getInstance().getSessions();
		Session session;
		for (Map.Entry<String, Session> entry : sessions.entrySet()) {
			session = entry.getValue();
			try {
				session.getRemote().sendString(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
