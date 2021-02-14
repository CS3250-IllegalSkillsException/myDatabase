
import java.io.*;
import java.sql.*;

public class Main {

public static void main(String[] args) {
String jdbcURL = "jdbc:mysql://localhost:3306/test";
String username = "root";
String password = "SMapi3407";
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
		//Timestamp sqlTimestamp = Timestamp.valueOf(timestamp);
		//statement.setTimestamp(3, sqlTimestamp);
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
}