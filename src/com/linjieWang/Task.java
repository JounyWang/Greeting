package com.sihuatech.sensetime.demo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Task {

	public static final short TYPE_DEFAULT = 0;
	public static final short TYPE_TEST_STREAM = 1;

	private short projectId = 1000;
	private short type = TYPE_DEFAULT;

	private List<Output> outputs = new ArrayList<Output>();
	private Private private_;
	private SourceWrapper source;

	public short getProjectId() {
		return projectId;
	}

	public void setProjectId(short projectId) {
		this.projectId = projectId;
	}

	public short getType() {
		return this.type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public void addOutput(Output output) {
		outputs.add(output);
	}

	public void setSourceWrapper(SourceWrapper source) {
		this.source = source;
	}

	public void setPrivate(Private private_) {
		this.private_ = private_;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		JSONArray arr = new JSONArray();
		int i = 0;
		for (Output output : this.outputs) {
			JSONObject item = output.toJSONObject();
			item.put("Index", i++);
			arr.add(item);
		}
		obj.put("Result", arr);
		obj.put("Source", source.toJSONObject());
		obj.put("Private", private_.toJSONObject());

		JSONObject json = new JSONObject();
		json.put("taskType", type);
		json.put("param", obj);
		return json;
	}

	static class SourceWrapper {
		public static final short TYPE_FILE = 0;
		public static final short TYPE_RTSP_STREAM = 2;
		private short type = TYPE_RTSP_STREAM;
		private Source source;

		public SourceWrapper(Source source) {
			this.source = source;
		}

		public JSONObject toJSONObject() {
			URI uri = source.getUri();
			JSONObject json = new JSONObject();
			json.put("SourceType", type);
			json.put("StdWidth", source.getFrameWidth());
			json.put("StdHeight", source.getFrameHeight());
			switch (type) {
			case SourceWrapper.TYPE_FILE:
				json.put("VideoFile", uri.getPath());
				break;
			case SourceWrapper.TYPE_RTSP_STREAM:
				json.put("RtspUrl", uri.toString());
				break;
			default:
				// TODO://
			}
			return json;
		}
	}

	public static class Output {
		public static final short PROTOCOL_TCP = 0;
		public static final short PROTOCOL_HTTP = 10;
		private short protocolType = PROTOCOL_HTTP;
		private String url;
		private String ip;
		private int port;

		public Output(String url) {
			this.protocolType = PROTOCOL_HTTP;
			this.url = url;
		}

		public Output(String ip, int port) {
			this.protocolType = PROTOCOL_TCP;// UDP?
			this.ip = ip;
			this.port = port;
		}

		public JSONObject toJSONObject() {
			JSONObject json = new JSONObject();
			json.put("ProtocolType", protocolType);
			switch (protocolType) {
			case PROTOCOL_HTTP:
				json.put("URL", url);
				break;
			case PROTOCOL_TCP:
				json.put("IP", ip);
				json.put("Port", port);
			default:
				//
			}
			return json;
		}

	}

	public static class Private {

		private List<Algorithm> algorithms = new ArrayList<Algorithm>();
		private List<Target> targets = new ArrayList<Target>();
		private ImageMode imageMode;

		public void setImageMode(ImageMode imageMode) {
			this.imageMode = imageMode;
		}

		public void addAlgorithm(Algorithm algorithm) {
			this.algorithms.add(algorithm);
		}

		public void addTarget(Target target) {
			this.targets.add(target);
		}

		public JSONObject toJSONObject() {
			JSONObject json = new JSONObject();
			// targets
			JSONArray targets = new JSONArray();
			for (Target target : this.targets) {
				targets.add(target.toJSONObject());
			}
			json.put("targets", targets);
			json.put("imgMode", imageMode.toJSONObject());
	
			json.put("algParam", ((Algorithm) algorithms.get(0)).toJSONObject());
			return json;
		}

		public static class Target {
			private String repositoryId;
			private float score;

			public Target(String repositoryId, float score) {
				this.repositoryId = repositoryId;
				this.score = score;
			}

			public JSONObject toJSONObject() {
				JSONObject obj = new JSONObject();
				obj.put("dbId", repositoryId);
				obj.put("score", score);
				return obj;
			}
		}

		public static class ImageMode {
			public static final short IMAGE_MODE_BODY = 0;
			public static final short IMAGE_MODE_FACE = 1;
			public static final short IMAGE_MODE_HALF_BODY = 2;
			public static final short IMAGE_MODE_NONE = 3;
			private short mode = IMAGE_MODE_FACE;

			public void setMode(short mode) {
				this.mode = mode;
			}

			public JSONObject toJSONObject() {
				JSONObject json = new JSONObject();
				json.put("mode", mode);
				return json;
			}
		}

		public static class Algorithm {
			private List<Region> regions = new ArrayList<Region>();
			private int minFaceW = 30;
			private int minFaceH = 30;
			private Integer maxFaceW = null;
			private Integer maxFaceH = null;
			private float thresholdOfDetection = 0.5f;
			private int intervalOfDectection = 0;
			private float thresholdOfRecognition = 0.5f;
			private int intervelOfRecognize = 1;
			private int minEyeSpacing = 30;
			private float maxYaw = 30;
			private float maxPitch = 40;
			private float maxRotaion = 30;
			private Short extractFeature = 0;

			public void addRegion(Region region) {
				this.regions.add(region);
			}

			public JSONObject toJSONObject() {
				JSONObject json = new JSONObject();
				json.put("minDetectWidth", minFaceW);
				json.put("minDetectHeight", minFaceH);
				if (maxFaceW != null) {
					json.put("maxDetectWidth", maxFaceW);
				}
				if (maxFaceH != null) {
					json.put("maxDetectHeight", maxFaceH);
				}
				json.put("detectThd", thresholdOfDetection);
				json.put("detectInterval", intervalOfDectection);
				json.put("recogThd", thresholdOfRecognition);
				json.put("recogInterval", intervelOfRecognize);
				json.put("minEyeDist", minEyeSpacing);
				json.put("maxYaw", maxYaw);
				json.put("maxPitch", maxPitch);
				json.put("maxRoll", maxRotaion);
				json.put("pointLen", regions.size());
				if (extractFeature != null) {
					json.put("isExtractAttribute", extractFeature);
				}
				JSONArray arr = new JSONArray();
				int i = 0;
				for (Region region : this.regions) {
					JSONObject item = region.toJSONObject();
					item.put("index", i++);
					arr.add(item);
				}
				json.put("hotRegion", arr);
				return json;
			}

			public static class Region {
				private int x;
				private int y;

				public Region(int x, int y) {
					this.x = x;
					this.y = y;
				}

				public JSONObject toJSONObject() {
					JSONObject json = new JSONObject();
					json.put("pointX", x);
					json.put("pointY", y);
					return json;
				}
			}
		}
	}
}
