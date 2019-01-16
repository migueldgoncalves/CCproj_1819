package CC1819.viajes;

import java.util.HashMap;

import io.javalin.Javalin;

public class JavalinApp {
	
	public static final String VARIABLE_PUERTO = "PORT_VIAJES";
	public static final String VARIABLE_URL = "URL_VIAJES";
	public static final int PUERTO_DEFECTO = 7001; //Distinto del puerto del microservicio de viajes
	public static final String URL_VIAJES_DEFECTO = "http://localhost:" + PUERTO_DEFECTO;
	
	public static final int OK = 200;
	public static final int CREATED = 201;
	public static final int NO_CONTENT = 204;
	public static final int BAD_REQUEST = 400;
	public static final int NOT_FOUND = 404;
	
	public static final String PEDIDO_INVALIDO = "Pedido invalido";
	public static final String PAGINA_NO_EXISTENTE = "Pagina no existente";
	
	public JavalinApp() {
		
	}
	
	public Javalin init() {
		
		String portString = System.getenv().get(VARIABLE_PUERTO);
		int port = PUERTO_DEFECTO;
		if(portString!=null)
			port = Integer.parseInt(portString);
		
		Dao dao = Dao.getDao();
		
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
		
		app.get("/viajes/:viaje-id", ctx -> {
			ctx.json(dao.findViajeById(Integer.parseInt(ctx.pathParam("viaje-id"))));
		});
		
		app.get("/viajes/:viaje-id/disponible", ctx -> {
				ctx.json(dao.isNotBought(Integer.parseInt(ctx.pathParam("viaje-id"))));
		});
		
		app.post("/viajes", ctx -> {
			dao.postViaje();
			ctx.status(CREATED);
		});
		
		app.put("/viajes/:viaje-id/comprar", ctx -> {
			dao.comprarViaje(Integer.parseInt(ctx.pathParam("viaje-id")));
			ctx.status(OK);
		});
		
		app.put("/viajes/:viaje-id/cancelar", ctx -> {
			dao.cancelarCompra(Integer.parseInt(ctx.pathParam("viaje-id")));
			ctx.status(OK);
		});
		
		app.delete("/viajes/:viaje-id", ctx -> {
			dao.deleteViaje(Integer.parseInt(ctx.pathParam("viaje-id")));
			ctx.status(NO_CONTENT);
		});
		
		return app;
		
	}

}
