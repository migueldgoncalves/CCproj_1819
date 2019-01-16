package CC1819.integracion;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import io.javalin.Javalin;

public class IntegracionTest {
	
	private static final Logger LOGGER = Logger.getLogger( IntegracionTest.class.getName() );
	
	public static final String VARIABLE_PUERTO_INFO = CC1819.informacion.JavalinApp.VARIABLE_PUERTO;
	public static final int PUERTO_DEFECTO_INFO = CC1819.informacion.JavalinApp.PUERTO_DEFECTO; //Distinto del puerto del microservicio de viajes
	public static final String VARIABLE_PUERTO_VIAJES = CC1819.viajes.JavalinApp.VARIABLE_PUERTO;
	public static final int PUERTO_DEFECTO_VIAJES = CC1819.viajes.JavalinApp.PUERTO_DEFECTO; //Distinto del puerto del microservicio de informacion
	
	public static final int OK = 200;
	public static final int CREATED = 201;
	public static final int NO_CONTENT = 204;
	public static final int BAD_REQUEST = 400;
	public static final int NOT_FOUND = 404;
	
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private int infoPort = 0;
    private int viajesPort = 0;
	
	private Javalin infoJavalin = null;
	private Javalin viajesJavalin = null;
	
	public static final String URL_TEMPLATE = "http://localhost:";
	public static final String URL_NOEXISTENTE = "/paginaQueNoExiste";
	public static final String URL_VIAJES = "/viajes";
	public static final String URL_DISPONIBLE = "/disponible";
	public static final String URL_COMPRAR = "/comprar";
	public static final String URL_CANCELAR = "/cancelar";
	
	public static final String EXCEPCION = "Una excepcion ha ocurrido";
	public static final String INTEGRACION_FALLADA = "Integracion fallada";
	
	OkHttpClient client = null;
	
	@Before
	public void setUp() {
		LOGGER.log( Level.SEVERE, "processing 0 entries in loop");
		String portInfoString = System.getenv().get(VARIABLE_PUERTO_INFO);
		this.infoPort = PUERTO_DEFECTO_INFO;
		if(portInfoString!=null)
			this.infoPort = Integer.parseInt(portInfoString);
		
		String portViajesString = System.getenv().get(VARIABLE_PUERTO_VIAJES);
		this.viajesPort = PUERTO_DEFECTO_VIAJES;
		if(portViajesString!=null)
			this.viajesPort = Integer.parseInt(portViajesString);
		
		CC1819.informacion.JavalinApp infoApp = new CC1819.informacion.JavalinApp();
		this.infoJavalin = infoApp.init();
		
		this.client = new OkHttpClient();
	}
	
	@Test
	public void integracionTest() {
		
		try {
		
			// Estado inicial
			assertEquals(3, CC1819.informacion.Dao.getDao().getViajesNumber());
			assertFalse(CC1819.informacion.Dao.getDao().getMicroservicioActivo());
			String URL = URL_TEMPLATE+infoPort;
			Request request = new Request.Builder().url(URL).build();
			Response response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			assertEquals("{\"status\":\"OK\"}", response.body().string());
		
			// Inicializar microservicio de gestion de viajes
			CC1819.viajes.Dao.pruebaIntegracionEjecutando();
			CC1819.viajes.JavalinApp viajesApp = new CC1819.viajes.JavalinApp();
			this.viajesJavalin = viajesApp.init();
			
			URL = URL_TEMPLATE+viajesPort;
			request = new Request.Builder().url(URL).build();
			response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			assertEquals("{\"status\":\"OK\"}", response.body().string());
			
			assertEquals(3, CC1819.viajes.Dao.getDao().getViajesNumber());
			for(int i=1; i<=3; i++) {
				assertEquals(CC1819.viajes.Dao.ASIENTO_DEFECTO, CC1819.viajes.Dao.getDao().findViajeById(i));
				assertTrue(CC1819.viajes.Dao.getDao().isNotBought(i));
			}
			assertEquals(CC1819.viajes.Dao.ASIENTO_NO_VIAJE, CC1819.viajes.Dao.getDao().findViajeById(-1));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(-1));
			assertEquals(CC1819.viajes.Dao.ASIENTO_NO_VIAJE, CC1819.viajes.Dao.getDao().findViajeById(4));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(4));
			assertTrue(CC1819.informacion.Dao.getDao().getMicroservicioActivo());
		
