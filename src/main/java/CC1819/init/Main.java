package CC1819.init;

public class Main {
	
	// Variables de entorno aplicables a los microservicios
	public static final String VARIABLE_SERVICIO = "SERVICIO";
	public static final String VARIABLE_URL_INFO = "URL_INFO";
	public static final String VARIABLE_URL_VIAJES = "URL_VIAJES";
	public static final String VARIABLE_PUERTO_INFO = "PORT_INFO";
	public static final String VARIABLE_PUERTO_VIAJES = "PORT_VIAJES";
	
	// Decide que servicio o servicios se ejecutaran
	public static final int SERVICIO_HEROKU = 0;
	public static final int SERVICIO_INFO = 1;
	public static final int SERVICIO_VIAJES = 2;
	public static final int SERVICIO_TODOS = 3;
	public static final int SERVICIO_DEFECTO = SERVICIO_INFO; //Valor por defecto
	
	// Los dos puertos tienen que ser distintos si el URL es igual
	public static final int PUERTO_DEFECTO_INFO = 7000;
	public static final int PUERTO_DEFECTO_VIAJES = 7001;
	
	// URLs
	public static final String URL_BASE_DEFECTO = "http://localhost";
	public static final String URL_INFO_DEFECTO = URL_BASE_DEFECTO + ":" + PUERTO_DEFECTO_INFO;
	public static final String URL_VIAJES_DEFECTO = URL_BASE_DEFECTO + ":" + PUERTO_DEFECTO_VIAJES;
	
	// Datos de la base de datos Mongo
	public static final String MONGO_HOST = "localhost";
	public static final int MONGO_PORT = 27017;
	public static final String DATABASE_NAME = "informacion";
	
	//Puertos y URLs
	public static int puertoInfo = 0;
	public static int puertoViajes = 0;
	public static String urlInfo = null; //Los URLs tienen que empezar con 'http://'
	public static String urlViajes = null;
	
	public static int servicio = 0;
	
	public static void main(String[] args) {
		
		variableSetter();
		
		// Arranca el servicio de informacion al cliente
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
	
	//Cambia las variables estaticas teniendo en cuenta las variables de entorno
	public static void variableSetter() {
		
		// Servicio(s) a ejecutar
		String string = System.getenv().get(VARIABLE_SERVICIO);
		servicio = SERVICIO_DEFECTO;
		if(string!=null)
			servicio = Integer.parseInt(string);
		
		//Puerto del microservicio de informacion al cliente
		string = System.getenv().get(VARIABLE_PUERTO_INFO);
		puertoInfo = PUERTO_DEFECTO_INFO;
		if(string!=null)
			puertoInfo = Integer.parseInt(string);
		
		//Puerto del microservicio de gestion de viajes
		string = System.getenv().get(VARIABLE_PUERTO_VIAJES);
		puertoViajes = PUERTO_DEFECTO_VIAJES;
		if(string!=null)
			puertoViajes = Integer.parseInt(string);
		
		//URL del microservicio de informacion al cliente
		string = System.getenv().get(VARIABLE_URL_INFO);
		urlInfo = URL_BASE_DEFECTO + ":" + puertoInfo;
		if(string!=null)
			urlInfo = string + ":" + puertoInfo;
		
		//URL del microservicio de gestion de viajes
		string = System.getenv().get(VARIABLE_URL_VIAJES);
		urlViajes = URL_BASE_DEFECTO + ":" + puertoViajes;
		if(string!=null)
			urlViajes = string + ":" + puertoViajes;
	}
}
