import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Database {
    private String jdbcURL = "jdbc:mysql://localhost:3306/test";
    private String username;
    private String password;
    private String csvFilePath = "inventory_team5.csv";
    private Connection connection;

        public Database(String user, String pass){
            username = user;
            password = pass;
            initializeConnection();
        }

        //non-default constructor for cases where we are accessing a different database.
        public Database(String user, String pass, String url){
            username = user;
            password = pass;
            jdbcURL = url;
            initializeConnection();
        }

        public void setUsername(String user){
            username = user;
        }

        public String getUsername(){
            return username;
        }

        public void setPassword(String pass){
            password = pass;
        }

        public String getPassword(){
            return password;
        }

        public void setJdbcUrl(String url){
            jdbcURL = url;
        }

        public String getJdbcUrl(){
            return jdbcURL;
        }

        public void setCsvFilePath(String filepath){
            csvFilePath = filepath;
        }

        public String getCsvFilePath(){
            return csvFilePath;
        }

        //attempts to connect to the SQL database. Returns true if successful, false if unsuccessful.
        public boolean initializeConnection(){
            try {
                connection = DriverManager.getConnection(jdbcURL, username, password);
                connection.setAutoCommit(false);
            } catch (SQLException ex){
                ex.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            }
            return true;
        }

        public void importFromCsvFile() {
            try {
            	String sql = "INSERT INTO inventory (product_id, quantity, wholesale_cost, sale_price, supplier_id) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                BufferedReader lineReader = new BufferedReader(new FileReader(csvFilePath));
                String lineText = null;
                int count = 0;
                lineReader.readLine(); // skip header line
                while ((lineText = lineReader.readLine()) != null) {
                    String[] data = lineText.split(",");
                    String product_id = data[0];
                    String quantity = data[1];
                    String wholesale_cost = data[2];
                    String sale_price = data[3];
                    String supplier_id = data[4];
                    statement.setString(1, product_id);
                    statement.setString(2, quantity);
                    statement.setString(3, wholesale_cost);
                    statement.setString(4, sale_price);
                    statement.setString(5, supplier_id);
                    statement.executeBatch();
                }
                lineReader.close();
                // execute the remaining queries
                statement.executeBatch();
                connection.commit();
                connection.close();
            } catch (IOException ex) {
                System.err.println(ex);
            } catch (SQLException ex) {
                ex.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    public void read() {
    	return;
    }
    
    public void insert(String product_id, String quantity, String wholesale_cost, String sale_price, String supplier_id) {
        String jdbcURL = "jdbc:mysql://localhost:3306/test";
        String username = " ";
        String password = " ";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);
            String sql = "INSERT INTO inventory (product_id, quantity, wholesale_cost, sale_price, supplier_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, product_id);
            statement.setString(2, quantity);
            statement.setString(3, wholesale_cost);
            statement.setString(4, sale_price);
            statement.setString(5, supplier_id);
            // execute the remaining queries
            statement.addBatch();
            statement.executeBatch();
            connection.commit();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void modify() {
    	return;
    }
    

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String jdbcURL = "jdbc:mysql://localhost:3306/test";
        String username = "";
        String password = "";

        try{
            connection = DriverManager.getConnection(jdbcURL, username, password);
            preparedStatement = connection.prepareStatement("DELETE FROM inventory WHERE product_id = ?");
            preparedStatement.setString(1,id);
            preparedStatement.executeUpdate();

            System.out.println("Deleting Product ID: " + id);

        }
        catch (Exception e){
            e.printStackTrace();
        } finally {
            if (preparedStatement != null){
                try{
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
    


