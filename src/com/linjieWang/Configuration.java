package com.sihuatech.sensetime.demo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import com.sihuatech.sensetime.demo.helper.URIBuilderHelper;

public class Configuration {
	private static String facesImagePath = "./faces";
	private static String searchPath = "./searchs";

	private static String sensetimeServerHost = "localhost";
	private static int sensetimeServerPort = 80;
	private static String host = "localhost";
	private static int port = 8888;
	private static String personsPath="./persons";
	private static String sourceScheme;
	private static String sourceHost;
	private static int sourcePort;
	private static String sourcePath;
	private static int fw = 1920;
	private static int fh = 1080;
	static String callbackServletPath = "/callback";
	static String webSocketServerPath = "/wss";

	public static URI getSensetimeServer() {
		return URIBuilderHelper.buildHttpURI(sensetimeServerHost, sensetimeServerPort, "/");
	}

	public static String getCallback() {
		return host + port + callbackServletPath;

	}

	public static Source getSource() {
		URI sourceUri = URIBuilderHelper.buildURI(sourceScheme, sourceHost, sourcePort, sourcePath, null);

		return new Source(sourceUri, fw, fh);
	}

	public static String getPersonsPath() {
		return personsPath;
	}

	public static void setPersonsPath(String personsPath) {
		Configuration.personsPath = personsPath;
	}

	public static String getFacesImagePath() {
		return facesImagePath;
	}

	public static void setFacesImagePath(String facesImagePath) {
		Configuration.facesImagePath = facesImagePath;
	}

	public static String getSearchPath() {
		return searchPath;
	}

	public static void setSearchPath(String searchPath) {
		Configuration.searchPath = searchPath;
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		Configuration.host = host;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		Configuration.port = port;
	}

	private static void initSensetimeServer(Properties prop) {
		String host = prop.getProperty("sensetime.server.host");
		if (host != null) {
			sensetimeServerHost = host;
		}
		String port = prop.getProperty("sensetime.server.port");
		if (port == null) {
			sensetimeServerPort = Integer.parseInt(port);
		}
	}

	private static void initCallbackServer(Properties prop) {
		String _host = prop.getProperty("server.host");
		if (_host != null) {
			host = _host;
		}
		String _port = prop.getProperty("server.port");
		if (_port != null) {
			port = Integer.parseInt(_port);
		}
	}

	private static void initSource(Properties prop) {
		sourceScheme = prop.getProperty("source.scheme");
		sourceHost = prop.getProperty("source.host");
		String _sourcePort = prop.getProperty("source.port");
		sourcePort = (_sourcePort != null) ? Integer.parseInt(_sourcePort) : 554;
		String _sourcePath = prop.getProperty("source.path");
		sourcePath = (_sourcePath != null) ? _sourcePath : "/";
		String _fw = prop.getProperty("source.frame.width");
		String _fh = prop.getProperty("source.frame.height");
		fw = (_fw != null) ? Integer.parseInt(_fw) : 1920;
		fh = (_fh != null) ? Integer.parseInt(_fh) : 1080;
	}	
	
	public static void init(String config) {

		File configFile = new File(config);
		if (!configFile.exists() || !configFile.isFile()) {
			throw new RuntimeException("Config \"" + configFile.getPath() + "\" file not found.");
		}

		Properties prop = new Properties();
		try {
			prop.load(new FileReader(configFile));
		} catch (IOException e) {
			e.printStackTrace();
		}

		initCallbackServer(prop);
		initSensetimeServer(prop);

		initSource(prop);


		facesImagePath = prop.getProperty("faces.image.path");
		searchPath = prop.getProperty("search.path");

	}
}
