package com.sihuatech.sensetime.demo.ws;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sihuatech.sensetime.demo.holder.Sessions;

public class DemoWebSocketAdapter extends WebSocketAdapter {
	private static final Logger logger = LoggerFactory.getLogger(DemoWebSocketAdapter.class);

	private String sessionId;

	@Override
	public void onWebSocketConnect(Session session) {
		UpgradeRequest req = session.getUpgradeRequest();
		Map<String, List<String>> params = req.getParameterMap();
		if (params != null) {
			logger.info("params: " + params);
			for (Map.Entry<String, List<String>> entry : params.entrySet()) {
				String key = entry.getKey();
				List<String> values = entry.getValue();
				StringBuffer sb = new StringBuffer();
				for (String value : values) {
					sb.append(value + ", ");
				}
				logger.info(key + ": " + sb.toString());
			}
			List<String> id = params.get("id");
			if (id != null && id.size() != 0) {
				sessionId = id.get(0);
			}
		}
		if (sessionId != null) {
			Sessions.getInstance().put(sessionId, session);
			super.onWebSocketConnect(session);
		} else {
			session.close();
		}
		logger.info("onWebSocketConnect");
	}

	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
		if (isConnected()) {
			logger.info(">>> " + message);
			boolean isEcho = false;
			if (isEcho) {
				try {
					getRemote().sendString(message);
				} catch (IOException e) {
					logger.warn("", e);
				}
			}
		}
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		Sessions.getInstance().remove(sessionId);
		super.onWebSocketClose(statusCode, reason);
		logger.info("onWebSocketClose");
	}
}
