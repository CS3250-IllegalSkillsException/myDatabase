import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Week {
    private String startDay;
    private String endDay;
    private final ArrayList<Day> days = new ArrayList<>();;

    public Week(String start, String end) {
        //Separate the year, month, and day from the start date.
        int year = Integer.parseInt(start.substring(0,4));
        int month = Integer.parseInt(start.substring(5,7)) - 1;
        int day = Integer.parseInt(start.substring(8));
        GregorianCalendar firstDay = new GregorianCalendar(year,month,day);
        //Separate the year, month, and day from the end date.
        year = Integer.parseInt(end.substring(0,4));
        month = Integer.parseInt(end.substring(5,7)) - 1;
        day = Integer.parseInt(end.substring(8));
        GregorianCalendar lastDay = new GregorianCalendar(year,month,day);
        //Add one to the last day (for looping purposes)
        lastDay.add(Calendar.DAY_OF_MONTH, 1);
        while(!firstDay.equals(lastDay)){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setCalendar(firstDay);
            String formattedDay = format.format(firstDay.getTime());
            //Create a day object for each date between the start and end date.
            days.add(new Day(formattedDay));
            firstDay.add(Calendar.DAY_OF_MONTH,1);
        }
    }

    //We should move the following two functions to the orders database at some point instead.

    //Loop through the days and total the number of orders for each of them.
    public int getNumOrdersForWeek(Orders db){
        int total = 0;
        for(int i = 0; i < days.size(); i++){
            total = total + db.getNumOrdersForDay(days.get(i));
        }
        return total;
    }

    //Loop through the days and total the revenue for each of them.
    public double getRevenueForWeek(Orders db){
        double total = 0;
        for(int i = 0; i < days.size(); i++){
            total = total + db.getRevenueForDay(days.get(i));
        }
        return total;
    }
}
