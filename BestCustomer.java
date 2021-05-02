import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class BestCustomer extends Database{
	/* This class provides a Best Customer Report for Marketing purposes */
	
	//The following two lines provide two ways of connecting to the MySQL database.
	private final DataGovernance governance = new DataGovernance();
	
	public BestCustomer(Database db) throws SQLException {
		super(db.getUsername(),db.getPassword());
	}
	
	public BestCustomer(String user, String pass) {
		super(user,pass);
	}
	/* This function takes the Order Table and adds together all the purchases made by each individual customer using their email
	 * and a time range provided by the user. The purchase total is put into the customers table and the user can select the  
	   number of top customers they want to display. All this information is then outputed into a CSV file in their downloads folder.*/
	public void customerReport() throws SQLException, FileNotFoundException {
		try {
			//creates Customer Table
			Statement s = connection.createStatement();
			s.executeUpdate("TRUNCATE test.customers");
			String email = "SELECT cust_email from orders";
			PreparedStatement statement = connection.prepareStatement(email);
			ResultSet set = statement.executeQuery(email);
			//access the User's home 
			String home = System.getProperty("user.home");
			//gets the number of days
			int days;
		    System.out.println("This tool allows you to generate a customer report for a given amount of days. \nFor example, entering '7' will generate a report for the best customers in the last 7 days.");
		    System.out.println("Enter number of days to generate report on: ");
		    Scanner dayInput = new Scanner(System.in);
		    days = dayInput.nextInt();
		    System.out.println("Please wait while we compile the report.....");
			while(set.next()) {
				String emails = set.getString("cust_email");
				String newCust = "INSERT IGNORE INTO customers (email, purchased) VALUES (?,?) ";
				PreparedStatement statement3 = connection.prepareStatement(newCust);
				String sql = "SELECT cust_email, SUM(subtotal) AS purchased FROM orders WHERE cust_email = '"+emails+"' AND date>= DATE_ADD(CURDATE(), INTERVAL -" + days + " DAY) ";
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
			//outputs the Customer Report into a CSV
			PrintWriter pw = new PrintWriter(new File(home + "\\Downloads\\CustomerOrderReport.csv"));
			StringBuilder sb = new StringBuilder();
			ResultSet rrs = null;
			sb.append("Email");
			sb.append(",");
			sb.append("Purchased");
			sb.append("\r\n");
			String query = "SELECT email, purchased FROM customers ORDER BY purchased DESC";
			PreparedStatement ps = connection.prepareStatement(query);
			rrs = ps.executeQuery();
			//gets number of customers
			System.out.println("How many best customers would you like to see?");
			System.out.println("Enter number of customers to rank: ");
			Scanner numInput = new Scanner(System.in);
			int numCustomers = numInput.nextInt();
			//unhashes the email in the report
			for(int i = 0; i < numCustomers; i++){
				rrs.next();
				sb.append(governance.unHash(rrs.getString("email"),connection));
				sb.append(",");
				sb.append(rrs.getString("purchased"));
				sb.append("\r\n");
			}
			pw.write(sb.toString());
			pw.close();
			System.out.println(days + "Report export complete. Find CustomerOrderReport.csv in your Downloads folder.");
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
		}
	}
}