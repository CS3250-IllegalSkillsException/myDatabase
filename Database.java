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

        public void closeConnection(){
            try{
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
                BufferedReader lineReader = new BufferedReader(new FileReader(csvFilePath));
                String lineText = null;
                lineReader.readLine(); // skip header line
                String sql = "INSERT INTO inventory (product_id, quantity, wholesale_cost, sale_price, supplier_id) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                int count = 0;
                while ((lineText = lineReader.readLine()) != null) {
                    String[] data = lineText.split(",");
                    statement.setString(1, data[0]);
                    statement.setString(2, data[1]);
                    statement.setString(3, data[2]);
                    statement.setString(4, data[3]);
                    statement.setString(5, data[4]);
                    statement.addBatch();
                    if (count >= 20) {
                        statement.executeBatch();
                        count = 0;
                    }
                    count++;
                }
                lineReader.close();
                // execute the remaining queries
                statement.executeBatch();
                connection.commit();
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
        try {
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
    
    public void delete(String id){
        try{
            PreparedStatement statement = connection.prepareStatement("DELETE FROM inventory WHERE product_id = ?");
            statement.setString(1,id);
            statement.addBatch();
            statement.executeBatch();
            //preparedStatement.executeUpdate();
            System.out.println("Deleting Product ID: " + id);
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
    


