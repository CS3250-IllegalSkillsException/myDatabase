import java.sql.*;

public class Database extends DatabaseConnection{
    
    public Database(String user, String pass){
        super(user,pass);
    }


    public int getNumEntries() {
        try{
            String sql3 = "SELECT order_id FROM test.orders ORDER BY CAST(order_id AS UNSIGNED) DESC LIMIT 1";
            PreparedStatement statement3 = connection.prepareStatement(sql3);
            ResultSet numRows = statement3.executeQuery();
            numRows.next();
            return numRows.getInt("order_id");
        } catch (SQLException e){
            //e.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
        return 0;
    }


}