package CC1819;

public class DataObject {
	
	String origen = null;
	String destino = null;
	String partida = null;
	String llegada = null;
	double precio = 0;
	
	public DataObject(){
		
	}
	
	public DataObject(String origen, String destino, String partida, String llegada, double precio) {
		this.origen=origen;
		this.destino=destino;
		this.partida=partida;
		this.llegada=llegada;
		this.precio=precio;
	}
	
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	
	public void setDestino(String destino) {
		this.destino = destino;
	}
	
	public void setPartida(String partida) {
		this.partida = partida;
	}
	
	public void setLlegada(String llegada) {
		this.llegada = llegada;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}
	
	public String getOrigen() {
		return this.origen;
	}
	
	public String getDestino() {
		return this.destino;
	}
	
	public String getPartida() {
		return this.partida;
	}
	
	public String getLlegada() {
		return this.llegada;
	}
	
	public double getPrecio() {
		return this.precio;
	}

}
