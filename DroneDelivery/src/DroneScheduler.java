import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Scanner;

public class DroneScheduler {

	static int promoters = 0;
	static int detractors = 0;
	static int neutrals = 0;
	static int total = 0;
	static float NPS = 0f;
	static String startTime = "06:00:00";
	static String endTime = "22:00:00";
	static HashMap<Order,Double> orderRequest = new HashMap<>();
	static LinkedHashMap<String,String> orderResponse = new LinkedHashMap<>();
	static List<Order> orders = new ArrayList<>();
	static String droneStartTime=startTime;	//will be updated everytime drone returns to the warehouse
	static String droneReturnTime=startTime;
	static boolean sameDay = true;
	static String previousdroneReturnTime = null;

	/**
	 * Main method which reads the input file, calls the scheduling method, and writes to the output file
	 */
	public static void main(String[] args) throws Exception 
	{ 
		File file = new File(args[0]); 
		DroneScheduler d = new DroneScheduler();
		d.readFile(file);

		while(!orders.isEmpty() && (d.getTimeDifference(droneReturnTime, endTime))<0 && sameDay)
			d.scheduling(orders);

		d.writeFile();
		System.out.println("Output File = " +System.getProperty("user.dir")+"\\output.txt");
	}

	/**
	 * Reads the input file
	 * @param file
	 * @throws FileNotFoundException
	 */
	public void readFile(File file) throws FileNotFoundException
	{
		Scanner sc = new Scanner(file); 
		List<String> lines = new ArrayList<String>();
		List<String> orderNo = new ArrayList<>();
		List<String> coordinates = new ArrayList<>();
		List<String> orderTime = new ArrayList<>();

		while (sc.hasNextLine()) 
			lines.add(sc.nextLine());

		int i=0;
		for(String x: lines)
		{
			orderNo.add(i,x.split(" ")[0]);
			coordinates.add(i,x.split(" ")[1]);
			orderTime.add(i,x.split(" ")[2]);
			orders.add(i,new Order(orderNo.get(i),coordinates.get(i),orderTime.get(i)));
			i++;
		}

		sc.close();
	}

	/**
	 * Writes to the input file
	 * @throws IOException
	 */
	public void writeFile() throws IOException
	{
		FileWriter fileWriter = new FileWriter("output.txt");
		PrintWriter printWriter = new PrintWriter(fileWriter);

		Iterator it = orderResponse.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			printWriter.println(pair.getKey() + " " + pair.getValue());
		}

