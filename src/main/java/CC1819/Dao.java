package CC1819;

import java.util.ArrayList;

import com.mongodb.client.MongoDatabase; 
import com.mongodb.MongoClient;

import java.util.concurrent.atomic.AtomicInteger;

public class Dao {
	
	public static final String HOST = "localhost";
	public static final int MONGO_PORT = 27017;
	public static final String DATABASE_NAME = "informacion";
	public static final String VIAJES_COLLECTION = "viajes";
	public static final String NOTICIAS_COLLECTION = "noticias";
	
	private static Dao dao = null;
	
	private ArrayList<DataObject> viajes = new ArrayList<>();
	private ArrayList<String> noticias = new ArrayList<>();
	
	private MongoDatabase db = null;
	
	private AtomicInteger counter = new AtomicInteger(1);
	
	private Dao() {
		
	}
	
	private void setDao() {
		
		postViaje("Granada", "Maracena", "08h04", "08h10", 1.50);
		postViaje("Granada", "Armilla", "09h16", "09h26", 1.50);
		postViaje("Granada", "Huetor Vega", "17h28", "17h40", 1.65);
		
		postNoticia("1.000 dias sin tren de Granada a Madrid");
		postNoticia("Habra Talgo de Granada a Madrid");
		postNoticia("Podra haber Tren Hotel de Granada a Barcelona?");
		
		setDatabase();

	}
	
	public static Dao getDao() {
		
		if(Dao.dao!=null)
			return Dao.dao;
		
		Dao.dao=new Dao();
		
		Dao.dao.setDao();
		
		return Dao.dao;
			
	}
	
	public void postViaje(String origen, String destino, String partida, String llegada, double precio) {
		this.viajes.add(new DataObject(origen, destino, partida, llegada, precio));
	}
	
	public void postNoticia(String texto) {
		this.noticias.add(texto);
	}
	
	public DataObject findViajeById(int id) {
		if (0<=id-1 && id-1<viajes.size())
			return this.viajes.get(id-1);
		return null;
	}
	
	public String findNoticiaById(int id) {
		if (0<=id-1 && id-1<noticias.size())
			return this.noticias.get(id-1);
		return null;
	}
	
	public ArrayList<DataObject> getAllViajes() {
		return viajes;
	}
	
	public ArrayList<String> getAllNoticias() {
		return noticias;
	}
	
	public void deleteViaje(int id) {
		if (0<=id-1 && id-1<viajes.size())
			this.viajes.set(id-1, null);
	}
	
	public void deleteNoticia(int id) {
		if (0<=id-1 && id-1<noticias.size())
			this.noticias.set(id-1, null);		
	}
	
	public static void cleanDao() {
		Dao.getDao().db.drop();
		Dao.dao = null;
	}
	
	public void setDatabase() {
		
		//La instancia de Mongo correra en http://<HOST> y escuchara en el puerto MONGO_PORT
		MongoClient mongo = new MongoClient( HOST , MONGO_PORT );
		// La base de datos es creada automaticamente si no existe
		db = mongo.getDatabase(DATABASE_NAME);
		// Collections son como tablas en bases de datos relacionales
		db.createCollection(VIAJES_COLLECTION);
		db.createCollection(NOTICIAS_COLLECTION);
	}
	
}
