package CC1819.init;

import CC1819.informacion.JavalinApp;

public class Main {
	
	// Decide que servicio o servicios se ejecutaran
	public static final String VARIABLE_SERVICIO = "SERVICIO";
	
	public static final int SERVICIO_INFO = 1;
	public static final int SERVICIO_VIAJES = 2;
	public static final int SERVICIO_TODOS = 3;
	
	public static void main(String[] args) {
		
		String servicioString = System.getenv().get(VARIABLE_SERVICIO);
		int servicio = SERVICIO_INFO; //Valor por defecto
		if(servicioString!=null)
			servicio = Integer.parseInt(servicioString);
		
		// Arranca el microservicio de informacion al cliente
		if(servicio==1 || servicio==3) {
			CC1819.informacion.JavalinApp app = new CC1819.informacion.JavalinApp();
			app.init();
		}
		
		//Arranca el microservicio de gestion de viajes
		if(servicio==2 || servicio==3) {
			
		}
	}

}
