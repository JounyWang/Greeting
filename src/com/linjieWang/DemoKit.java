package com.sihuatech.sensetime.demo;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sihuatech.sensetime.demo.servlet.CallbackServlet;
import com.sihuatech.sensetime.demo.servlet.DemoWebSocketServlet;
import com.sihuatech.sensetime.demo.Configuration;

public class DemoKit implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(DemoKit.class);

	@Override
	public void run() {

		String callbackServletPath = "/callback";
		String webSocketServerPath = "/wss";

		int port = Configuration.getPort();

		Server server = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		if (logger.isDebugEnabled()) {
			String[] vhs = context.getVirtualHosts();
			if (vhs != null) {
				for (String vh : vhs) {
					logger.debug(vh);
				}
			}
		}
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new CallbackServlet()), callbackServletPath);
		context.addServlet(new ServletHolder(new DemoWebSocketServlet()), webSocketServerPath);
		server.setHandler(context);
		try {
			logger.info("Starting the demo server");
			server.start();
			// server.join();
		} catch (InterruptedException e) {
			logger.warn("", e);
		} catch (Exception e) {
			logger.warn("", e);
		}

		HttpExecutor executor = new HttpExecutor();
		Demo demo = new Demo(Configuration.getSensetimeServer());
		demo.setExecutor(executor);
		Thread demoThread = new Thread(demo);
		demoThread.setName("runner");
		demoThread.start();
	}

	public static final void main(String[] args) {
		String config = null;
		if (args != null && args.length != 0) {
			config = args[0];
		} else {
			config = "./config/config.properties";
		}
		DemoKit dk = new DemoKit();
		Configuration.init(config);
		// dk.loadConfiguration(config);
		dk.run();
	}
}
