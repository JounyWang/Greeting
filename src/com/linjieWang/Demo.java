package com.sihuatech.sensetime.demo;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sihuatech.sensetime.demo.helper.URIBuilderHelper;
import com.sihuatech.sensetime.demo.holder.Persons;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Demo implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Demo.class);


//	private String personsPath = "D:/Neonworkspace/SenseTimeDemo/persons";

	private URI sensetimeServer;


	private HttpExecutor executor;

	public void setExecutor(HttpExecutor executor) {
		this.executor = executor;
	}

	public Demo(URI sensetimeServer){
		this.sensetimeServer = sensetimeServer;
	}
	
	
	public List<Repository> getRepositories() {
		URI uri = URIBuilderHelper.buildURI(sensetimeServer, "/verify/target/gets");
		HttpUriRequest request = RequestBuilder.get().setUri(uri).build();
		String jsonString = executor.execute(request);
		List<Repository> repositories = null;
		if (jsonString != null) {
			JSONObject json = JSONObject.fromObject(jsonString);
			if (json != null) {
				repositories = new ArrayList<Repository>();
				JSONArray array = json.getJSONArray("data");
				Repository repository;
				for (int i = 0; i < array.size(); i++) {
					JSONObject obj = array.getJSONObject(i);
					logger.info(obj.toString());
					repository = new Repository();
					repository.setName(obj.getString("dbName"));
					// repository.setSize(obj.getInt("maxCount"));
					repository.setCount(obj.getInt("count"));
					repositories.add(repository);
				}
			}
		}
		return repositories;
	}

	public boolean createRepository(Repository repository) {
		URI uri = URIBuilderHelper.buildURI(sensetimeServer, "verify/target/add");
		HttpUriRequest request = RequestBuilder.post().setUri(uri).addParameter("dbName", repository.getName()).build();
		String jsonString = executor.execute(request);
		return jsonString != null;
	}

	public void loadPersons(File dir) {
		logger.info("load persons from json files");
		File[] files = dir.listFiles(new JsonFileFilter());
		if (files != null) {
			String jsonString;
			JSONObject json;
			Person person;
			try {
				for (File file : files) {
					jsonString = FileUtils.readFileToString(file, "UTF-8");
					json = JSONObject.fromObject(jsonString);
					person = (Person) JSONObject.toBean(json, Person.class);
					Persons.getInstance().put(person.getId(), person);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Person person;
		for (Map.Entry<String, Person> entry : Persons.getInstance().list().entrySet()) {
			person = entry.getValue();
			logger.info("person: " + person.getName() + ", " + person.getId());
		}
	}

	public boolean deleteImage(String imageId, String repositoryId) {
		boolean flag = false;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("dbName", repositoryId));
		params.add(new BasicNameValuePair("imageId", imageId));
		URI uri = URIBuilderHelper.buildURI(sensetimeServer, "/verify/face/deletes", params);
		HttpPost post = new HttpPost(uri);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		post.setEntity(builder.build());
		String jsonString = executor.execute(post);
		if (jsonString != null) {
			JSONObject json = JSONObject.fromObject(jsonString);
			logger.info("JSON: " + json.toString());
			String result = json.getString("result");
			if ("success".equals(result)) {
				flag = true;
			}
		}
		return flag;
	}

	public void addPersonsFrom(File dir, String repositoryId) {
		File[] images = dir.listFiles(new ImageFileFilter());
		if (images != null) {

			List<Person> persons = addPersons(images, repositoryId);
			String fileName, newFileName;
			String jsonFileName;
			File jsonFile;
			if (persons != null) {
				for (Person person : persons) {
					fileName = person.getOriginalImageName();
					newFileName = fileName + ".bak";
					File image = new File(dir, fileName);
					if (image.exists() && image.isFile()) {
						if (!image.renameTo(new File(dir, newFileName))) {
							logger.info("Rename image file failed.");
						}
					}
					String personsPath = Configuration.getPersonsPath();
					File jsonDir = new File(personsPath);
					if (jsonDir.exists() && dir.isDirectory()) {
						String imgName =person.getOriginalImageName();
						jsonFileName = imgName.substring(0,imgName.lastIndexOf(".")) + ".json";
						jsonFile = new File(jsonDir, jsonFileName);
						try {
							JSONObject json = JSONObject.fromObject(person);
							String data = json.toString();
							logger.info(json.toString());
							FileUtils.writeStringToFile(jsonFile, data, "UTF-8");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	private List<Person> addPersons(File[] images, String repositoryId) {
		URI uri = URIBuilderHelper.buildURI(sensetimeServer, "/verify/face/synAdd");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addPart("dbName", new StringBody(repositoryId, ContentType.TEXT_PLAIN));
		if (images != null) {
			for (File image : images) {
				builder.addPart("imageDatas", new FileBody(image));
			}
		}

		HttpPost post = new HttpPost(uri);
		post.setEntity(builder.build());
		String jsonString = executor.execute(post);
		List<Person> persons = null;
		if (jsonString != null) {
			JSONObject json = JSONObject.fromObject(jsonString);
			JSONArray arr = json.getJSONArray("success");
			if (arr != null) {
				persons = new ArrayList<Person>();
				String imageFileName;
				String id, name;
				String feature = null;
				String greeting;
				Person person;
				for (int i = 0; i < arr.size(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					imageFileName = obj.getString("name");
					id = obj.getString("imageId");
					name = imageFileName.substring(0, imageFileName.lastIndexOf("."));
					greeting = "Hello, " + name;
					// feature = obj.getString("feature");
					person = new Person();
					person.setOriginalImageName(imageFileName);
					person.setRepository(repositoryId);
					person.setId(id);
					person.setName(name);
					person.setNickname(name);
					person.setFeature(feature);
					person.setGreetings(new String[] { greeting });
					persons.add(person);
				}
			}
		}
		return persons;

	}

	public void search(File image, float minScore, int top, Repository repository) {
		URI uri = URIBuilderHelper.buildURI(sensetimeServer, "/verify/face/search");
		HttpPost post = new HttpPost(uri);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("dbName", repository.getName());
		builder.addTextBody("topNum", String.valueOf(top));
		builder.addTextBody("score", String.valueOf(minScore));
		builder.addBinaryBody("imageData", image);
		post.setEntity(builder.build());
		String jsonString = executor.execute(post);
		if (jsonString != null) {
			JSONObject json = JSONObject.fromObject(jsonString);
			if (json != null) {
				logger.debug("JSON: " + json.toString());
				String result = json.getString("result");
				logger.debug("Result: " + result);
				if ("success".equals(result)) {
					logger.info("Searched successfully!");
					JSONArray arr = json.getJSONArray("data");
					if (arr != null) {
						String imageId, score;
						for (int i = 0; i < arr.size(); i++) {
							JSONObject obj = arr.getJSONObject(i);
							imageId = obj.getString("imageId");
							score = obj.getString("score");
							logger.info(imageId + ", " + score);
						}
					} else {
						logger.warn("No person matched");
					}
				}
			} else {
				logger.info("Search failed!");
			}
		}
	}

	public boolean clearRepository() {
		URI uri = URIBuilderHelper.buildURI(sensetimeServer, "/verify/target/clear");
		HttpUriRequest request = RequestBuilder.post().setUri(uri).addParameter("dbName", "Demo").build();
		String jsonString = executor.execute(request);
		return jsonString != null;
	}

	public boolean createTask(Task task, short projectId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ProjectID", String.valueOf(projectId)));
		URI uri = URIBuilderHelper.buildURI(sensetimeServer, "/Task/CreateTask", params);
		logger.info("*** " + task.toJSONObject());
		HttpEntity entity = null;
		try {
			entity = new StringEntity(task.toJSONObject().toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpPost post = new HttpPost(uri);
		post.setEntity(entity);
		String jsonString = executor.execute(post);
		if (jsonString != null) {
			JSONObject obj = JSONObject.fromObject(jsonString);
			if (obj != null) {
				String returnCode = obj.getString("returnCode");
				if (returnCode != null && returnCode.equals("0")) {
					String taskId = obj.getString("taskID");
					logger.info("Task created! taskId: " + taskId);
					return true;
				}
			}
		}
		return false;
	}

	public List<String> listTasks(int projectId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ProjectID", String.valueOf(projectId)));
		URI uri = URIBuilderHelper.buildURI(sensetimeServer, "Task/QueryResource", params);
		HttpGet get = new HttpGet(uri);
		String jsonString = executor.execute(get);
		List<String> tasks = null;
		if (jsonString != null) {
			JSONObject json = JSONObject.fromObject(jsonString);
			if (json != null) {
				String code = json.getString("returnCode");
				if (code != null && code.equals("0")) {
					if (json.containsKey("taskIds")) {
						JSONArray arr = json.getJSONArray("taskIds");
						if (arr != null) {
							tasks = new ArrayList<String>();
							String id;
							for (int i = 0; i < arr.size(); i++) {
								id = arr.getString(i);
								tasks.add(id);
							}
						}
					}
				}
			}
		}
		return tasks;
	}

	public Task getTask(String id) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("taskid", id));
		URI uri = URIBuilderHelper.buildURI(sensetimeServer, "/Task/GetTaskInfo", params);
		HttpGet get = new HttpGet(uri);
		String jsonString = executor.execute(get);
		if (jsonString != null) {
			JSONObject json = JSONObject.fromObject(jsonString);
			if (json != null) {
				String code = json.getString("returnCode");
				if (code != null && code.equals("0")) {
					short projectId = (short) json.getInt("projectId");
					String param = json.getString("param");
					logger.info(param);
					Task task = new Task();
					task.setProjectId(projectId);
				}
			}
		}
		return null;
	}

	public boolean deleteTask(String id, short projectId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ProjectID", String.valueOf(projectId)));
		URI uri = URIBuilderHelper.buildURI(sensetimeServer, "/Task/DeleteTask", params);
		JSONArray arr = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("taskID", id);
		arr.add(obj);
		logger.info("# " + obj.toString());
		HttpEntity entity = null;
		try {
			entity = new StringEntity(obj.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpPost post = new HttpPost(uri);
		post.setEntity(entity);
		String jsonString = executor.execute(post);
		if (jsonString != null) {
			JSONObject json = JSONObject.fromObject(jsonString);
			if (json != null) {
				String code = json.getString("returnCode");
				if (code != null && code.equals("0")) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void run() {

		boolean firstTime = true;
		executor.setup();
		if (firstTime) {

			// clearRepository();
			List<Repository> repositories = getRepositories();
			Repository repository = null;
			if (repositories != null && repositories.size() != 0) {
				repository = repositories.get(0);
			} else {
				repository = new Repository();
				repository.setName("Demo");
				repository.setSize(100);
				createRepository(repository);
			}

			String repositoryId = repository.getName();
			String facesImagePath =Configuration.getFacesImagePath();
			File facesDir = new File(facesImagePath);
			if (facesDir.exists() && facesDir.isDirectory()) {
				addPersonsFrom(facesDir, repositoryId);
			} else {
				logger.warn(facesDir.getPath() + " is not a dir");
			}
			String personsPath = Configuration.getPersonsPath();
			File personsDir = new File(personsPath);
			if (personsDir.exists() && personsDir.isDirectory()) {
				loadPersons(personsDir);
			}
			// Quick search\
			String searchPath = Configuration.getSearchPath();
			File searchDir = new File(searchPath);
			if (searchDir.exists() && searchDir.isDirectory()) {
				File[] images = searchDir.listFiles(new ImageFileFilter());
				for (File image : images) {
					logger.info("Search " + image.getAbsolutePath());
					// search(image, 0.5f, 10, repository);
				}
			}

			short projectId = 1000;
			List<String> taskIds = listTasks(projectId);
			if (taskIds != null) {
				for (String taskId : taskIds) {
					logger.debug("Delete task: " + taskId);
					// deleteTask(taskId, projectId);
				}
			}
			Task task = buildDemoTask(projectId, repository.getName(), 0.7f);
			logger.debug("Create task: " + task);
			// createTask(task, projectId);
			listTasks(projectId);
		}
		executor.teardown();
	}

	private Task buildDemoTask(short projectId, String repositoryId, float score) {
		int w = 640, h = 480;
		Task.Private private_ = new Task.Private();
		private_.addTarget(new Task.Private.Target(repositoryId, score));
		Task.Private.Algorithm algorithm = new Task.Private.Algorithm();
		algorithm.addRegion(new Task.Private.Algorithm.Region(0, 0));
		algorithm.addRegion(new Task.Private.Algorithm.Region(w, 0));
		algorithm.addRegion(new Task.Private.Algorithm.Region(w, h));
		algorithm.addRegion(new Task.Private.Algorithm.Region(0, h));
		private_.addAlgorithm(algorithm);
		private_.setImageMode(new Task.Private.ImageMode());
		Task.SourceWrapper sourceWrapper = new Task.SourceWrapper(Configuration.getSource());
		Task task = new Task();
		task.setProjectId(projectId);
		task.setSourceWrapper(sourceWrapper);
		task.addOutput(new Task.Output(Configuration.getCallback()));
		task.setPrivate(private_);
		return task;
	}

	class ImageFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			String pathName = pathname.getName();
			return pathName.endsWith(".jpg")||pathName.endsWith(".png");
		}
	}

	class JsonFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".json");
		}
	}
}
