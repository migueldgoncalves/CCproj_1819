package CC1819.integracion;

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

public class LocalIntegracionTest {
	
	public static final String VARIABLE_PUERTO_INFO = CC1819.init.Main.VARIABLE_PUERTO_INFO;
	public static final String VARIABLE_PUERTO_VIAJES = CC1819.init.Main.VARIABLE_PUERTO_VIAJES;
	public static final String VARIABLE_URL_INFO = CC1819.init.Main.VARIABLE_URL_INFO;
	public static final String VARIABLE_URL_VIAJES = CC1819.init.Main.VARIABLE_PUERTO_VIAJES;
	public static final String VARIABLE_SERVICIO = CC1819.init.Main.VARIABLE_SERVICIO;
	public static final int SERVICIO_VIAJES = CC1819.init.Main.SERVICIO_VIAJES;
	
	public static final int OK = 200;
	
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private String infoURL = null;
    private String viajesURL = null;
	
	private Javalin infoJavalin = null;
	private Javalin viajesJavalin = null;
	
	public static final String URL_VIAJES = "/viajes";
	public static final String URL_COMPRAR = "/comprar";
	public static final String URL_CANCELAR = "/cancelar";
	
	OkHttpClient client = null;
	
	@Before
	public void setUp() {
		CC1819.init.Main.variableSetter();
		infoURL = CC1819.init.Main.urlInfo;
		viajesURL = CC1819.init.Main.urlViajes;
		
		CC1819.informacion.JavalinApp infoApp = new CC1819.informacion.JavalinApp();
		this.infoJavalin = infoApp.init();
		
		this.client = new OkHttpClient();
	}
	
	@Test
	public void integracionTest() {
		
		try {
			
			// Dos microservicios en la misma maquina
			if(CC1819.init.Main.servicio==CC1819.init.Main.SERVICIO_TODOS) {
			
			// Estado inicial
			assertEquals(3, CC1819.informacion.Dao.getDao().getViajesNumber());
			assertFalse(CC1819.informacion.Dao.getDao().getMicroservicioActivo());
			Request request = new Request.Builder().url(infoURL).build();
			Response response = client.newCall(request).execute();
			assertEquals(OK, response.code());
			assertEquals("{\"status\":\"OK\"}", response.body().string());
		
			// Inicializar microservicio de gestion de viajes
			CC1819.viajes.Dao.pruebaIntegracionEjecutando();
			CC1819.viajes.JavalinApp viajesApp = new CC1819.viajes.JavalinApp();
			this.viajesJavalin = viajesApp.init();
			
			request = new Request.Builder().url(viajesURL).build();
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
			String stringJson = "{\"origen\":\"Granada\","
	                + "\"destino\":\"Aeropuerto\","
	                + "\"partida\":\"06h52\","
	                + "\"llegada\":\"07h15\","
	                + "\"precio\":1.85"
	                + "}";
			RequestBody body = RequestBody.create(JSON, stringJson);
			request = new Request.Builder().url(infoURL+URL_VIAJES).post(body).build();
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
			request = new Request.Builder().url(infoURL+URL_VIAJES+"/4").delete().build();
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
			stringJson = "{\"origen\":\"Aeropuerto\","
	                + "\"destino\":\"Granada\","
	                + "\"partida\":\"06h52\","
	                + "\"llegada\":\"07h15\","
	                + "\"precio\":1.85"
	                + "}";
			body = RequestBody.create(JSON, stringJson);
			request = new Request.Builder().url(infoURL+URL_VIAJES).post(body).build();
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
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(viajesURL+URL_VIAJES+"/3"+URL_COMPRAR).put(body).build();
			client.newCall(request).execute();
			assertEquals(5, CC1819.viajes.Dao.getDao().getViajesNumber());
			assertTrue(CC1819.viajes.Dao.getDao().findViajeById(3)>=1 && CC1819.viajes.Dao.getDao().findViajeById(3)<=CC1819.viajes.Dao.NUM_ASIENTOS);
			assertFalse(CC1819.viajes.Dao.getDao().isNotBought(3));
			
			// Borrar viaje comprada
			request = new Request.Builder().url(infoURL+URL_VIAJES+"/3").delete().build();
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
			body = RequestBody.create(JSON, "[]");
			request = new Request.Builder().url(viajesURL+URL_VIAJES+"/3"+URL_CANCELAR).put(body).build();
			client.newCall(request).execute();
			assertEquals(5, CC1819.viajes.Dao.getDao().getViajesNumber());
			assertEquals(CC1819.viajes.Dao.ASIENTO_DEFECTO, CC1819.viajes.Dao.getDao().findViajeById(3));
			assertTrue(CC1819.viajes.Dao.getDao().isNotBought(3));
			
			// Reintentar borrar el viaje
			request = new Request.Builder().url(infoURL+URL_VIAJES+"/3").delete().build();
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
			
			}
			
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	@After
	public void tearDown() {
		if(this.infoJavalin!=null)
			this.infoJavalin.stop();
		if(this.viajesJavalin!=null)
			this.viajesJavalin.stop();
		CC1819.informacion.Dao.cleanDao();
		CC1819.viajes.Dao.cleanDao();
	}
}