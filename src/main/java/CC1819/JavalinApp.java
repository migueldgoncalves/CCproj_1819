package CC1819;

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
		});
		
		app.error(404, ctx -> {
			ctx.result("not found");
		});
		
		app.get("/", ctx -> ctx.result("Hello World"));
		
		app.get("/viajes", ctx -> {
			ctx.json(dao.getAllViajes());
		});
		
		app.get("viajes/:viaje-id", ctx -> {
			ctx.json(dao.findById(Integer.parseInt(ctx.pathParam("viaje-id"))));
		});
		
		
		app.post("/viajes", ctx -> {
			DataObject viaje = ctx.bodyAsClass(DataObject.class);
			dao.post(viaje.origen, viaje.destino, viaje.partida, viaje.llegada, viaje.precio);
			ctx.status(201);
		});
		
		app.patch("viajes/:viaje-id", ctx -> {
			DataObject viaje = ctx.bodyAsClass(DataObject.class);
			dao.update(viaje.origen, viaje.destino, viaje.partida, viaje.llegada, viaje.precio, Integer.parseInt(ctx.pathParam("viaje-id")));
			ctx.status(204);
		});
		
		app.delete("/viajes/:viaje-id", ctx -> {
			dao.delete(Integer.parseInt(ctx.pathParam("viaje-id")));
			ctx.status(204);
		});
		
		return app;
		
	}

}