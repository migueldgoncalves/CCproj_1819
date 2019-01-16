package CC1819.informacion;

import java.util.HashMap;

import io.javalin.Javalin;

public class JavalinAppHeroku {
	
	public static final String VARIABLE_PUERTO = "PORT_INFO";
	public static final String VARIABLE_URL = "URL_INFO";
	public static final int PUERTO_DEFECTO = 7000; //Distinto del puerto del microservicio de viajes
	public static final String URL_INFO_DEFECTO = "http://localhost:" + PUERTO_DEFECTO;
	
	public static final int OK = 200;
	public static final int CREATED = 201;
	public static final int NO_CONTENT = 204;
	public static final int BAD_REQUEST = 400;
	public static final int NOT_FOUND = 404;
	
	public static final String PEDIDO_INVALIDO = "Pedido invalido";
	public static final String PAGINA_NO_EXISTENTE = "Pagina no existente";
	
	public JavalinAppHeroku() {
		
	}
	
	public Javalin init() {
		
		String portString = System.getenv().get(VARIABLE_PUERTO);
		int port = PUERTO_DEFECTO;
		if(portString!=null)
			port = Integer.parseInt(portString);
		
		DaoHeroku dao = DaoHeroku.getDao(); //Principal diferencia con el JavalinApp estandar
		
		Javalin app = Javalin.create().start(port);
		
		app.exception(Exception.class, (e, ctx) -> {
			e.printStackTrace();
			ctx.status(BAD_REQUEST);
			ctx.result(PEDIDO_INVALIDO);
		});
		
		app.error(NOT_FOUND, ctx -> {
			ctx.result(PAGINA_NO_EXISTENTE);
		});
		
		HashMap<String, String> hash = new HashMap<String, String>();
		hash.put("status", "OK");
		app.get("/", ctx -> ctx.json(hash));
		
		app.get("/viajes", ctx -> {
			ctx.json(dao.getAllViajes());
		});
		
		app.get("/noticias", ctx -> {
			ctx.json(dao.getAllNoticias());
		});
		
		app.get("viajes/:viaje-id", ctx -> {
			ctx.json(dao.findViajeById(Integer.parseInt(ctx.pathParam("viaje-id"))));
		});
		
		app.get("noticias/:noticia-id", ctx -> {
			ctx.json(dao.findNoticiaById(Integer.parseInt(ctx.pathParam("noticia-id"))));
		});
		
		app.get("/numero", ctx -> {
			ctx.json(dao.getViajesNumber());
		});
		
		app.post("/viajes", ctx -> {
			DataObject viaje = ctx.bodyAsClass(DataObject.class);
			dao.postViaje(viaje.origen, viaje.destino, viaje.partida, viaje.llegada, viaje.precio);
			ctx.status(CREATED);
		});
		
		app.post("/noticias", ctx -> {
			String noticia = ctx.bodyAsClass(String.class);
			dao.postNoticia(noticia);
			ctx.status(CREATED);
		});
		
		app.delete("/viajes/:viaje-id", ctx -> {
			dao.deleteViaje(Integer.parseInt(ctx.pathParam("viaje-id")));
			ctx.status(NO_CONTENT);
		});
		
		app.delete("/noticias/:noticia-id", ctx -> {
			dao.deleteNoticia(Integer.parseInt(ctx.pathParam("noticia-id")));
			ctx.status(NO_CONTENT);
		});
		
		return app;
		
	}

}