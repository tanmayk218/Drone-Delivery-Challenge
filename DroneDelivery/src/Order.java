
public class Order{

	String orderNo;
	String coordinates;
	String orderTime;
	int serviceTime;	// time taken for drone to travel from warehouse to order location
	
	Order(String orderNo, String coordinates, String orderTime)
	{
		this.orderNo=orderNo;
		this.coordinates=coordinates;
		this.orderTime=orderTime;
		this.serviceTime=setServiceTime(coordinates);
	}
	
	Order()
	{
	}
	
	/**
	 * Sets the service time for each order
	 * @param coordinates
	 * @return service time
	 */
	public int setServiceTime(String coordinates)
	{
		int serviceTime = coordinateHelper(coordinates);
		return serviceTime;
	}

	/**
	 * Helper method to calculate the service time based on the coordinates
	 * @param coordinates
	 * @return
	 */
	public int coordinateHelper(String coordinates) {
		
		String co1=Character.toString(coordinates.charAt(1));
		String co2="";
		int res=0;
		int i=2;
		
		while(i<coordinates.length())
		{
			if(coordinates.charAt(i)>=48 && coordinates.charAt(i)<=57)
				{
					co1=co1+Character.toString(coordinates.charAt(i));
					i++;
				}
			else
				break;
		}
		
		i++;
		
		while(i<coordinates.length())
		{
			if(coordinates.charAt(i)>=48 && coordinates.charAt(i)<=57)
				{
					co2=co2+Character.toString(coordinates.charAt(i));
					i++;
				}
			else
				break;
		}
		
		res=Integer.parseInt(co1)+Integer.parseInt(co2);
		return res;
	}

}
