package com.sihuatech.sensetime.demo;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpExecutor {

	private static final Logger logger = LoggerFactory.getLogger(DemoKit.class);

	private CloseableHttpClient httpClient;

	public void setup() {
		RequestConfig config = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000)
				.setConnectionRequestTimeout(3000).build();
		httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
	}

	public String execute(HttpUriRequest request) {
		logger.info(request.getMethod() + "\t" + request.getURI().toString());
		String json = null;
		int status = 0;
		try {
			HttpResponse response = httpClient.execute(request);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine != null) {
				status = statusLine.getStatusCode();
				if (status == HttpStatus.SC_OK) {
					logger.info("status: " + status);
					json = EntityUtils.toString(response.getEntity());
					logger.info(json);
				} else {
					logger.warn("Status: ", status);
				}
			} else {
				logger.warn("Status Line is null");
			}
		} catch (ClientProtocolException e) {
			logger.info("ClientProtocolException", e);
		} catch (IOException e) {
			logger.info("IOException", e);
		}
		return json;
	}

	public void teardown() {
		try {
			httpClient.close();
		} catch (IOException e) {
			logger.warn("IOException", e);
		}
	}

}
