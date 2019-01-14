package CC1819.informacion;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import io.javalin.Javalin;

public class ServiceInformacionTest {
	
	public static final int OK = 200;
	public static final int CREATED = 201;
	public static final int NO_CONTENT = 204;
	public static final int BAD_REQUEST = 400;
	public static final int NOT_FOUND = 404;
	
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private int port = 0;
	
	private Javalin app = null;
	
	public static final String URL_TEMPLATE = "http://localhost:";
	public static final String URL_NOEXISTENTE = "/paginaQueNoExiste";
	public static final String URL_VIAJES = "/viajes";
	public static final String URL_NOTICIAS = "/noticias";
	
	OkHttpClient client = null;
	
	@Before
	public void setUp() {
		String portString = System.getenv().get("PORT");
		this.port = 7000;
		if(portString!=null)
			port = Integer.parseInt(portString);
		
		JavalinApp javalinApp = new JavalinApp();
		this.app = javalinApp.init();
		this.client = new OkHttpClient();
	}
	
	@Test
	public void invalidPathTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_NOEXISTENTE;
			Request request = new Request.Builder().url(new URL(URL)).build();
			Response response = client.newCall(request).execute();
			assertEquals(NOT_FOUND, response.code());
			assertEquals("Pagina no existente", response.body().string());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void rootPathTest() {
		try {
			String URL = URL_TEMPLATE+port;
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			assertEquals("{\"status\":\"OK\"}", response.body().string());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void getViajesPathTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES;
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringJson = "[{\"origen\":\"Granada\","
	                + "\"destino\":\"Maracena\","
	                + "\"partida\":\"08h04\","
	                + "\"llegada\":\"08h10\","
	                + "\"precio\":1.5"
	                + "},"
	    			+ "{\"origen\":\"Granada\","
	    	        + "\"destino\":\"Armilla\","
	    	        + "\"partida\":\"09h16\","
	    	        + "\"llegada\":\"09h26\","
	    	        + "\"precio\":1.5"
	    	        + "},"
	    			+ "{\"origen\":\"Granada\","
	    	        + "\"destino\":\"Huetor Vega\","
	    	        + "\"partida\":\"17h28\","
	    	        + "\"llegada\":\"17h40\","
	    	        + "\"precio\":1.65"
	    	        + "}]";
			String stringResponse = response.body().string(); //response.body().string() can only be called once
			assertEquals(stringJson, stringResponse);
			assertEquals(OK, response.code());
			
			//Verificar la idempotencia del metodo
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals(stringJson, stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void getNoticiasPathTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_NOTICIAS;
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringJson = "[\"1.000 dias sin tren de Granada a Madrid\","
	                + "\"Habra Talgo de Granada a Madrid\","
	                + "\"Podra haber Tren Hotel de Granada a Barcelona?\""
	    	        + "]";
			String stringResponse = response.body().string();
			assertEquals(stringJson, stringResponse);
			assertEquals(OK, response.code());
			
			//Verificar la idempotencia del metodo
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals(stringJson, stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void getAViajePathTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES+"/1";
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringJson = "{\"origen\":\"Granada\","
	                + "\"destino\":\"Maracena\","
	                + "\"partida\":\"08h04\","
	                + "\"llegada\":\"08h10\","
	                + "\"precio\":1.5"
	                + "}";
			String stringResponse = response.body().string();
			assertEquals(stringJson, stringResponse);
			assertEquals(OK, response.code());
			
			//Verificar la idempotencia del metodo
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals(stringJson, stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void getANoticiaPathTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_NOTICIAS+"/2";
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringJson = "\"Habra Talgo de Granada a Madrid\"";
			String stringResponse = response.body().string();
			assertEquals(stringJson, stringResponse);
			assertEquals(OK, response.code());
			
			//Verificar la idempotencia del metodo
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals(stringJson, stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void getNonExistingViajeTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES+"/0";
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("Pedido invalido", stringResponse);
			assertEquals(BAD_REQUEST, response.code());
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/4";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("Pedido invalido", stringResponse);
			assertEquals(BAD_REQUEST, response.code());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void getNonExistingNoticiaTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_NOTICIAS+"/0";
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("Pedido invalido", stringResponse);
			assertEquals(BAD_REQUEST, response.code());
			
			URL = URL_TEMPLATE+port+URL_NOTICIAS+"/4";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("Pedido invalido", stringResponse);
			assertEquals(BAD_REQUEST, response.code());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void postViajeTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES;
			String stringJson = "{\"origen\":\"Granada\","
	                + "\"destino\":\"Aeropuerto\","
	                + "\"partida\":\"06h52\","
	                + "\"llegada\":\"07h15\","
	                + "\"precio\":1.85"
	                + "}";
			RequestBody body = RequestBody.create(JSON, stringJson);
			Request request = new Request.Builder().url(URL).post(body).build();
			Response response = client.newCall(request).execute();
			assertEquals(CREATED, response.code());
			
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertTrue(stringResponse.contains(stringJson));
			
			request = new Request.Builder().url(URL+"/4").build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals(stringJson, stringResponse);
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void postNoticiaTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_NOTICIAS;
			String stringJson = "\"Noticia\"";
			RequestBody body = RequestBody.create(JSON, stringJson);
			Request request = new Request.Builder().url(URL).post(body).build();
			Response response = client.newCall(request).execute();
			assertEquals(CREATED, response.code());
			
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertTrue(stringResponse.contains(stringJson));
			
			request = new Request.Builder().url(URL+"/4").build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals(stringJson, stringResponse);
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void postInvalidViajeTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES;
			String stringJson = "{\"origen\":\"Granada\","
	                + "}";
			RequestBody body = RequestBody.create(JSON, stringJson);
			Request request = new Request.Builder().url(URL).post(body).build();
			Response response = client.newCall(request).execute();
			assertEquals("Pedido invalido", response.body().string());
			assertEquals(BAD_REQUEST, response.code());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void postInvalidNoticiaTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_NOTICIAS;
			String stringJson = "{\"origen\":\"Granada\","
	                + "}";
			RequestBody body = RequestBody.create(JSON, stringJson);
			Request request = new Request.Builder().url(URL).post(body).build();
			Response response = client.newCall(request).execute();
			assertEquals(BAD_REQUEST, response.code());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void deleteViajeTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES+"/2";
			Request request = new Request.Builder().url(URL).delete().build();
			Response response = client.newCall(request).execute();
			assertEquals(NO_CONTENT, response.code());
			
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("Pedido invalido", response.body().string());
			assertEquals(BAD_REQUEST, response.code());
			
			//Verificar la idempotencia del metodo
			request = new Request.Builder().url(URL).delete().build();
			response = client.newCall(request).execute();
			assertEquals(NO_CONTENT, response.code());
			
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("Pedido invalido", response.body().string());
			assertEquals(BAD_REQUEST, response.code());
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/1";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertTrue(response.body().string().contains("Maracena"));
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/3";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertTrue(response.body().string().contains("Huetor Vega"));
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void deleteNoticiaTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_NOTICIAS+"/2";
			Request request = new Request.Builder().url(URL).delete().build();
			Response response = client.newCall(request).execute();
			assertEquals(NO_CONTENT, response.code());
			
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("Pedido invalido", response.body().string());
			assertEquals(BAD_REQUEST, response.code());
			
			//Verificar la idempotencia del metodo
			request = new Request.Builder().url(URL).delete().build();
			response = client.newCall(request).execute();
			assertEquals(NO_CONTENT, response.code());
			
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("Pedido invalido", response.body().string());
			assertEquals(BAD_REQUEST, response.code());
			
			URL = URL_TEMPLATE+port+URL_NOTICIAS+"/1";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("\"1.000 dias sin tren de Granada a Madrid\"", response.body().string());
			
			URL = URL_TEMPLATE+port+URL_NOTICIAS+"/3";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("\"Podra haber Tren Hotel de Granada a Barcelona?\"", response.body().string());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void deleteInvalidViajeTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES+"/0";
			Request request = new Request.Builder().url(URL).delete().build();
			Response response = client.newCall(request).execute();
			assertEquals(NO_CONTENT, response.code());
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/4";
			request = new Request.Builder().url(URL).delete().build();
			response = client.newCall(request).execute();
			assertEquals(NO_CONTENT, response.code());
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/1";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertTrue(response.body().string().contains("Maracena"));
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/3";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertTrue(response.body().string().contains("Huetor Vega"));
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@Test
	public void deleteInvalidNoticiaTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_NOTICIAS+"/0";
			Request request = new Request.Builder().url(URL).delete().build();
			Response response = client.newCall(request).execute();
			assertEquals(NO_CONTENT, response.code());
			
			URL = URL_TEMPLATE+port+URL_NOTICIAS+"/4";
			request = new Request.Builder().url(URL).delete().build();
			response = client.newCall(request).execute();
			assertEquals(NO_CONTENT, response.code());
			
			URL = URL_TEMPLATE+port+URL_NOTICIAS+"/1";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("\"1.000 dias sin tren de Granada a Madrid\"", response.body().string());
			
			URL = URL_TEMPLATE+port+URL_NOTICIAS+"/3";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("\"Podra haber Tren Hotel de Granada a Barcelona?\"", response.body().string());
		}
		catch (Exception e) {
			System.out.println("Una excepcion ha ocurrido");
			fail("Prueba fallada");
		}
	}
	
	@After
	public void tearDown() {
		this.app.stop();
		Dao.cleanDao();
	}

}
