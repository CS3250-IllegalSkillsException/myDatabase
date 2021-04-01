import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;

public class Database extends dataGovernance{
    
    public Database(String user, String pass) {
        super(user, pass);
        // TODO Auto-generated constructor stub
    }


    private int getNumEntries() {
        try{
            String sql3 = "SELECT order_id FROM test.orders ORDER BY CAST(order_id AS UNSIGNED) DESC LIMIT 1";
            PreparedStatement statement3 = connection.prepareStatement(sql3);
            ResultSet numRows = statement3.executeQuery();
            numRows.next();
            return numRows.getInt("order_id");
        } catch (SQLException e){
            e.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
        return -100;
    }

    public void importCustomerData() {
        try {
            BufferedReader lineReader = new BufferedReader(new FileReader(customerOrderCsv));
            String lineText = null;
            lineReader.readLine(); // skip header line
            String sql = "INSERT INTO orders (order_id, date, cust_email, cust_location, product_id, product_quantity) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            String sql2 = "UPDATE inventory SET quantity = quantity - ? WHERE product_id = ?";
            PreparedStatement statement2 = connection.prepareStatement(sql2);
            int orderId = getNumEntries() + 1;
            int count = 0;
            while ((lineText = lineReader.readLine()) != null) {
                statement.setInt(1, orderId);
                String[] data = lineText.split(",");
                statement.setString(2, data[0]);
                statement.setString(3, data[1]);
                statement.setString(4, data[2]);
                statement.setString(5, data[3]);
                statement.setString(6, data[4]);
                statement2.setInt(1, Integer.parseInt(data[4]));
                statement2.setString(2, data[3]);
                statement.addBatch();
                statement2.addBatch();
                if (count >= 20) {
                    statement.executeBatch();
                    statement2.executeBatch();
                    count = 0;
                }
                count++;
                orderId++;
            }
            lineReader.close();
            // execute the remaining queries
            statement.executeBatch();
            statement2.executeBatch();
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
                        		double wholesale_cost = set1.getDouble("wholesale_cost");
                        		double sale_price = set1.getDouble("sale_price");
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
    public void insertOrders(String date, String cust_email, String cust_location, String product_id, String product_quantity) {
        try {
            PreparedStatement productCheck = connection.prepareStatement("SELECT * FROM inventory WHERE product_id = ?");
            productCheck.setString(1,product_id);
            ResultSet product = productCheck.executeQuery();
            if(product.next()){
                int quantity = Integer.parseInt(product.getString("quantity"));
                String updateQ = String.valueOf(quantity - Integer.parseInt(product_quantity));
                if(Integer.parseInt(product_quantity) <= quantity){
                    String sql = "INSERT INTO orders (order_id, date, cust_email, cust_location, product_id, product_quantity) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    String sql2 = "UPDATE inventory SET quantity = ? WHERE product_id = ?";
                    PreparedStatement statement2 = connection.prepareStatement(sql2);
                    int orderID = getNumEntries() + 1;
                    statement.setInt(1, orderID);
                    statement.setString(2, date);
                    statement.setString(3, cust_email);
                    statement.setString(4, cust_location);
                    statement.setString(5, product_id);
                    statement.setString(6, product_quantity);
                    statement2.setString(1, updateQ);
                    statement2.setString(2, product_id);
                    // execute the remaining queries
                    statement.addBatch();
                    statement2.addBatch();
                    statement.executeBatch();
                    statement2.executeBatch();
                    connection.commit();
                } else {
                    System.out.println("Not enough in inventory. Please lower your order volume or try again later.");
                }
            } else{
                System.out.println("The product ID you entered has not been found.");
            }
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
    
  
    public void search() {


        Scanner searchFilter = new Scanner(System.in);
        System.out.println("Would you like to search the Inventory or the Customer Orders? \n 1. Inventory \n 2. Customer Orders");
        int tableChoice = searchFilter.nextInt();
        switch (tableChoice) {
            case 1:
                // inventory submenu
                int menuOption;
                String option = "Y";
                System.out.println("This menu will let you add and apply a filter before displaying matching database entries. \nSet a filter to generate results. ");
                do {
                    String searchSuppID = null;
                    String searchProdID = null;
                    String searchQuantity = null;
                    String searchWSCost = null;
                    String searchSalePrice = null;
                    do {
                        System.out.println("\nWhich filter would you like to set? \n" +
                                "1. Product ID \n" +
                                "2. Supplier ID \n" +
                                "3. Quantity \n" +
                                "4. Wholesale Cost \n" +
                                "5. Sale Price");

                        menuOption = searchFilter.nextInt();

                        switch (menuOption) {
                            case 1:
                                System.out.println("Enter product ID or type X to cancel: ");
                                searchProdID = searchFilter.next();
                                if (searchProdID.matches("X")) {
                                    searchProdID = null;
                                    System.out.println("Filter canceled.");
                                } else {
                                    System.out.println("Product ID filter set.");
                                    option = "N";
                                }
                                break;


                            case 2:
                                System.out.println("Enter Supplier ID or type X to cancel: ");
                                searchSuppID = searchFilter.next();
                                if (searchSuppID.matches("X")) {
                                    searchSuppID = null;
                                    System.out.println("Filter canceled.");
                                } else {
                                    System.out.println("Supplier ID filter set.");
                                    option = "N";
                                }
                                break;


                            case 3:
                                System.out.println("Enter quantity or type X to cancel: ");
                                searchQuantity = searchFilter.next();
                                if (searchQuantity.matches("X")) {
                                    searchQuantity = null;
                                    System.out.println("Filter canceled.");
                                } else {
                                    System.out.println("Quantity filter set.");
                                    option = "N";
                                }
                                break;


                            case 4:
                                System.out.println("Enter wholesale cost ($$.$$) or type X to cancel: ");
                                searchWSCost = searchFilter.next();
                                if (searchWSCost.matches("X")) {
                                    searchWSCost = null;
                                    System.out.println("Filter canceled.");
                                } else {
                                    System.out.println("Wholesale cost filter set.");
                                    option = "N";
                                }
                                break;


                            case 5:
                                System.out.println("Enter sale price ($$.$$) or type X to cancel: ");
                                searchSalePrice = searchFilter.next();
                                if (searchSalePrice.matches("X")) {
                                    searchSalePrice = null;
                                    System.out.println("Filter canceled.");
                                } else {
                                    System.out.println("Sale price filter set.");
                                    option = "N";
                                }
                                break;

                            default:
                                System.out.println("Invalid option");
                                break;
                        }
                    } while (option == "Y");


                    // Building the sql query string with added filter
                    System.out.println("Generating results... ");

                    try {
                        String sqlQuery = "SELECT product_id, quantity, wholesale_cost, sale_price, supplier_id FROM inventory WHERE";

                        if (searchProdID != null) {
                            sqlQuery += " product_id = '" + searchProdID + "'";
                        } else if (searchQuantity != null) {
                            sqlQuery += " quantity = '" + searchQuantity + "'";
                        } else if (searchWSCost != null) {
                            sqlQuery += " wholesale_cost = '" + searchWSCost + "'";
                        } else if (searchSalePrice != null) {
                            sqlQuery += " sale_price = '" + searchSalePrice + "'";
                        } else if (searchSuppID != null) {
                            sqlQuery += " supplier_id = '" + searchSuppID + "'";
                        }

                        PreparedStatement statement = connection.prepareStatement(sqlQuery);


                        // for checking SQL query syntax
                        // System.out.println(sqlQuery + "\n");

                        // Display results from database
                        ResultSet results = statement.executeQuery(sqlQuery);

                        System.out.println("-----------------------------------------------------------------------------");
                        System.out.printf("%-18s%-13s%-18s%-15s%-15s\n", "Product ID", "Quantity", "Wholesale Cost", "Sale Price", "Supplier ID");
                        System.out.println("-----------------------------------------------------------------------------");

                        while (results.next()) {
                            String product_id = results.getString("product_id");
                            int quantity = results.getInt("quantity");
                            double wholesale_cost = results.getDouble("wholesale_cost");
                            double sale_price = results.getDouble("sale_price");
                            String supplier_id = results.getString("supplier_id");
                            System.out.printf("%-18s%-13s%-18s%-15s%-15s\n", product_id, quantity, wholesale_cost, sale_price, supplier_id);

                        }

                    } catch (SQLException e) {
                        System.out.println("Error generating results. ");
                        e.printStackTrace();
                    }


                    System.out.println("--------------------------- End of Results ----------------------------------");
                    System.out.println("\n");
                    System.out.println("Would you like to search again with a new filter? Y/N");
                    option = searchFilter.next();
                } while (option.equals("Y"));
                System.out.println("Exiting");
                break;


            case 2:
                // customer orders submenu
                int coMenuOption;
                String option2 = "Y";
                System.out.println("This menu will let you add and apply a filter before displaying matching database entries. \nSet a filter to generate results. ");
                do {
                    String searchCustDate = null;
                    String searchCustEmail = null;
                    String searchCustLoc = null;
                    String searchCustPID = null;
                    String searchCustQuant = null;
                    String searchCustOID = null;
                    do {
                        System.out.println("\nWhich filter would you like to set? \n" +
                                "1. Date \n" +
                                "2. Email address \n" +
                                "3. Zip Code \n" +
                                "4. Product ID \n" +
                                "5. Quantity \n" +
                                "6. Order ID");

                        coMenuOption = searchFilter.nextInt();


                        switch (coMenuOption) {
                            case 1:
                                System.out.println("Enter date in form 'YYYY-MM-DD' or type X to cancel: ");
                                searchCustDate = searchFilter.next();
                                if (searchCustDate.matches("X")) {
                                    searchCustDate = null;
                                    System.out.println("Filter canceled.");
                                } else {
                                    System.out.println("Date filter set.");
                                    System.out.println("Generating results... ");
                                    option2 = "N";
                                }
                                break;


                            case 2:
                                System.out.println("Enter full customer email address or type X to cancel: ");
                                searchCustEmail = searchFilter.next();
                                if (searchCustEmail.matches("X")) {
                                    searchCustEmail = null;
                                    System.out.println("Filter canceled.");
                                } else {
                                    System.out.println("Customer email address filter set.");
                                    System.out.println("Generating results... ");
                                    option2 = "N";
                                }
                                break;


                            case 3:
                                System.out.println("Enter customer zip code or type X to cancel: ");
                                searchCustLoc = searchFilter.next();
                                if (searchCustLoc.matches("X")) {
                                    searchCustLoc = null;
                                    System.out.println("Filter canceled.");
                                } else {
                                    System.out.println("Customer zip code filter set.");
                                    System.out.println("Generating results... ");
                                    option2 = "N";
                                }
                                break;


                            case 4:
                                System.out.println("Enter Product ID or type X to cancel: ");
                                searchCustPID = searchFilter.next();
                                if (searchCustPID.matches("X")) {
                                    searchCustPID = null;
                                    System.out.println("Filter canceled.");
                                } else {
                                    System.out.println("Product ID filter set.");
                                    System.out.println("Generating results... ");
                                    option2 = "N";
                                }
                                break;


                            case 5:
                                System.out.println("Enter quantity purchased or type X to cancel: ");
                                searchCustQuant = searchFilter.next();
                                if (searchCustQuant.matches("X")) {
                                    searchCustQuant = null;
                                    System.out.println("Filter canceled.");
                                } else {
                                    System.out.println("Quantity filter set.");
                                    System.out.println("Generating results... ");
                                    option2 = "N";
                                }
                                break;

                            case 6:
                                System.out.println("Enter Order ID or type X to cancel: ");
                                searchCustOID = searchFilter.next();
                                if (searchCustOID.matches("X")) {
                                    searchCustOID = null;
                                    System.out.println("Filter canceled.");
                                } else {
                                    System.out.println("Order ID filter set.");
                                    System.out.println("Generating results... ");
                                    option2 = "N";
                                }
                                break;

                            default:
                                System.out.println("Invalid option");
                                break;
                        }
                    } while (option2 == "Y");


                    // Building the sql query string with added filter

                    try {
                        String sqlQuery2 = "SELECT date, cust_email, cust_location, product_id, product_quantity, order_id FROM orders WHERE";

                        if (searchCustDate != null) {
                            sqlQuery2 += " date = '" + searchCustDate + "'";
                        } else if (searchCustEmail != null) {
                            sqlQuery2 += " cust_email = '" + searchCustEmail + "'";
                        } else if (searchCustLoc != null) {
                            sqlQuery2 += " cust_location = '" + searchCustLoc + "'";
                        } else if (searchCustPID != null) {
                            sqlQuery2 += " product_id = '" + searchCustPID + "'";
                        } else if (searchCustQuant != null) {
                            sqlQuery2 += " product_quantity = '" + searchCustQuant + "'";
                        } else if (searchCustOID != null) {
                            sqlQuery2 += " order_id = '" + searchCustOID + "'";
                        }

                        PreparedStatement statement2 = connection.prepareStatement(sqlQuery2);


                        // for checking SQL query syntax
                        // System.out.println(sqlQuery + "\n");

                        // Display results from database
                        ResultSet results2 = statement2.executeQuery(sqlQuery2);

                        System.out.println("----------------------------------------------------------------------------------------------------");
                        System.out.printf("%-15s%-22s%-18s%-18s%-12s%-15s\n", "Date", "Customer Email", "Customer Zip", "Product ID", "Quantity", "Order ID");
                        System.out.println("----------------------------------------------------------------------------------------------------");

                        while (results2.next()) {
                            String date = results2.getString("date");
                            String cust_email = results2.getString("cust_email");
                            int cust_location = results2.getInt("cust_location");
                            String product_id = results2.getString("product_id");
                            int product_quantity = results2.getInt("product_quantity");
                            String order_id = results2.getString("order_id");
                            System.out.printf("%-15s%-22s%-18s%-18s%-12s%-15s\n", date, cust_email, cust_location, product_id, product_quantity, order_id);

                        }

                    } catch (SQLException e) {
                        System.out.println("Error generating results. ");
                        e.printStackTrace();
                    }


                    System.out.println("------------------------------------ End of Results -----------------------------------------------");
                    System.out.println("\n");
                    System.out.println("Would you like to search again with a new filter? Y/N");
                    option = searchFilter.next();
                } while (option.equals("Y"));
                System.out.println("Exiting");
                break;
        }

    }
  
    public void delete(String id){
        try{
            PreparedStatement statement = connection.prepareStatement("DELETE FROM inventory WHERE product_id = ?");
            statement.setString(1,id);
            statement.addBatch();
            statement.executeBatch();
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

    public void deleteOrders(String id){
        try{
            //Run query to see if order exists
            PreparedStatement sqlCheck = connection.prepareStatement("SELECT * FROM orders WHERE order_id = ?");
            sqlCheck.setString(1,id);
            ResultSet exists = sqlCheck.executeQuery();
            if(exists.next()){
                PreparedStatement statement = connection.prepareStatement("DELETE FROM orders WHERE order_id = ?");
                statement.setString(1,id);
                statement.addBatch();
                statement.executeBatch();
                System.out.println("Deleting Order ID: " + id);
                connection.commit();
            }
            else{
                System.out.println("The order ID you entered doesn't exist.");
            }
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