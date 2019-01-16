package CC1819.informacion;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClassInformacionTest {
	
	private Dao dao = null;
	
	@Before
	public void setUp() {
		this.dao = Dao.getDao();
	}
	
	@Test
	public void constructorTest() {
		Assert.assertNotNull(dao);
		
		DataObject viaje0 = dao.findViajeById(0);
		DataObject viaje1 = dao.findViajeById(1);
		DataObject viaje2 = dao.findViajeById(2);
		DataObject viaje3 = dao.findViajeById(3);
		DataObject viaje4 = dao.findViajeById(4);
		Assert.assertNull(viaje0);
		Assert.assertTrue(viaje1.origen.equals("Granada"));
		Assert.assertTrue(viaje2.destino.equals("Armilla"));
		Assert.assertTrue(viaje3.precio==1.65);
		Assert.assertNull(viaje4);
		
		String noticia0 = dao.findNoticiaById(0);
		String noticia1 = dao.findNoticiaById(1);
		String noticia2 = dao.findNoticiaById(2);
		String noticia3 = dao.findNoticiaById(3);
		String noticia4 = dao.findNoticiaById(4);
		Assert.assertNull(noticia0);
		Assert.assertTrue(noticia1.equals("1.000 dias sin tren de Granada a Madrid"));
		Assert.assertTrue(noticia2.equals("Habra Talgo de Granada a Madrid"));
		Assert.assertTrue(noticia3.equals("Podra haber Tren Hotel de Granada a Barcelona?"));
		Assert.assertNull(noticia4);
		
		//Verifica si el patron Singleton esta bien implementado
		
		dao = Dao.getDao();
		Assert.assertNotNull(dao);
		
		viaje0 = dao.findViajeById(0);
		viaje1 = dao.findViajeById(1);
		viaje2 = dao.findViajeById(2);
		viaje3 = dao.findViajeById(3);
		viaje4 = dao.findViajeById(4);
		Assert.assertNull(viaje0);
		Assert.assertTrue(viaje1.origen.equals("Granada"));
		Assert.assertTrue(viaje2.destino.equals("Armilla"));
		Assert.assertTrue(viaje3.precio==1.65);
		Assert.assertNull(viaje4);
		
		noticia0 = dao.findNoticiaById(0);
		noticia1 = dao.findNoticiaById(1);
		noticia2 = dao.findNoticiaById(2);
		noticia3 = dao.findNoticiaById(3);
		noticia4 = dao.findNoticiaById(4);
		Assert.assertNull(noticia0);
		Assert.assertTrue(noticia1.equals("1.000 dias sin tren de Granada a Madrid"));
		Assert.assertTrue(noticia2.equals("Habra Talgo de Granada a Madrid"));
		Assert.assertTrue(noticia3.equals("Podra haber Tren Hotel de Granada a Barcelona?"));
		Assert.assertNull(noticia4);
	}
	
	@Test
	public void postViajeTest() {
		dao.postViaje("Albolote", "Granada", "07h40", "07h52", 1.65);
		DataObject viaje = dao.findViajeById(4);
		Assert.assertTrue(viaje.getOrigen().equals("Albolote"));
		Assert.assertTrue(viaje.getDestino().equals("Granada"));
		Assert.assertTrue(viaje.getPartida().equals("07h40"));
		Assert.assertTrue(viaje.getLlegada().equals("07h52"));
		Assert.assertTrue(viaje.getPrecio()==1.65);
	}
	
	@Test
	public void postNoticiaTest() {
		dao.postNoticia("AVE en Granada en 2019");
		String noticia = dao.findNoticiaById(4);
		Assert.assertTrue(noticia.equals("AVE en Granada en 2019"));
	}
	
	@Test
	public void findInvalidIdViajeTest() {
		Assert.assertNull(dao.findViajeById(-1));
	}
	
	@Test
	public void findInvalidIdNoticiaTest() {
		Assert.assertNull(dao.findNoticiaById(-1));
	}
	
	@Test
	public void getAllViajesTest() {
		Assert.assertTrue(dao.getAllViajes().size()==3);
		dao.postViaje("Albolote", "Granada", "07h40", "07h52", 1.65);
		Assert.assertTrue(dao.getAllViajes().size()==4);
		dao.deleteViaje(4);
		Assert.assertTrue(dao.getAllViajes().size()==4);//Deleting does not remove index, sets value to null
	}
	
	@Test
	public void getAllNoticiasTest() {
		Assert.assertTrue(dao.getAllNoticias().size()==3);
		dao.postNoticia("Noticia");
		Assert.assertTrue(dao.getAllNoticias().size()==4);
		dao.deleteNoticia(4);
		Assert.assertTrue(dao.getAllNoticias().size()==4);//Deleting does not remove index, sets value to null
	}
	
	@Test
	public void deleteViajesTest() {
		dao.deleteViaje(4);
		Assert.assertNull(dao.findViajeById(4));
		Assert.assertTrue(dao.getAllViajes().size()==3);
		dao.deleteViaje(2);
		Assert.assertNull(dao.findViajeById(2));
		Assert.assertNotNull(dao.findViajeById(3));
		Assert.assertTrue(dao.getAllViajes().size()==3);
		dao.deleteViaje(0);
		Assert.assertNull(dao.findViajeById(0));
		Assert.assertTrue(dao.getAllViajes().size()==3);
		dao.deleteViaje(-1);
		Assert.assertNotNull(dao.findViajeById(1));
		Assert.assertTrue(dao.getAllViajes().size()==3);
	}
	
	@Test
	public void deleteNoticiasTest() {
		dao.deleteNoticia(4);
		Assert.assertNull(dao.findNoticiaById(4));
		dao.deleteNoticia(2);
		Assert.assertNull(dao.findNoticiaById(2));
		Assert.assertNotNull(dao.findNoticiaById(3));
		dao.deleteNoticia(0);
		Assert.assertNull(dao.findNoticiaById(0));
		dao.deleteNoticia(-1);
		Assert.assertNotNull(dao.findNoticiaById(1));
	}
	
	@After
	public void tearDown() {
		Dao.cleanDao();
	}

}
