package CC1819.informacion;

import java.util.ArrayList;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.MongoClient;
import org.bson.Document;

import java.util.concurrent.atomic.AtomicInteger;

public class Dao {
	
	public static final String HOST = "localhost";
	public static final int MONGO_PORT = 27017;
	public static final String DATABASE_NAME = "informacion";
	public static final String VIAJES_COLLECTION = "viajes";
	public static final String NOTICIAS_COLLECTION = "noticias";
	
	public static final String ID_COLUMN = "id";
	public static final String ORIGEN_COLUMN = "origen";
	public static final String DESTINO_COLUMN = "destino";
	public static final String PARTIDA_COLUMN = "partida";
	public static final String LLEGADA_COLUMN = "llegada";
	public static final String PRECIO_COLUMN = "precio";
	public static final String NOTICIA_COLUMN = "noticia";
	
	private static Dao dao = null;
	
	private MongoDatabase db = null;
	
	private AtomicInteger counterViajes = new AtomicInteger(0);
	private AtomicInteger counterNoticias = new AtomicInteger(0);
	
	private Dao() {
		
	}
	
	private void setDao() {
		
		setDatabase();
		
		postViaje("Granada", "Maracena", "08h04", "08h10", 1.50);
		postViaje("Granada", "Armilla", "09h16", "09h26", 1.50);
		postViaje("Granada", "Huetor Vega", "17h28", "17h40", 1.65);
		
		postNoticia("1.000 dias sin tren de Granada a Madrid");
		postNoticia("Habra Talgo de Granada a Madrid");
		postNoticia("Podra haber Tren Hotel de Granada a Barcelona?");

	}
	
	public static Dao getDao() {
		
		if(Dao.dao!=null)
			return Dao.dao;
		
		Dao.dao=new Dao();
		
		Dao.dao.setDao();
		
		return Dao.dao;
			
	}
	
	public void postViaje(String origen, String destino, String partida, String llegada, double precio) {
		
		counterViajes.incrementAndGet();
		
		// Documents funcionan como lineas de una tabla en una base de datos relacional
		Document document = new Document(ID_COLUMN, counterViajes)
				.append(ORIGEN_COLUMN, origen)
				.append(DESTINO_COLUMN, destino)
				.append(PARTIDA_COLUMN, partida)
				.append(LLEGADA_COLUMN, llegada)
				.append(PRECIO_COLUMN, precio);
		db.getCollection(VIAJES_COLLECTION).insertOne(document);
	}
	
	public void postNoticia(String texto) {
		
		counterNoticias.incrementAndGet();
		
		Document document = new Document(ID_COLUMN, counterNoticias)
				.append(NOTICIA_COLUMN, texto);
		db.getCollection(NOTICIAS_COLLECTION).insertOne(document);
	}
	
	public DataObject findViajeById(int id) {
		if (0<id && id<=counterViajes.get()) {
			MongoCursor<Document> cursor = db.getCollection(VIAJES_COLLECTION).find(Filters.eq(ID_COLUMN, id)).iterator();
			if(cursor.hasNext()) {
				Document document = cursor.next();
				String origen = document.getString(ORIGEN_COLUMN);
				String destino = document.getString(DESTINO_COLUMN);
				String partida = document.getString(PARTIDA_COLUMN);
				String llegada = document.getString(LLEGADA_COLUMN);
				double precio = document.getDouble(PRECIO_COLUMN);
				cursor.close();
				return new DataObject(origen, destino, partida, llegada, precio);
			}
			cursor.close();
		}
		return null;
	}
	
	public String findNoticiaById(int id) {
		if (0<id && id<=counterNoticias.get()) {
			MongoCursor<Document> cursor = db.getCollection(NOTICIAS_COLLECTION).find(Filters.eq(ID_COLUMN, id)).iterator();
			if(cursor.hasNext()) {
				Document document = cursor.next();
				String noticia = document.getString(NOTICIA_COLUMN);
				cursor.close();
				return noticia;
			}
			cursor.close();
		}
		return null;
	}
	
	public ArrayList<DataObject> getAllViajes() {
		ArrayList<DataObject> viajes = new ArrayList<>();
		for(int i=1; i<=counterViajes.get(); i++)
			viajes.add(findViajeById(i));
		return viajes;
	}
	
	public ArrayList<String> getAllNoticias() {
		ArrayList<String> noticias = new ArrayList<>();
		for(int i=1; i<=counterNoticias.get(); i++)
			noticias.add(findNoticiaById(i));
		return noticias;
	}
	
	public void deleteViaje(int id) {
		if (0<id && id<=counterViajes.get())
			db.getCollection(VIAJES_COLLECTION).deleteOne(Filters.eq(ID_COLUMN, id));
	}
	
	public void deleteNoticia(int id) {
		if (0<id && id<=counterNoticias.get())
			db.getCollection(NOTICIAS_COLLECTION).deleteOne(Filters.eq(ID_COLUMN, id));
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
		db.createCollection(NOTICIAS_COLLECTION);
	}
	
}
