package CC1819.informacion;

import java.util.HashMap;

import io.javalin.Javalin;

public class JavalinApp {
	
	public JavalinApp() {
		
	}
	
	public Javalin init() {
		
		String portString = System.getenv().get("PORT");
		int port = 7000;
		if(portString!=null)
			port = Integer.parseInt(portString);
		
		Dao dao = Dao.getDao();
		
		Javalin app = Javalin.create().start(port);
		
		app.exception(Exception.class, (e, ctx) -> {
			e.printStackTrace();
			ctx.status(400);
			ctx.result("Pedido invalido");
		});
		
		app.error(404, ctx -> {
			ctx.result("Pagina no existente");
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
		
		app.post("/viajes", ctx -> {
			DataObject viaje = ctx.bodyAsClass(DataObject.class);
			dao.postViaje(viaje.origen, viaje.destino, viaje.partida, viaje.llegada, viaje.precio);
			ctx.status(201);
		});
		
		app.post("/noticias", ctx -> {
			String noticia = ctx.bodyAsClass(String.class);
			dao.postNoticia(noticia);
			ctx.status(201);
		});
		
		app.delete("/viajes/:viaje-id", ctx -> {
			dao.deleteViaje(Integer.parseInt(ctx.pathParam("viaje-id")));
			ctx.status(204);
		});
		
		app.delete("/noticias/:noticia-id", ctx -> {
			dao.deleteNoticia(Integer.parseInt(ctx.pathParam("noticia-id")));
			ctx.status(204);
		});
		
		return app;
		
	}

}