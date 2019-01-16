package CC1819.viajes;

import java.util.ArrayList;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import CC1819.init.Main;

import com.mongodb.MongoClient;
import org.bson.Document;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class Dao {
	
	public static final String HOST = "localhost";
	public static final int MONGO_PORT = 27017;
	public static final String DATABASE_NAME = "viajes";
	public static final String VIAJES_COLLECTION = "viajes";
	
	public static final String ID_COLUMN = "id";
	public static final String ASIENTO_COLUMN = "asiento";
	
	public static final int ASIENTO_DEFECTO = 0; //Valor del asiento al crearse un viaje
	public static final int ASIENTO_NO_VIAJE = -1; //Asiento retornado si viaje no existe
	
	public static final int NUM_ASIENTOS = 200;
	
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	public static final String ERROR_HTTP = "Error al comunicar con el microservicio de informacion al cliente";
	
	private static Dao dao = null;
	
	private MongoDatabase db = null;
	
	private static boolean pruebaIntegracion = false; //Si la prueba de integracion esta en ejecucion
	
	private AtomicInteger counterViajes = new AtomicInteger(0);
	
	private Dao() {
		
	}
	
	public static Dao getDao() {
		
		if(Dao.dao!=null)
			return Dao.dao;
		
		Dao.dao=new Dao();
		
		Dao.dao.setDatabase();
		
		int servicio = Main.SERVICIO_INFO;
		if(System.getenv().get(Main.VARIABLE_SERVICIO)!=null)
			servicio = Integer.parseInt(System.getenv().get(Main.VARIABLE_SERVICIO));
		
		if(servicio==Main.SERVICIO_TODOS || Dao.dao.pruebaIntegracion) //Si el microservicio de informacion tambien esta funcionando
			Dao.dao.fillDatabase(); //sincroniza con el
		
		return Dao.dao;
			
	}
	
	public static void pruebaIntegracionEjecutando() {
		Dao.pruebaIntegracion = true;
	}
	
	public void fillDatabase() {
		
		String url = CC1819.informacion.JavalinApp.URL_INFO_DEFECTO + "/numero";
		if(System.getenv().get(CC1819.informacion.JavalinApp.VARIABLE_URL)!=null)
			url = System.getenv().get(CC1819.informacion.JavalinApp.VARIABLE_URL) +
					System.getenv().get(CC1819.informacion.JavalinApp.VARIABLE_PUERTO) + "/numero";
		
		OkHttpClient client = new OkHttpClient();
		
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = client.newCall(request).execute();
			int numViajes = Integer.valueOf(response.body().string());
			for(int i=1; i<=numViajes; i++)
				postViaje();
		}
		catch (Exception e) {
			System.out.println(ERROR_HTTP);
			e.printStackTrace();
		}
	}
	
	public void postViaje() {
		
		counterViajes.incrementAndGet();
		
		// Documents funcionan como lineas de una tabla en una base de datos relacional
		Document document = new Document(ID_COLUMN, counterViajes)
				.append(ASIENTO_COLUMN, ASIENTO_DEFECTO);
		db.getCollection(VIAJES_COLLECTION).insertOne(document);
	}
	
	public int findViajeById(int id) {
		if (0<id && id<=counterViajes.get()) {
			MongoCursor<Document> cursor = db.getCollection(VIAJES_COLLECTION).find(Filters.eq(ID_COLUMN, id)).iterator();
			if(cursor.hasNext()) {
				Document document = cursor.next();
				return document.getInteger(ASIENTO_COLUMN);
			}
			cursor.close();
		}
		return ASIENTO_NO_VIAJE;
	}
	
	public ArrayList<Integer> getAllViajes() {
		ArrayList<Integer> viajes = new ArrayList<>();
		for(int i=1; i<=counterViajes.get(); i++)
			viajes.add(findViajeById(i));
		return viajes;
	}
	
	public int getViajesNumber() {
		return getAllViajes().size();
	}
	
	public void comprarViaje(int id) {
		
		Random random = new Random();
		// nextInt retorna numeros en el rango [0; parametro[
		int asiento = random.nextInt(NUM_ASIENTOS+1) + 1; //Asientos estan en el rango [1; NUM_ASIENTOS]
		
		if(findViajeById(id)==ASIENTO_DEFECTO) {
			db.getCollection(VIAJES_COLLECTION).updateOne(Filters.eq(ID_COLUMN, id), Updates.set(ASIENTO_COLUMN, asiento));
		}
	}
	
	public void cancelarCompra(int id) {
		if(findViajeById(id)>=1 && findViajeById(id)<=NUM_ASIENTOS) {
			db.getCollection(VIAJES_COLLECTION).updateOne(Filters.eq(ID_COLUMN, id), Updates.set(ASIENTO_COLUMN, ASIENTO_DEFECTO));
		}
	}
	
	public boolean isNotBought(int id) {
		return (findViajeById(id)==ASIENTO_DEFECTO || findViajeById(id)==ASIENTO_NO_VIAJE);
	}
	
	public void deleteViaje(int id) {
		if (0<id && id<=counterViajes.get())
			db.getCollection(VIAJES_COLLECTION).deleteOne(Filters.eq(ID_COLUMN, id));
	}
	
	public static void cleanDao() {
		Dao.getDao().db.drop();
		Dao.dao = null;
	}
	
	public void setDatabase() {
		
		//La instancia de Mongo corre en http://<HOST> y escucha en el puerto MONGO_PORT
		MongoClient mongo = new MongoClient(HOST, MONGO_PORT);
		// La base de datos es creada automaticamente si no existe
		db = mongo.getDatabase(DATABASE_NAME);
		// Collections son como tablas en bases de datos relacionales
		db.createCollection(VIAJES_COLLECTION);
	}
	
}