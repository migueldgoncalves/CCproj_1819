package CC1819;

import java.time.LocalTime;

public class DataObject {
	
	String origen = null;
	String destino = null;
	LocalTime partida = LocalTime.of(0, 0);
	LocalTime llegada = LocalTime.of(0, 0);
	double precio = 0;
	int id = 0;
	
	public DataObject(String origen, String destino, LocalTime partida, LocalTime llegada, double precio, int id) {
		this.origen=origen;
		this.destino=destino;
		this.partida=partida;
		this.llegada=llegada;
		this.precio=precio;
		this.id=id;
	}
	
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	
	public void setDestino(String destino) {
		this.destino = destino;
	}
	
	public void setPartida(LocalTime partida) {
		this.partida = partida;
	}
	
	public void setLlegada(LocalTime llegada) {
		this.llegada = llegada;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getOrigen() {
		return this.origen;
	}
	
	public String getDestino() {
		return this.destino;
	}
	
	public LocalTime getPartida() {
		return this.partida;
	}
	
	public LocalTime getLlegada() {
		return this.llegada;
	}
	
	public double getPrecio() {
		return this.precio;
	}
	
	public int getId() {
		return this.id;
	}

}
