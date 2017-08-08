package com.sihuatech.sensetime.demo;

import java.net.URI;

public class Source {
	private URI uri;
	private int frameWidth = 1920;
	private int frameHeight = 1080;

	public Source(URI uri, int w, int h) {
		this.uri = uri;
		this.frameWidth = w;
		this.frameHeight = h;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public void setFrameWidth(int frameWidth) {
		this.frameWidth = frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public void setFrameHeight(int frameHeight) {
		this.frameHeight = frameHeight;
	}

}
