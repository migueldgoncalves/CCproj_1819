package CC1819.init;

import CC1819.informacion.JavalinApp;

public class Main {
	
	// Decide que servicio o servicios se ejecutaran
	public static final String VARIABLE_SERVICIO = "SERVICIO";
	
	public static final int SERVICIO_HEROKU = 0;
	public static final int SERVICIO_INFO = 1;
	public static final int SERVICIO_VIAJES = 2;
	public static final int SERVICIO_TODOS = 3;
	
	public static void main(String[] args) {
		
		String servicioString = System.getenv().get(VARIABLE_SERVICIO);
		int servicio = SERVICIO_INFO; //Valor por defecto
		if(servicioString!=null)
			servicio = Integer.parseInt(servicioString);
		
		// Por defecto - Arranca el servicio de informacion al cliente
		// sin cliente HTTP para funcionar en Heroku
		if(servicio==SERVICIO_HEROKU) {
			CC1819.informacion.JavalinAppHeroku app = new CC1819.informacion.JavalinAppHeroku();
			app.init(); // Se esta ejecutando en Heroku
		}
		
		// Arranca el microservicio de informacion al cliente
		if(servicio==SERVICIO_INFO || servicio==SERVICIO_TODOS) {
			CC1819.informacion.JavalinApp app = new CC1819.informacion.JavalinApp();
			app.init(); // No se esta ejecutando en Heroku
		}
		
		//Arranca el microservicio de gestion de viajes
		if(servicio==SERVICIO_VIAJES || servicio==SERVICIO_TODOS) {
			CC1819.viajes.JavalinApp app = new CC1819.viajes.JavalinApp();
			app.init(); // No se ejecuta en Heroku
		}
	}

}
