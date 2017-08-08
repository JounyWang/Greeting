package com.sihuatech.sensetime.demo.helper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URIBuilderHelper {
	private static final Logger logger = LoggerFactory.getLogger(URIBuilderHelper.class);

	public static URI buildHttpURI(String host, int port, String path) {
		return buildURI("http", host, port, path, null);
	}

	public static URI buildRtspURI(String host, int port, String path) {
		return buildURI("rtsp", host, port, path, null);
	}

	public static URI buildURI(String scheme, String host, int port, String path, List<NameValuePair> pairs) {
		URI uri = null;
		try {
			URIBuilder b = new URIBuilder();
			b.setScheme(scheme);
			b.setHost(host);
			b.setPort(port);
			b.setPath(path);
			if (pairs != null) {
				for (NameValuePair pair : pairs) {
					b.setParameter(pair.getName(), pair.getValue());
				}
			}
			uri = b.build();
			logger.info("URI: " + uri.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}

	public static URI buildURI(URI uri, String path) {
		return buildURI(uri, path, null);
	}

	public static URI buildURI(URI uri, String path, List<NameValuePair> pairs) {
		URI newUri = null;
		try {
			URIBuilder b = new URIBuilder(uri);
			b.setPath(path);
			if (pairs != null) {
				for (NameValuePair pair : pairs) {
					b.setParameter(pair.getName(), pair.getValue());
				}
			}
			newUri = b.build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return newUri;
	}
}
