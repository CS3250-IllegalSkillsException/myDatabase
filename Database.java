import java.io.*;
import java.sql.*;
import java.util.Scanner;
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
        	PreparedStatement preparedStatement;
            Scanner scanner = new Scanner(System.in);
            try {
                String loop;
                do {
                	System.out.println("Enter which ID to read: \n"
                			+ " 1.Product ID \n"
                			+ " 2.Supplier ID");
                    int id = scanner.nextInt();
                    switch (id) {
                        case 1:
                        	System.out.println("Enter Product ID: ");
                        	String prod_id = scanner.next();
                        	String sql1 = "SELECT product_id,  quantity, wholesale_cost, "
                        			+ "sale_price, supplier_id FROM inventory "
                        			+ "WHERE product_id= '" + prod_id + "'";
                        	PreparedStatement statement1 = connection.prepareStatement(sql1);
                        	ResultSet set1 = statement1.executeQuery(sql1);
                        	while (set1.next()) {
                        		String product_id = set1.getString("product_id");
                        		int quantity = set1.getInt("quantity");
                        		int wholesale_cost = set1.getInt("wholesale_cost");
                        		int sale_price = set1.getInt("sale_price");
                        		String supplier_id = set1.getString("supplier_id"); 
                        		System.out.println("product_id,  quantity, wholesale_cost, sale_price, supplier_id");
                        		System.out.format("%s, %s, %s, %s, %s\n", product_id,  quantity, wholesale_cost, sale_price, supplier_id);
                        	}
                        	
                        break;
                        case 2:
                        	System.out.println("Enter Supplier ID: ");
                        	String supp_id = scanner.next();
                        	String sql2 = "SELECT product_id,  quantity, wholesale_cost, "
                        			+ "sale_price, supplier_id FROM inventory "
                        			+ "WHERE supplier_id= '" + supp_id + "'";
                        	PreparedStatement statement2 = connection.prepareStatement(sql2);
                        	ResultSet set2 = statement2.executeQuery(sql2);
                        	while (set2.next()) {
                        		String product_id = set2.getString("product_id");
                        		int quantity = set2.getInt("quantity");
                        		int wholesale_cost = set2.getInt("wholesale_cost");
                        		int sale_price = set2.getInt("sale_price");
                        		String supplier_id = set2.getString("supplier_id"); 
                        		System.out.println("product_id,  quantity, wholesale_cost, sale_price, supplier_id");
                        		System.out.format("%s, %s, %s, %s, %s\n", product_id,  quantity, wholesale_cost, sale_price, supplier_id);
                        	}
                        break;
                    }
                    System.out.println("\nWould you like to read another ID? Y/N");
                    loop = scanner.next();
                }
                while (!loop.equals("N") && !loop.equals("n"));
                connection.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
        PreparedStatement preparedStatement;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Product ID to edit: ");
        String prodIDinput = scanner.nextLine();
        try {
            String loop;
            do {
                System.out.println("Which column would you like to update?" +
                        "\n 1. Quantity \n 2. Wholesale Cost \n 3. Sale Price \n 4. Supplier ID");
                int column = scanner.nextInt();
                switch (column) {

                    case 1:
                        System.out.println("Enter new quantity: ");
                        int newQuantity = scanner.nextInt();
                        String quantityEdit = "UPDATE inventory SET quantity= ? WHERE product_id = ?";
                        preparedStatement = connection.prepareStatement(quantityEdit);
                        preparedStatement.setString(1, String.valueOf(newQuantity));
                        preparedStatement.setString(2, prodIDinput);
                        int i = preparedStatement.executeUpdate();
                        if (i > 0) {
                            System.out.println("Successfully updated quantity of Product ID: " + prodIDinput);
                        } else {
                            System.out.println("Error updating value");
                        }
                        break;

                    case 2:
                        System.out.println("Enter new Wholesale Cost: ");
                        double newWSCost = scanner.nextDouble();
                        String wsCostEdit = "UPDATE inventory SET wholesale_cost = ? WHERE product_id = ?";
                        preparedStatement = connection.prepareStatement(wsCostEdit);
                        preparedStatement.setString(1, String.valueOf(newWSCost));
                        preparedStatement.setString(2, prodIDinput);
                        int j = preparedStatement.executeUpdate();
                        if (j > 0) {
                            System.out.println("Successfully updated wholesale cost of Product ID: " + prodIDinput);
                        } else {
                            System.out.println("Error updating value");
                        }
                        break;

                    case 3:
                        System.out.println("Enter new Sale Price: ");
                        double newSalePrice = scanner.nextDouble();
                        String salePriceEdit = "UPDATE inventory SET sale_price = ? WHERE product_id = ?";
                        preparedStatement = connection.prepareStatement(salePriceEdit);
                        preparedStatement.setString(1, String.valueOf(newSalePrice));
                        preparedStatement.setString(2, prodIDinput);
                        int k = preparedStatement.executeUpdate();
                        if (k > 0) {
                            System.out.println("Successfully updated sale price of Product ID: " + prodIDinput);
                        } else {
                            System.out.println("Error updating value");
                        }
                        break;

                    case 4:
                        System.out.println("Enter new Supplier ID: ");
                        String newSupplierID = scanner.next();
                        String supplierIDEdit = "UPDATE inventory SET supplier_id = ? WHERE product_id = ?";
                        preparedStatement = connection.prepareStatement(supplierIDEdit);
                        preparedStatement.setString(1, String.valueOf(newSupplierID));
                        preparedStatement.setString(2, prodIDinput);
                        int l = preparedStatement.executeUpdate();
                        if (l > 0) {
                            System.out.println("Successfully updated Supplier ID of Product ID: " + prodIDinput);
                        } else {
                            System.out.println("Error updating value");
                        }
                        break;
                }
                System.out.println("Would you like to make another change to this product? Y/N");
                loop = scanner.next();
            }
            while (!loop.equals("N") && !loop.equals("n"));
            connection.commit();
            System.out.println("Updates complete.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
