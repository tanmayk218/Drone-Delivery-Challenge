	import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

class DroneOrderTests {

	Order order = new Order();
	DroneScheduler d = new DroneScheduler();
	
	@Test
	void testOrderCoordinateHelper1() {
		
		assertEquals(6, order.coordinateHelper("N1W5"));
	}
	
	@Test
	void testOrderCoordinateHelper2() {
		
		assertEquals(85, order.coordinateHelper("N65E20"));
	}

	@Test
	void testOrderCoordinateHelper3() {
	
		assertEquals(35, order.coordinateHelper("N12W23"));
	}

	@Test
	void testOrderCoordinateHelper4() {
	
		assertEquals(26, order.coordinateHelper("N1W25"));
	}

	
	@Test
	void testGetKeyByValue1()
	{
		HashMap<Order, Double> hm= new HashMap<>(); 
		hm.put(new Order("WM001","N11W5","05:11:50"), 1.0);
		hm.put(new Order("WM002", "S3E2", "05:11:55"), 1.5);
		hm.put(new Order("WM003", "N7E50", "05:31:50"), 3.5);
		hm.put(new Order("WM005", "N11E5", "06:23:50"), 3.4999999);
		hm.put(new Order("WM005", "N11E5", "06:35:50"), 5.5);
		hm.put(new Order("WM005", "N11E5", "06:42:50"), 7.0);
		
		Order order = d.getKeyByValue(hm, 1.5);
		
		assertEquals("WM002", order.orderNo);
		assertEquals("S3E2", order.coordinates);
		assertEquals("05:11:55", order.orderTime);
	}
	
	@Test
	void testPickNextOrder1()
	{
		HashMap<Order, Double> hm= new HashMap<>(); 
		hm.put(new Order("WM001","N11W5","05:11:50"), 1.0);
		hm.put(new Order("WM002", "S3E2", "05:11:55"), 1.5);
		hm.put(new Order("WM003", "N7E50", "05:31:50"), 3.5);
		hm.put(new Order("WM004", "N11E5", "06:32:50"), 3.4999999);
		hm.put(new Order("WM005", "N11E5", "06:45:50"), 5.5);
		hm.put(new Order("WM006", "N11E5", "06:54:50"), 7.0);
		
		double d1 = d.pickNextOrder(hm);
		
		assertEquals(1.0, d1);
	}
	
	@Test
	void testPickNextOrder2()
	{
		HashMap<Order, Double> hm= new HashMap<>(); 
		hm.put(new Order("WM003", "N7E50", "05:31:50"), 3.23);
		hm.put(new Order("WM004", "N11E5", "06:11:50"), 3.4999);
		hm.put(new Order("WM005", "N11E5", "06:32:50"), 5.5);
		hm.put(new Order("WM006", "N11E5", "06:54:50"), 7.0);
		
		double d1 = d.pickNextOrder(hm);
		
		assertEquals(3.23, d1);
	}
	
	@Test
	void testPickNextOrder3()
	{
		HashMap<Order, Double> hm= new HashMap<>(); 
		hm.put(new Order("WM004", "N11E5", "06:11:50"), 3.5);
		hm.put(new Order("WM005", "N11E5", "06:34:50"), 5.5);
		hm.put(new Order("WM006", "N11E5", "06:43:50"), 7.0);
		
		double d1 = d.pickNextOrder(hm);
		
		assertEquals(3.5, d1);
	}
	
	
	@Test
	void testCalculateNPS()
	{
		int promoters = 20;
		int detractors = 10;
		int total = 40;
		
		assertEquals(25.0, d.calculateNPS(promoters, detractors, total));
	}
}
