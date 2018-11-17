package CC1819;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import io.javalin.Javalin;

public class ServiceTest {
	
	public static final int OK = 200;
	public static final int CREATED = 201;
	public static final int NO_CONTENT = 204;
	
	public static final int NOT_FOUND = 404;
	
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	Javalin app = null;
	
	String URL = "http://localhost:7000";
	
	@Before
	public void setUp() {
		JavalinApp javalinApp = new JavalinApp();
		this.app = javalinApp.init();
	}
	
	@Test
	public void okHttpRootPathTest() {
		try {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			System.out.println("An exception ocurred");
			fail("Test failed");
		}
	}
	
	@Test
	public void usuarioPathTest() {
		try {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(URL+"/viajes").build();
			Response response = client.newCall(request).execute();
			String stringResponse = response.body().string(); //response.body().string() can only be called once
			assertTrue(stringResponse.contains("Almeria"));
			assertTrue(stringResponse.contains("Madrid"));
			assertTrue(stringResponse.contains("Barcelona"));
		}
		catch (Exception e) {
			System.out.println("An exception ocurred");
			fail("Test failed");
		}
	}
	
	@Test
	public void postSimpleTest() {
		try {
			OkHttpClient client = new OkHttpClient();
			String stringJson = "{\"origen\":\"Granada\","
	                + "\"destino\":\"Lisboa\","
	                + "\"partida\":\"12h00\","
	                + "\"llegada\":\"20h00\","
	                + "\"precio\":200.0"
	                + "}";
			System.out.println(stringJson);
			RequestBody body = RequestBody.create(JSON, stringJson);
			Request request = new Request.Builder().url(URL+"/viajes").post(body).build();
			Response response = client.newCall(request).execute();
			//assertEquals(CREATED, response.code());
			request = new Request.Builder().url(URL+"/viajes").build();
			response = client.newCall(request).execute();
			String stringResponse = response.body().string(); //response.body().string() can only be called once
			System.out.println("Print response");
			System.out.println(response);
			assertTrue(stringResponse.contains("Lisboa"));
			assertTrue(stringResponse.contains("Granada"));
			assertTrue(stringResponse.contains("200.0"));
	                
		}
		catch (Exception e) {
			System.out.println("An exception ocurred");
			fail("Test failed");
		}
	}
	
	@After
	public void tearDown() {
		this.app.stop();
	}

}
