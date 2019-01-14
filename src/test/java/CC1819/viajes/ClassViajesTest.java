package CC1819.viajes;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClassViajesTest {
	
	private Dao dao = null;
	
	@Before
	public void setUp() {
		this.dao = Dao.getDao();
		dao.postViaje();
		dao.postViaje();
		dao.postViaje();
	}
	
	@Test
	public void constructorTest() {
		Assert.assertNotNull(dao);
		
		int asiento1 = dao.findViajeById(1);
		int asiento2 = dao.findViajeById(2);
		int asiento3 = dao.findViajeById(3);
		Assert.assertTrue(asiento1==Dao.ASIENTO_DEFECTO);
		Assert.assertTrue(asiento2==Dao.ASIENTO_DEFECTO);
		Assert.assertTrue(asiento3==Dao.ASIENTO_DEFECTO);
		
		//Verifica si el patron Singleton esta bien implementado
		
		dao = Dao.getDao();
		Assert.assertNotNull(dao);
		
		asiento1 = dao.findViajeById(1);
		asiento2 = dao.findViajeById(2);
		asiento3 = dao.findViajeById(3);
		Assert.assertTrue(asiento1==Dao.ASIENTO_DEFECTO);
		Assert.assertTrue(asiento2==Dao.ASIENTO_DEFECTO);
		Assert.assertTrue(asiento3==Dao.ASIENTO_DEFECTO);
	}

	@Test
	public void postViajeTest() {
		dao.postViaje();
		int asiento = dao.findViajeById(4);
		Assert.assertTrue(asiento==Dao.ASIENTO_DEFECTO);
	}

	@Test
	public void findViajeByIdTest() {
		Assert.assertTrue(dao.findViajeById(-1)==Dao.ASIENTO_NO_VIAJE);
		Assert.assertTrue(dao.findViajeById(0)==Dao.ASIENTO_NO_VIAJE);
		Assert.assertTrue(dao.findViajeById(1)==Dao.ASIENTO_DEFECTO);
		Assert.assertTrue(dao.findViajeById(2)==Dao.ASIENTO_DEFECTO);
		Assert.assertTrue(dao.findViajeById(3)==Dao.ASIENTO_DEFECTO);
		Assert.assertTrue(dao.findViajeById(4)==Dao.ASIENTO_NO_VIAJE);
		dao.comprarViaje(2);
		Assert.assertTrue(dao.findViajeById(2)>=1 && dao.findViajeById(2)<=Dao.NUM_ASIENTOS);
		dao.cancelarCompra(2);
		Assert.assertTrue(dao.findViajeById(2)==Dao.ASIENTO_DEFECTO);
	}
	
	@Test
	public void getAllViajesTest() {
		Assert.assertTrue(dao.getAllViajes().size()==3);
		dao.postViaje();
		Assert.assertTrue(dao.getAllViajes().size()==4);
		dao.deleteViaje(2);
		Assert.assertTrue(dao.getAllViajes().size()==4);//Deleting does not remove index, sets value to null
	}
	
	@Test
	public void compraInvalidIdTest() {
		dao.comprarViaje(-1);
		Assert.assertTrue(dao.getAllViajes().size()==3);
		Assert.assertTrue(dao.findViajeById(1)==Dao.ASIENTO_DEFECTO);
		dao.comprarViaje(0);
		Assert.assertTrue(dao.getAllViajes().size()==3);
		Assert.assertTrue(dao.findViajeById(1)==Dao.ASIENTO_DEFECTO);
	}
	
	@Test
	public void compraNonExistentIdTest() {
		dao.comprarViaje(4);
		Assert.assertTrue(dao.getAllViajes().size()==3);
		Assert.assertTrue(dao.findViajeById(3)==Dao.ASIENTO_DEFECTO);
	}
	
	@Test
	public void cancelarCompraInvalidIdTest() {
		dao.cancelarCompra(-1);
		Assert.assertTrue(dao.getAllViajes().size()==3);
		Assert.assertTrue(dao.findViajeById(1)==Dao.ASIENTO_DEFECTO);
		dao.cancelarCompra(0);
		Assert.assertTrue(dao.getAllViajes().size()==3);
		Assert.assertTrue(dao.findViajeById(1)==Dao.ASIENTO_DEFECTO);
	}
	
	@Test
	public void cancelarCompraNonExistingIdTest() {
		dao.cancelarCompra(4);
		Assert.assertTrue(dao.getAllViajes().size()==3);
		Assert.assertTrue(dao.findViajeById(3)==Dao.ASIENTO_DEFECTO);
	}
	
	@Test
	public void compraCancelamentoTest() {
		dao.comprarViaje(2);
		Assert.assertTrue(dao.findViajeById(2)>=1 && dao.findViajeById(2)<=Dao.NUM_ASIENTOS);
		Assert.assertTrue(dao.findViajeById(1)==Dao.ASIENTO_DEFECTO);
		Assert.assertTrue(dao.findViajeById(3)==Dao.ASIENTO_DEFECTO);
		dao.cancelarCompra(2);
		Assert.assertTrue(dao.findViajeById(2)==Dao.ASIENTO_DEFECTO);
		Assert.assertTrue(dao.findViajeById(1)==Dao.ASIENTO_DEFECTO);
		Assert.assertTrue(dao.findViajeById(3)==Dao.ASIENTO_DEFECTO);
	}
	
	@Test
	public void isNotBoughtTest() {
		Assert.assertTrue(dao.isNotBought(-1));
		Assert.assertTrue(dao.isNotBought(0));
		Assert.assertTrue(dao.isNotBought(1));
		Assert.assertTrue(dao.isNotBought(2));
		Assert.assertTrue(dao.isNotBought(3));
		Assert.assertTrue(dao.isNotBought(4));
		dao.comprarViaje(2);
		Assert.assertFalse(dao.isNotBought(2));
		dao.cancelarCompra(2);
		Assert.assertTrue(dao.isNotBought(2));
	}

	@Test
	public void deleteInvalidViajeIdTest() {
		dao.deleteViaje(-1);
		Assert.assertTrue(dao.getAllViajes().size()==3);
		dao.deleteViaje(0);
		Assert.assertTrue(dao.getAllViajes().size()==3);
	}
	
	@Test
	public void deleteNonExistingViajeIdTest() {
		dao.deleteViaje(4);
		Assert.assertTrue(dao.getAllViajes().size()==3);
	}
	
	@Test
	public void deleteViajesTest() {
		dao.deleteViaje(1);
		Assert.assertTrue(dao.findViajeById(1)==Dao.ASIENTO_NO_VIAJE);
		Assert.assertTrue(dao.getAllViajes().size()==3);
	}

	@After
	public void tearDown() {
		Dao.cleanDao();
	}
}
