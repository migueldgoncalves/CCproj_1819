package CC1819.viajes;

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

public class ServiceViajesTest {
	
	public static final String VARIABLE_PUERTO = "PORT_VIAJES";
	public static final int PUERTO_DEFECTO = 7001; //Distinto del puerto del microservicio de viajes
	
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
	public static final String URL_DISPONIBLE = "/disponible";
	public static final String URL_COMPRAR = "/comprar";
	public static final String URL_CANCELAR = "/cancelar";
	
	public static final String EXCEPCION = "Una excepcion ha ocurrido";
	public static final String PRUEBA_FALLADA = "Prueba fallada";
	public static final String SETUP_FALLADO = "Setup de las pruebas fallado";
	
	OkHttpClient client = null;
	
	@Before
	public void setUp() {
		String portString = System.getenv().get(VARIABLE_PUERTO);
		this.port = PUERTO_DEFECTO;
		if(portString!=null)
			port = Integer.parseInt(portString);
		
		JavalinApp javalinApp = new JavalinApp();
		this.app = javalinApp.init();
		this.client = new OkHttpClient();
		
		try {
			RequestBody body = RequestBody.create(JSON, "[]");
			Request request = new Request.Builder().url(URL_TEMPLATE+port+URL_VIAJES).post(body).build();
			// Crear 3 viajes en el microservicio
			client.newCall(request).execute();
			client.newCall(request).execute();
			client.newCall(request).execute();
		} catch (Exception e) {
			System.out.println(SETUP_FALLADO);
			e.printStackTrace();
		}
	}
	
	@Test
	public void invalidPathTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_NOEXISTENTE;
			Request request = new Request.Builder().url(new URL(URL)).build();
			Response response = client.newCall(request).execute();
			assertEquals(NOT_FOUND, response.code());
			assertEquals(JavalinApp.PAGINA_NO_EXISTENTE, response.body().string());
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
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
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
		}
	}
	
	@Test
	public void getViajesPathTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES;
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringResponse = response.body().string(); //response.body().string() can only be called once
			assertEquals("[0,0,0]", stringResponse);
			assertEquals(OK, response.code());
			
			//Verificar la idempotencia del metodo
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("[0,0,0]", stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
		}
	}
	
	@Test
	public void getNonExistingViajeTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES+"/-1";
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("-1", stringResponse);
			assertEquals(OK, response.code());
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/0";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("-1", stringResponse);
			assertEquals(OK, response.code());
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/4";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("-1", stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
		}
	}
	
	@Test
	public void getAViajePathTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES+"/1";
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("0", stringResponse);
			assertEquals(OK, response.code());
			
			//Verificar la idempotencia del metodo
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("0", stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
		}
	}
	
	@Test
	public void getNonExistingViajeIsDisponible() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES+"/-1"+URL_DISPONIBLE;
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			assertEquals(OK, response.code());
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/0"+URL_DISPONIBLE;
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			assertEquals(OK, response.code());
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/4"+URL_DISPONIBLE;
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
		}
	}
	
	@Test
	public void getViajeIsDisponible() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES+"/1"+URL_DISPONIBLE;
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
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
			assertEquals(CREATED, response.code()); //La funcion de creacion de viaje no recibe argumentos, por eso no falla
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
		}
	}
	
	@Test
	public void postViajeTest() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES;
			RequestBody body = RequestBody.create(JSON, "[]");
			Request request = new Request.Builder().url(URL).post(body).build();
			Response response = client.newCall(request).execute();
			assertEquals(CREATED, response.code());
			
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("[0,0,0,0]", stringResponse);
			
			request = new Request.Builder().url(URL+"/4").build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("0", stringResponse);
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
		}
	}
	
	@Test
	public void comprarNoExistenteViaje() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES+"/-1"+URL_COMPRAR;
			RequestBody body = RequestBody.create(JSON, "[]");
			Request request = new Request.Builder().url(URL).put(body).build();
			Response response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(URL_TEMPLATE+port+URL_VIAJES+"/-1"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/0"+URL_COMPRAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(URL_TEMPLATE+port+URL_VIAJES+"/0"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/4"+URL_COMPRAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(URL_TEMPLATE+port+URL_VIAJES+"/4"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
		}
	}
	
	@Test
	public void cancelarNoExistenteViaje() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES+"/-1"+URL_CANCELAR;
			RequestBody body = RequestBody.create(JSON, "[]");
			Request request = new Request.Builder().url(URL).put(body).build();
			Response response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(URL_TEMPLATE+port+URL_VIAJES+"/-1"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/0"+URL_CANCELAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(URL_TEMPLATE+port+URL_VIAJES+"/0"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/4"+URL_CANCELAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(URL_TEMPLATE+port+URL_VIAJES+"/4"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
		}
	}
	
	@Test
	public void comprarCancelarViaje() {
		try {
			String URL = URL_TEMPLATE+port+URL_VIAJES+"/1"+URL_COMPRAR;
			RequestBody body = RequestBody.create(JSON, "[]");
			Request request = new Request.Builder().url(URL).put(body).build();
			Response response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(URL_TEMPLATE+port+URL_VIAJES+"/1"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("false", stringResponse);
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/1"+URL_CANCELAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(URL_TEMPLATE+port+URL_VIAJES+"/1"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
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
			assertEquals("0", response.body().string());
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/3";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("0", response.body().string());
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
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
			assertEquals("-1", response.body().string());
			assertEquals(OK, response.code());
			
			//Verificar la idempotencia del metodo
			request = new Request.Builder().url(URL).delete().build();
			response = client.newCall(request).execute();
			assertEquals(NO_CONTENT, response.code());
			
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("-1", response.body().string());
			assertEquals(OK, response.code());
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/1";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("0", response.body().string());
			
			URL = URL_TEMPLATE+port+URL_VIAJES+"/3";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("0", response.body().string());
		}
		catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(PRUEBA_FALLADA);
		}
	}
	
	@After
	public void tearDown() {
		this.app.stop();
		Dao.cleanDao();
	}

}
