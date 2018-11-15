package CC1819;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import java.time.LocalTime;

public class Dao {
	
	private static Dao dao = null;
	
	private List<DataObject> viajes = new ArrayList<>();
	
	private static AtomicInteger ultimoId = new AtomicInteger(0);
	
	public Dao() {
	
	}
	
	public static Dao getDao() {
		
		if(dao!=null)
			return dao;
		
		dao=new Dao();
	
		dao.viajes.add(new DataObject("Granada", "Almeria", LocalTime.of(21, 50), LocalTime.of(22, 30), 20, ultimoId.incrementAndGet()));
		dao.viajes.add(new DataObject("Granada", "Madrid", LocalTime.of(8, 00), LocalTime.of(13, 30), 50, ultimoId.incrementAndGet()));
		dao.viajes.add(new DataObject("Granada", "Barcelona", LocalTime.of(00, 00), LocalTime.of(12, 00), 100, ultimoId.incrementAndGet()));
		
		return dao;
			
	}
	
	public void post(String origen, String destino, LocalTime partida, LocalTime llegada, double precio) {
		int id = Dao.ultimoId.incrementAndGet();
		this.viajes.add(new DataObject(origen, destino, partida, llegada, precio, id));
	}
	
	public DataObject findById(int id) {
		for(int i=0; i<viajes.size(); i++)
			if (viajes.get(i).getId()==id)
				return viajes.get(i);
		return null;
	}
	
	public List<DataObject> getAllViajes() {
		return viajes;
	}
	
	public void update(String origen, String destino, LocalTime partida, LocalTime llegada, double precio, int id) {
		DataObject searchedObject = findById(id);
		if(searchedObject!=null) {
			searchedObject.setOrigen(origen);
			searchedObject.setDestino(destino);
			searchedObject.setPartida(partida);
			searchedObject.setLlegada(llegada);
			searchedObject.setPrecio(precio);
		}
	}
	
	public void delete(int id) {
		for(int i=0; i<viajes.size(); i++)
			if (viajes.get(i).getId()==id)
				viajes.remove(i);
	}
	
}
