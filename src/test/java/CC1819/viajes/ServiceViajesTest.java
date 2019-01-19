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
	
	public static final String VARIABLE_PUERTO = CC1819.init.Main.VARIABLE_PUERTO_VIAJES;
	public static final int PUERTO_DEFECTO = CC1819.init.Main.PUERTO_DEFECTO_VIAJES;
	
	public static final int OK = 200;
	public static final int CREATED = 201;
	public static final int NO_CONTENT = 204;
	public static final int BAD_REQUEST = 400;
	public static final int NOT_FOUND = 404;
	
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	private Javalin app = null;
	
	public static final String URL_NOEXISTENTE = "/paginaQueNoExiste";
	public static final String URL_VIAJES = "/viajes";
	public static final String URL_DISPONIBLE = "/disponible";
	public static final String URL_COMPRAR = "/comprar";
	public static final String URL_CANCELAR = "/cancelar";
	
	public static final String SETUP_FALLADO = "Setup de las pruebas fallado";
	
	OkHttpClient client = null;
	
	@Before
	public void setUp() {
		CC1819.init.Main.variableSetter();
		JavalinApp javalinApp = new JavalinApp();
		this.app = javalinApp.init();
		this.client = new OkHttpClient();
		
		//Si el microservicio de informacion al cliente no esta activo en otra maquina
		//se crean 3 viajes en este microservicio
		if(!(CC1819.init.Main.servicio==CC1819.init.Main.SERVICIO_VIAJES &&
						System.getenv().get(CC1819.init.Main.VARIABLE_URL_INFO)!=null)) {
			try {
				RequestBody body = RequestBody.create(JSON, "[]");
				Request request = new Request.Builder().url(CC1819.init.Main.urlViajes+URL_VIAJES).post(body).build();
				// Crear 3 viajes en el microservicio
				client.newCall(request).execute();
				client.newCall(request).execute();
				client.newCall(request).execute();
			} catch (Exception e) {
				System.out.println(SETUP_FALLADO);
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void invalidPathTest() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_NOEXISTENTE;
			Request request = new Request.Builder().url(new URL(URL)).build();
			Response response = client.newCall(request).execute();
			assertEquals(NOT_FOUND, response.code());
			assertEquals(JavalinApp.PAGINA_NO_EXISTENTE, response.body().string());
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void rootPathTest() {
		try {
			String URL = CC1819.init.Main.urlViajes;
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			assertEquals("{\"status\":\"OK\"}", response.body().string());
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void getViajesPathTest() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES;
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
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void getNonExistingViajeTest() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/-1";
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("-1", stringResponse);
			assertEquals(OK, response.code());
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/0";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("-1", stringResponse);
			assertEquals(OK, response.code());
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/4";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("-1", stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void getAViajePathTest() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/1";
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
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void getNonExistingViajeIsDisponible() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/-1"+URL_DISPONIBLE;
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			assertEquals(OK, response.code());
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/0"+URL_DISPONIBLE;
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			assertEquals(OK, response.code());
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/4"+URL_DISPONIBLE;
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void getViajeIsDisponible() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/1"+URL_DISPONIBLE;
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			assertEquals(OK, response.code());
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void postInvalidViajeTest() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES;
			String stringJson = "{\"origen\":\"Granada\","
	                + "}";
			RequestBody body = RequestBody.create(JSON, stringJson);
			Request request = new Request.Builder().url(URL).post(body).build();
			Response response = client.newCall(request).execute();
			assertEquals(CREATED, response.code()); //La funcion de creacion de viaje no recibe argumentos, por eso no falla
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void postViajeTest() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES;
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
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void comprarNoExistenteViaje() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/-1"+URL_COMPRAR;
			RequestBody body = RequestBody.create(JSON, "[]");
			Request request = new Request.Builder().url(URL).put(body).build();
			Response response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(CC1819.init.Main.urlViajes+URL_VIAJES+"/-1"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/0"+URL_COMPRAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(CC1819.init.Main.urlViajes+URL_VIAJES+"/0"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/4"+URL_COMPRAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(CC1819.init.Main.urlViajes+URL_VIAJES+"/4"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void cancelarNoExistenteViaje() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/-1"+URL_CANCELAR;
			RequestBody body = RequestBody.create(JSON, "[]");
			Request request = new Request.Builder().url(URL).put(body).build();
			Response response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(CC1819.init.Main.urlViajes+URL_VIAJES+"/-1"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/0"+URL_CANCELAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(CC1819.init.Main.urlViajes+URL_VIAJES+"/0"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/4"+URL_CANCELAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(CC1819.init.Main.urlViajes+URL_VIAJES+"/4"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void comprarCancelarViaje() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/1"+URL_COMPRAR;
			RequestBody body = RequestBody.create(JSON, "[]");
			Request request = new Request.Builder().url(URL).put(body).build();
			Response response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(CC1819.init.Main.urlViajes+URL_VIAJES+"/1"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			String stringResponse = response.body().string();
			assertEquals("false", stringResponse);
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/1"+URL_CANCELAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			
			request = new Request.Builder().url(CC1819.init.Main.urlViajes+URL_VIAJES+"/1"+URL_DISPONIBLE).build();
			response = client.newCall(request).execute();
			stringResponse = response.body().string();
			assertEquals("true", stringResponse);
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void deleteInvalidViajeTest() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/0";
			Request request = new Request.Builder().url(URL).delete().build();
			Response response = client.newCall(request).execute();
			assertEquals(NO_CONTENT, response.code());
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/4";
			request = new Request.Builder().url(URL).delete().build();
			response = client.newCall(request).execute();
			assertEquals(NO_CONTENT, response.code());
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/1";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("0", response.body().string());
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/3";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("0", response.body().string());
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void deleteViajeTest() {
		try {
			String URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/2";
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
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/1";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("0", response.body().string());
			
			URL = CC1819.init.Main.urlViajes+URL_VIAJES+"/3";
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals("0", response.body().string());
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@After
	public void tearDown() {
		this.app.stop();
		Dao.cleanDao();
	}

}