			// Crear nueva viaje
			URL = URL_TEMPLATE+infoPort+URL_VIAJES;
			String stringJson = "{\"origen\":\"Granada\","
	                + "\"destino\":\"Aeropuerto\","
	                + "\"partida\":\"06h52\","
	                + "\"llegada\":\"07h15\","
	                + "\"precio\":1.85"
	                + "}";
			RequestBody body = RequestBody.create(JSON, stringJson);
			request = new Request.Builder().url(URL).post(body).build();
			client.newCall(request).execute();
			assertTrue(CC1819.informacion.Dao.getDao().getMicroservicioActivo());
			assertEquals(4, CC1819.informacion.Dao.getDao().getViajesNumber());
			assertEquals(4, CC1819.viajes.Dao.getDao().getViajesNumber());
			assertEquals(CC1819.viajes.Dao.ASIENTO_DEFECTO, CC1819.viajes.Dao.getDao().findViajeById(3));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(3));
			assertEquals(CC1819.viajes.Dao.ASIENTO_DEFECTO, CC1819.viajes.Dao.getDao().findViajeById(4));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(4));
			assertEquals(CC1819.viajes.Dao.ASIENTO_NO_VIAJE, CC1819.viajes.Dao.getDao().findViajeById(5));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(5));
			
			// Borrar viaje no comprada
			URL = URL_TEMPLATE+infoPort+URL_VIAJES+"/4";
			request = new Request.Builder().url(URL).delete().build();
			client.newCall(request).execute();
			assertTrue(CC1819.informacion.Dao.getDao().getMicroservicioActivo());
			assertEquals(4, CC1819.informacion.Dao.getDao().getViajesNumber());
			assertEquals(4, CC1819.viajes.Dao.getDao().getViajesNumber());
			assertEquals(CC1819.viajes.Dao.ASIENTO_DEFECTO, CC1819.viajes.Dao.getDao().findViajeById(3));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(3));
			assertEquals(CC1819.viajes.Dao.ASIENTO_NO_VIAJE, CC1819.viajes.Dao.getDao().findViajeById(4));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(4));
			assertEquals(CC1819.viajes.Dao.ASIENTO_NO_VIAJE, CC1819.viajes.Dao.getDao().findViajeById(5));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(5));
			
			// Crear segundo viaje
			URL = URL_TEMPLATE+infoPort+URL_VIAJES;
			stringJson = "{\"origen\":\"Aeropuerto\","
	                + "\"destino\":\"Granada\","
	                + "\"partida\":\"06h52\","
	                + "\"llegada\":\"07h15\","
	                + "\"precio\":1.85"
	                + "}";
			body = RequestBody.create(JSON, stringJson);
			request = new Request.Builder().url(URL).post(body).build();
			client.newCall(request).execute();
			assertTrue(CC1819.informacion.Dao.getDao().getMicroservicioActivo());
			assertEquals(5, CC1819.informacion.Dao.getDao().getViajesNumber());
			assertEquals(5, CC1819.viajes.Dao.getDao().getViajesNumber());
			assertEquals(CC1819.viajes.Dao.ASIENTO_DEFECTO, CC1819.viajes.Dao.getDao().findViajeById(3));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(3));
			assertEquals(CC1819.viajes.Dao.ASIENTO_NO_VIAJE, CC1819.viajes.Dao.getDao().findViajeById(4));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(4));
			assertEquals(CC1819.viajes.Dao.ASIENTO_DEFECTO, CC1819.viajes.Dao.getDao().findViajeById(5));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(5));
			assertEquals(CC1819.viajes.Dao.ASIENTO_NO_VIAJE, CC1819.viajes.Dao.getDao().findViajeById(6));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(6));
			
			// Comprar viaje
			URL = URL_TEMPLATE+viajesPort+URL_VIAJES+"/3"+URL_COMPRAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			client.newCall(request).execute();
			assertEquals(5, CC1819.viajes.Dao.getDao().getViajesNumber());
			assertTrue(CC1819.viajes.Dao.getDao().findViajeById(3)>=1 && CC1819.viajes.Dao.getDao().findViajeById(3)<=CC1819.viajes.Dao.NUM_ASIENTOS);
			assertFalse(CC1819.viajes.Dao.getDao().isNotBought(3));
			
			// Borrar viaje comprada
			URL = URL_TEMPLATE+infoPort+URL_VIAJES+"/3";
			request = new Request.Builder().url(URL).delete().build();
			client.newCall(request).execute();
			assertTrue(CC1819.informacion.Dao.getDao().getMicroservicioActivo());
			assertEquals(5, CC1819.informacion.Dao.getDao().getViajesNumber());
			assertEquals(5, CC1819.viajes.Dao.getDao().getViajesNumber());
			assertTrue(CC1819.viajes.Dao.getDao().findViajeById(3)>=1 && CC1819.viajes.Dao.getDao().findViajeById(3)<=CC1819.viajes.Dao.NUM_ASIENTOS);
			assertFalse(CC1819.viajes.Dao.getDao().isNotBought(3));
			assertEquals(CC1819.viajes.Dao.ASIENTO_DEFECTO, CC1819.viajes.Dao.getDao().findViajeById(2));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(2));
			assertEquals(CC1819.viajes.Dao.ASIENTO_NO_VIAJE, CC1819.viajes.Dao.getDao().findViajeById(4));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(4));
			assertEquals(0, CC1819.viajes.Dao.getDao().findViajeById(5));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(5));
			
			// Cancelar viaje
			URL = URL_TEMPLATE+viajesPort+URL_VIAJES+"/3"+URL_CANCELAR;
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(URL).put(body).build();
			client.newCall(request).execute();
			assertEquals(5, CC1819.viajes.Dao.getDao().getViajesNumber());
			assertEquals(CC1819.viajes.Dao.ASIENTO_DEFECTO, CC1819.viajes.Dao.getDao().findViajeById(3));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(3));
			
			// Reintentar borrar el viaje
			URL = URL_TEMPLATE+infoPort+URL_VIAJES+"/3";
			request = new Request.Builder().url(URL).delete().build();
			client.newCall(request).execute();
			assertTrue(CC1819.informacion.Dao.getDao().getMicroservicioActivo());
			assertEquals(5, CC1819.informacion.Dao.getDao().getViajesNumber());
			assertEquals(5, CC1819.viajes.Dao.getDao().getViajesNumber());
			assertEquals(CC1819.viajes.Dao.ASIENTO_NO_VIAJE, CC1819.viajes.Dao.getDao().findViajeById(3));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(3));
			assertEquals(CC1819.viajes.Dao.ASIENTO_DEFECTO, CC1819.viajes.Dao.getDao().findViajeById(2));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(2));
			assertEquals(CC1819.viajes.Dao.ASIENTO_NO_VIAJE, CC1819.viajes.Dao.getDao().findViajeById(4));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(4));
			assertEquals(CC1819.viajes.Dao.ASIENTO_DEFECTO, CC1819.viajes.Dao.getDao().findViajeById(5));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(5));
			
		} catch (Exception e) {
			System.out.println(EXCEPCION);
			fail(INTEGRACION_FALLADA);
		}
	}

	@After
	public void tearDown() {
		this.infoJavalin.stop();
		this.viajesJavalin.stop();
		CC1819.informacion.Dao.cleanDao();
		CC1819.viajes.Dao.cleanDao();
	}
}