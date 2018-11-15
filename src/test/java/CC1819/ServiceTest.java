package CC1819;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import io.javalin.Javalin;

import java.net.*;

public class ServiceTest {
	
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
			assertEquals(response.code(), 200);
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
	
	@After
	public void tearDown() {
		this.app.stop();
	}

}
