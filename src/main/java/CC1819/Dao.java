package CC1819;

import java.util.ArrayList;

public class Dao {
	
	private static Dao dao = null;
	
	private ArrayList<DataObject> viajes = new ArrayList<>();
	private ArrayList<String> noticias = new ArrayList<>();
	
	private Dao() {
		
	}
	
	private void setDao() {
		
		viajes.add(new DataObject("Granada", "Almeria", "21h50", "22h30", 20));
		viajes.add(new DataObject("Granada", "Madrid", "08h00", "13h30", 50));
		viajes.add(new DataObject("Granada", "Barcelona", "00h00", "12h00", 100));
		
		noticias.add("1.000 dias sin tren de Granada a Madrid");
		noticias.add("Habra Talgo de Granada a Madrid");
		noticias.add("Podra haber Tren Hotel de Granada a Barcelona?");

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
		Dao.dao = null;
	}
	
}
