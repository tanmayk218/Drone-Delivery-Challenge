# Drone-Delivery-Challenge
A program to schedule drone delivery

Assumptions:

1. Drone goes to a customer's location to drop an item, then comes back to the wareouse to pick up another item and goes to deliver that item.
2. Drone's speed is 1 block per minute. N2E6  
3. Drone does not take any time to drop and pick up items.
4. Drone can only go up, down, right, left, cannot move diagonally.
5. time to delivery upto 1.5 then promote,
 from 1.51 to 3.5 then neutral,
from 3.51 and up then detractor 
6. Entire scenario is valid only for one day, as there is no mention of days.
7. 24 hour clock - 6:00 to 22:00
8. If drone's return time is after 10 pm, even if order's expected delivery is before 10 pm, drone will not deliver that order.
9. The input file is error free
10. What about the orders which are not delivered on the same day? Are they counted in detractors? I haven't counted them as of yet.



# Instructions to run the program:

1. Compile all DroneScheduler.java and Order.java files.
2. Run the DroneScheduler.java and give the input file path as argument.
  e.g. java DroneScheduler C:\fakepath\input.txt
3. Output file will be created in the current directory.
  e.g. C:\fakepath\output.txt
