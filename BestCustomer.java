import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BestCustomer extends Database{
	
	public BestCustomer(Database db) throws SQLException {
		super(db.getUsername(),db.getPassword());
	}
	
	public BestCustomer(String user, String pass) {
		super(user,pass);
	}
	
	public void customerReport() throws SQLException {
		try {
			Statement s = connection.createStatement();
			s.executeUpdate("TRUNCATE test.customers");
			String email = "SELECT cust_email from orders";
			PreparedStatement statement = connection.prepareStatement(email);
			ResultSet set = statement.executeQuery(email);
			while(set.next()) {
				String emails = set.getString("cust_email");
				String newCust = "INSERT IGNORE INTO customers (email, purchased) VALUES (?,?) ";
				PreparedStatement statement3 = connection.prepareStatement(newCust);
				String sql = "SELECT cust_email, SUM(subtotal) AS purchased FROM orders WHERE cust_email = '"+emails+"' ORDER BY SUM(subtotal)";
				PreparedStatement s2 = connection.prepareStatement(sql);
				ResultSet rs = s2.executeQuery();
				while(rs.next()) {
					double newVal = rs.getDouble("purchased");
					statement3.setString(1,emails);
					statement3.setDouble(2,newVal);
					statement3.addBatch();
			        statement3.executeBatch();
			        connection.commit();
				}	
			}
			exportCustomerReport();
		} catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
		}
	}
	
	public void exportCustomerReport () {
		 String home = System.getProperty("user.home");
	        try {
	            PrintWriter pw = new PrintWriter(new File(home + "\\Downloads\\CustomerOrderReport.csv"));
	            StringBuilder sb = new StringBuilder();
	            ResultSet rs = null;
	            sb.append("Email");
	            sb.append(",");
	            sb.append("Purchased");
	            sb.append(",");
	            String query = "select * from customers ORDER BY purchased DESC";
	            PreparedStatement ps = connection.prepareStatement(query);
	            rs = ps.executeQuery();
	            while (rs.next()) {
	                sb.append(rs.getString("email"));
	                sb.append(",");
	                sb.append(rs.getString("purchased"));
	                sb.append("\r\n");
	            }
	            pw.write(sb.toString());
	            pw.close();
	            System.out.println("Export complete. Find CustomerOrderReport.csv in your Downloads folder.");
	        } catch (FileNotFoundException e) {
	        	e.printStackTrace();
	        } catch (SQLException e) {
	        	e.printStackTrace();
		}
	}
}
