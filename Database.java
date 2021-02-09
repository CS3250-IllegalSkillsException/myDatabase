import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Database {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/test";
        String username = " ";
        String password = " ";
        String csvFilePath = "inventory_team5.csv";
        int batchSize = 20;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);
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
                statement.addBatch();
                if (count % batchSize == 0) {
                    statement.executeBatch();
                }
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
    
    private void read() {
    	return;
    }
    
    private void insert() {
    	return;
    }
    
    private void modify() {
    	return;
    }

    private void delete(String id) {
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
    


