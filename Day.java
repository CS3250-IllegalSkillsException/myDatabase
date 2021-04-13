import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Day {
    String day;

    public Day(String date){
        day = date;
    }

    public String getDay(){
        return day;
    }

}