		printWriter.println();
		printWriter.println("NPS "+calculateNPS(promoters,detractors,total));
		printWriter.close();

	}

	/**
	 * schedules the drone delivery
	 * @param List<Order> orders. A List of all order objects
	 */
	public void scheduling(List<Order> orders)
	{
		droneStartTime=(getTimeDifference(droneStartTime, orders.get(0).orderTime)>0)?droneStartTime:orders.get(0).orderTime;

		double nextOrder;
		orderRequest = new HashMap<>();
		for(int i=0;i<orders.size();i++)
		{
			double orderDelay = getOrderDelay(droneStartTime, orders.get(i).orderTime, orders.get(i).serviceTime);
			if(getTimeDifference(droneStartTime,orders.get(i).orderTime)>=0 )
				orderRequest.put(orders.get(i),orderDelay);
		}

		nextOrder=pickNextOrder(orderRequest);
		Order nextOrderDetails = getKeyByValue(orderRequest, nextOrder);
		orderResponse.put(nextOrderDetails.orderNo, droneStartTime);
		previousdroneReturnTime = droneReturnTime;
		droneReturnTime = updateTime(droneStartTime, nextOrderDetails.serviceTime*2);
		droneStartTime = droneReturnTime;

		if(getTimeDifference(updateTime(droneReturnTime, nextOrderDetails.serviceTime),previousdroneReturnTime)<0)
			sameDay=false;

		NPShelper(nextOrderDetails.orderTime, updateTime(droneStartTime, nextOrderDetails.serviceTime));
		orders.remove(nextOrderDetails);
	}

	/**
	 * picks the key based on the value
	 * @param HashMap<Order, Double> map. A HashMap which maintains which orders with best delivery times
	 * @param value. A value whose key is to be retrieved from the HashMap
	 * @return Order object. A key which matches the value
	 */
	public Order getKeyByValue(HashMap<Order, Double> map, Double value) 
	{
		for (Entry<Order, Double> entry : map.entrySet()) 
		{
			if (Objects.equals(value, entry.getValue())) 
				return entry.getKey();
		}
		return null;
	}

	/**
	 * Pick the next order
	 * @param HashMap<Order, Double> map. A HashMap which maintains which orders with best delivery times
	 * @return a double value of the deivery time of the order which is picked up
	 */
	public double pickNextOrder(HashMap<Order, Double> orderRequest)
	{
		double nextOrder;
		List<Double> pos = new ArrayList<>();
		Iterator itr = orderRequest.values().iterator();
		
		while(itr.hasNext()) 
		{
			Double value = (Double) itr.next();
			if(value<=1.5)
				pos.add(value);
		}

		List<Double> neu = new ArrayList<>();
		itr = orderRequest.values().iterator();
		
		while(itr.hasNext()) 
		{
			Double value = (Double) itr.next();
			if(value>1.5 && value<3.5)
				neu.add(value);   
		}

		List<Double> neg = new ArrayList<>();
		itr = orderRequest.values().iterator();
		
		while(itr.hasNext()) 
		{
			Double value = (Double) itr.next();
			if(value>=3.5)
				neg.add(value);
		}
		nextOrder=nextOrderSelectHelper(pos, neu, neg);
		return nextOrder;
	}

	/**
	 * Helper method to pick up the next order 
	 * @param pos. A list containing expected promoter delivery times 
	 * @param neu. A list containing expected neutrals delivery times 
	 * @param neg. A list containing expected negative delivery times 
	 * @return Minimum value from the appropriate list 
	 */
	public double nextOrderSelectHelper(List<Double> pos, List<Double> neu, List<Double> neg)
	{
		if(!pos.isEmpty())
			return Collections.min(pos);
		else if(!neu.isEmpty())
			return Collections.min(neu);
		else if(!neg.isEmpty())
			return Collections.min(neg);

		return 0.0;
	}

	/**
	 * Gives the time delay after which the customer is expected to receive the order
	 * @param end. The
	 * @param start. The order request time
	 * @param serviceTimeMinutes. Time required to reach the customer location from the warehouse
	 * @return
	 */
	public double getOrderDelay(String end, String start, int serviceTimeMinutes)
	{
		double timediff = getTimeDifference(end,start)/(1000*60);
		return (timediff+serviceTimeMinutes)/60;
	}

	/**
	 * Calculates the NPS (Net Promoter Score)
	 * @param promoters. No. of promoters
	 * @param detractors. No. of detractors
	 * @param total. Total orders delivered
	 * @return NPS
	 */
	public float calculateNPS(int promoters, int detractors, int total)
	{
		NPS = (float) ((100.0/total)*(promoters-detractors));
		return NPS;
	}


	/**
	 * Gives the delivery time i.e. time between order placed and order delivered to the customer
	 * @param inputTime. Time when order is placed
	 * @param outputTime. Time when order is delivered
	 * @return difference between the time in hours
	 */
	public double deliveryTime(String inputTime, String outputTime) {

		return getTimeDifferenceInHours(outputTime, inputTime);
	}


	/**
	 * Keeps a counter of promoters, detractors, neutrals and total orders delivered
	 * @param inputTime. Time when order is placed
	 * @param outputTime. Time when order is delivered
	 */
	public void NPShelper(String inputTime, String outputTime)
	{
		double deliveryTime=deliveryTime(inputTime,outputTime);
		if(deliveryTime>=3.5)
			detractors++;
		else if(deliveryTime<=1.5)
			promoters++;
		else
			neutrals++;
		total++;
	}

	/**
	 * Gives time difference in hours (first time - second time)
	 * @param first
	 * @param second
	 * @return Time difference in hours
	 */
	public double getTimeDifferenceInHours(String first, String second)
	{
		return getTimeDifference(first,second)/(60*60*1000.0);
	}

	/**
	 * Gives time difference in milliseconds (first time - second time)
	 * @param first
	 * @param second
	 * @return Time difference in milliseconds
	 */
	public double getTimeDifference(String first, String second)
	{
		double diff=0;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			Date end = dateFormat.parse(first);
			Date start = dateFormat.parse(second);

			diff = (end.getTime()-start.getTime());
		} catch(Exception e) { 
			System.out.println(e);
		}
		return diff;
	}

	/**
	 * Updates time. Add minutes to the initial time.
	 * @param initialTime
	 * @param minutes
	 * @return updated time
	 */
	public String updateTime(String initialTime, int minutes)
	{
		String newTime=null;;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			Date initial = dateFormat.parse(initialTime);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(initial);
			calendar.add(Calendar.MINUTE, minutes);
			newTime = dateFormat.format(calendar.getTime());

		} catch(Exception e) { 
			System.out.println(e);
		}
		return newTime;
	}

}
