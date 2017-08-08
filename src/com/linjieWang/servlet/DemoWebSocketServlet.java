package com.sihuatech.sensetime.demo.servlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sihuatech.sensetime.demo.ws.DemoWebSocketAdapter;

public class DemoWebSocketServlet extends WebSocketServlet {

	private static final long serialVersionUID = -2871672844164343511L;
	private static final Logger logger = LoggerFactory.getLogger(DemoWebSocketServlet.class);

	@Override
	public void configure(WebSocketServletFactory factory) {

		// set a 10 second timeout
		factory.getPolicy().setIdleTimeout(30000);

		// set a custom WebSocket creator
		// factory.setCreator(new CommunicationCreator());

		// register MyEchoSocket as the WebSocket to create on Upgrade
		factory.register(DemoWebSocketAdapter.class);

		logger.info("...");

	}

}
