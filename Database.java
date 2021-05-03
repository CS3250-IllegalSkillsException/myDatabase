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

    /* This method checks to see if a value from a column in any table exists.
     * It accepts the table name, column name, and value and executes a query
     * using them. If a single row is found, the method returns true. */
    public Boolean exists(String table, String column, String id){
        //Build sql query string
        String sqlQuery = "SELECT * FROM " + table + " WHERE " + column + " = '" + id + "'";
        try{
            //Execute query
            PreparedStatement statement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet verify = statement.executeQuery();
            if (verify.first()){
                return true;
            }
            else{
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /* This method executes an sql query that deletes an entry from a specified table based on a value in a specified column*/
    public void delete(String table, String column, String id){
        try{
            //Generate sql query to delete product id and execute
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + table + " WHERE " + column  + "= '" + id + "'");
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}