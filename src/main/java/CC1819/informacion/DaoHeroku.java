package CC1819.informacion;

import java.util.ArrayList;

public class DaoHeroku {
	
	private static DaoHeroku dao = null;
	
	private ArrayList<DataObject> viajes = new ArrayList<>();
	private ArrayList<String> noticias = new ArrayList<>();
	
	private DaoHeroku() {
		
	}
	
	private void setDao() {
		
		viajes.add(new DataObject("Granada", "Maracena", "08h04", "08h10", 1.50));
		viajes.add(new DataObject("Granada", "Armilla", "09h16", "09h26", 1.50));
		viajes.add(new DataObject("Granada", "Huetor Vega", "17h28", "17h40", 1.65));
		
		noticias.add("1.000 dias sin tren de Granada a Madrid");
		noticias.add("Habra Talgo de Granada a Madrid");
		noticias.add("Podra haber Tren Hotel de Granada a Barcelona?");

	}
	
	public static DaoHeroku getDao() {
		
		if(DaoHeroku.dao!=null)
			return DaoHeroku.dao;
		
		DaoHeroku.dao=new DaoHeroku();
		
		DaoHeroku.dao.setDao();
		
		return DaoHeroku.dao;
			
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
	
	public int getViajesNumber() {
		return viajes.size();
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
		DaoHeroku.dao = null;
	}
	
}
