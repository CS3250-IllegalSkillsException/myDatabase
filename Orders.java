import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Random;
import java.util.Scanner;

import com.mysql.cj.protocol.Resultset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Orders extends Database{

    private final DataGovernance governance = new DataGovernance();

    public Orders(Database db){
        super(db.getUsername(),db.getPassword());
    }
    public Orders(String user, String pass){
        super(user,pass);
    }

    //Simulates customer orders as they come in through the csv file, inputting them into the database.
    public void importCustomerData() {
        try {
            BufferedReader lineReader = new BufferedReader(new FileReader(customerOrderCsv));
            String lineText = null;
            lineReader.readLine(); // skip header line
            String sql = "INSERT INTO orders (order_id, date, cust_email, cust_location, product_id, product_quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            String sql2 = "UPDATE inventory SET quantity = quantity - ? WHERE product_id = ?";
            PreparedStatement statement2 = connection.prepareStatement(sql2);
            int orderId = getNumEntries() + 1;
            int count = 0;            
            while ((lineText = lineReader.readLine()) != null) {
                //set the orderID.
                statement.setInt(1, orderId);
                //take the data from the csv line by line, adding it to the database and adjusting where necessary.
                String[] data = lineText.split(",");
                statement.setString(2, data[0]);
                statement.setString(3, ""+governance.getHash(data[1],connection));
                statement.setString(4, data[2]);
                statement.setString(5, data[3]);
                statement.setString(6, data[4]);
                statement2.setInt(1, Integer.parseInt(data[4]));
                statement2.setString(2, data[3]);
                int quant = Integer.parseInt(data[4]);
                double subtotal = getSubtotal(data[3], quant);
                statement.setDouble(7,subtotal);
                statement.addBatch();
                statement2.addBatch();
                //every 20 orders we process, execute our batch.
                if (count >= 20) {
                    statement.executeBatch();
                    statement2.executeBatch();
                    count = 0;
                }
                count++;
                orderId++;
            }
            
            lineReader.close();
            //execute any of the remaining queries in the batch
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
    
    public double getSubtotal(String product_id, int quantity) throws SQLException {
    	String sql = "SELECT sale_price FROM inventory WHERE product_id = '" + product_id + "'";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet set = statement.executeQuery();
        set.next();
        double sale_price = set.getDouble("sale_price");
        double subtotal = sale_price*quantity;
        return subtotal;
    }

    public void exportOrdersCSV() {
        String home = System.getProperty("user.home");
        try {
            PrintWriter pw = new PrintWriter(new File(home + "\\Downloads\\Orders.csv"));
            StringBuilder sb = new StringBuilder();
            ResultSet rs = null;
            sb.append("order_id");
            sb.append(",");
            sb.append("date");
            sb.append(",");
            sb.append("cust_email");
            sb.append(",");
            sb.append("cust_location");
            sb.append(",");
            sb.append("product_id");
            sb.append(",");
            sb.append("product_quantity");
            sb.append("\n");

            String query = "select * from orders";
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                sb.append(rs.getString("order_id"));
                sb.append(",");
                sb.append(rs.getString("date"));
                sb.append(",");
                String cust_email = rs.getString("cust_email");
                sb.append(governance.unHash(cust_email,connection));
                sb.append(",");
                sb.append(rs.getString("cust_location"));
                sb.append(",");
                sb.append(rs.getString("product_id"));
                sb.append(",");
                sb.append(rs.getString("product_quantity"));
                sb.append("\r\n");
            }

            pw.write(sb.toString());
            pw.close();
            System.out.println("Export complete. Find Orders.csv in your Downloads folder.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    Uses string builder and SQL statement to build a temporary table with just product IDs and quantities sold, using
    data from the Orders table within the main db. It also sorts the table so the products that have sold the most will
    appear at the top. Detailed instructions included for easier understanding by users.
    Downloaded file goes straight to machine's local Downloads folder.
     */
    public void orderReport() {
        // needed to access machine's local files
        String home = System.getProperty("user.home");
        int days;
        int rows;
        Scanner input = new Scanner(System.in);
        System.out.println("This tool allows you to generate a report for the best-selling products within a given amount of days. \nFor example, entering '7' and '10' will generate a report for the 10 best-selling products in the last 7 days.");
        System.out.println("Enter number of days to generate report on: ");
        days = input.nextInt();
        System.out.println("Enter number of product IDs in report: ");
        rows = input.nextInt();

        try {
            PrintWriter pw = new PrintWriter(new File(home + "\\Downloads\\BestProductOrderReport.csv"));
            StringBuilder sb = new StringBuilder();
            ResultSet rs = null;
            sb.append("product_id");
            sb.append(",");
            sb.append("quantity_sold");
            sb.append("\r\n");

            // building the temp table with just product IDs and quantities sold. This tool should work normally even if table columns are changed (as long as product ID and quantities sold are still there)
            String query = "SELECT product_id, SUM(product_quantity) AS quantity_sold FROM test.orders WHERE date>= DATE_ADD(CURDATE(), INTERVAL -" + days + " DAY) GROUP BY product_id ORDER BY SUM(product_quantity) DESC LIMIT " + rows + ";";
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                sb.append(rs.getString("product_id"));
                sb.append(",");
                sb.append(rs.getString("quantity_sold"));
                sb.append("\r\n");
            }
            pw.write(sb.toString());
            pw.close();
            System.out.println(days + " day report export complete. Find BestProductsOrderReport.csv in your Downloads folder.");

        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertOrders(String date, String cust_email, String cust_location, String product_id, String product_quantity) {
        try {
            //check if the product is a real product
            PreparedStatement productCheck = connection.prepareStatement("SELECT * FROM inventory WHERE product_id = ?");
            connection.setAutoCommit(false);
            productCheck.setString(1,product_id);
            ResultSet product = productCheck.executeQuery();
            if(product.next()){
                //check to see if we have it in stock
                int quantity = Integer.parseInt(product.getString("quantity"));
                String updateQ = String.valueOf(quantity - Integer.parseInt(product_quantity));
                if(Integer.parseInt(product_quantity) <= quantity){
                    String sql = "INSERT INTO orders (order_id, date, cust_email, cust_location, product_id, product_quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    String sql2 = "UPDATE inventory SET quantity = ? WHERE product_id = ?";
                    PreparedStatement statement2 = connection.prepareStatement(sql2);
                    //find the appropriate order id
                    int orderID = getNumEntries() + 1;
                    statement.setInt(1, orderID);
                    //set the rest of the data according to the arguments, making adjustments where necessary.
                    statement.setString(2, date);
                    statement.setString(3, ""+governance.getHash(cust_email,connection));
                    statement.setString(4, cust_location);
                    statement.setString(5, product_id);
                    statement.setString(6, product_quantity);
                    int quant = Integer.parseInt(product_quantity);
                    double subtotal = getSubtotal(product_id, quant);
                    statement.setDouble(7,subtotal);

                    //update the inventory's stock
                    statement2.setString(1, updateQ);
                    statement2.setString(2, product_id);

                    //execute the remaining queries
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
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Search function allows you to search the Orders table for specific entries using filters
    set by the user. User can set one or multiple filters to be applied before displaying matching entries,
    using a string builder and SQL statements to generate results.
     */
    public void searchOrders() {

        Scanner searchFilter = new Scanner(System.in);
        String option = "Y";
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
                        "[D] Date \n" +
                        "[E] Email address \n" +
                        "[Z] Zip Code \n" +
                        "[P] Product ID \n" +
                        "[Q] Quantity \n" +
                        "[O] Order ID");

                char choice = searchFilter.next().charAt(0);
                searchFilter.nextLine();
                switch (choice) {
                    case 'D':
                        System.out.println("Enter date in form 'YYYY-MM-DD' or type X to cancel: ");
                        searchCustDate = searchFilter.next();
                        if (searchCustDate.matches("X")) {
                            searchCustDate = null;
                            System.out.println("Filter canceled.");
                        } else {
                            System.out.println("Date filter set.");
                            System.out.println("Generating results... ");
                            option = "N";
                        }
                        break;


                    case 'E':
                        System.out.println("Enter full customer email address or type X to cancel: ");
                        searchCustEmail = searchFilter.next();
                        if (searchCustEmail.matches("X")) {
                            searchCustEmail = null;
                            System.out.println("Filter canceled.");
                        } else {
                            System.out.println("Customer email address filter set.");
                            System.out.println("Generating results... ");
                            option = "N";
                        }
                        break;


                    case 'Z':
                        System.out.println("Enter customer zip code or type X to cancel: ");
                        searchCustLoc = searchFilter.next();
                        if (searchCustLoc.matches("X")) {
                            searchCustLoc = null;
                            System.out.println("Filter canceled.");
                        } else {
                            System.out.println("Customer zip code filter set.");
                            System.out.println("Generating results... ");
                            option = "N";
                        }
                        break;


                    case 'P':
                        System.out.println("Enter Product ID or type X to cancel: ");
                        searchCustPID = searchFilter.next();
                        if (searchCustPID.matches("X")) {
                            searchCustPID = null;
                            System.out.println("Filter canceled.");
                        } else {
                            System.out.println("Product ID filter set.");
                            System.out.println("Generating results... ");
                            option = "N";
                        }
                        break;


                    case 'Q':
                        System.out.println("Enter quantity purchased or type X to cancel: ");
                        searchCustQuant = searchFilter.next();
                        if (searchCustQuant.matches("X")) {
                            searchCustQuant = null;
                            System.out.println("Filter canceled.");
                        } else {
                            System.out.println("Quantity filter set.");
                            System.out.println("Generating results... ");
                            option = "N";
                        }
                        break;

                    case 'O':
                        System.out.println("Enter Order ID or type X to cancel: ");
                        searchCustOID = searchFilter.next();
                        if (searchCustOID.matches("X")) {
                            searchCustOID = null;
                            System.out.println("Filter canceled.");
                        } else {
                            System.out.println("Order ID filter set.");
                            System.out.println("Generating results... ");
                            option = "N";
                        }
                        break;

                    default:
                        System.out.println("Invalid option");
                        break;
                }
            } while (option.equals("Y"));


            // Building the sql query string with added filter

            try {
                String sqlQuery2 = "SELECT date, cust_email, cust_location, product_id, product_quantity, order_id FROM orders WHERE";

                if (searchCustDate != null) {
                    sqlQuery2 += " date = '" + searchCustDate + "'";
                } else if (searchCustEmail != null) {
                    sqlQuery2 += " cust_email = '" + governance.getHash(searchCustEmail, connection) + "'";
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

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                while (results2.next()) {
                    String date = df.format(results2.getDate("date"));
                    String cust_email = results2.getString("cust_email");
                    int cust_location = results2.getInt("cust_location");
                    String product_id = results2.getString("product_id");
                    int product_quantity = results2.getInt("product_quantity");
                    String order_id = results2.getString("order_id");

                    cust_email = governance.unHash(cust_email, connection);
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
    }


    /* This method generates a product recommendation based off of a user's location.
     * First it searches for all orders from the customer.
     * If the customer has no existing orders, a random product will be provided instead.
     * Else, it will then search for any other orders placed by customers with the same zipcode.
     * If no other customers are found in the area, it will instead search for the first 10 orders with the nearest zipcodes */
    public String getRecommend(String email){
        //Generate sql query string t osearch for all orders from given customer
        String sqlQuery3 = "SELECT cust_email, cust_location, product_id, product_quantity FROM orders WHERE cust_email = '" + governance.getHash(email,connection) + "'";
        String output;
        try{
            PreparedStatement statement = connection.prepareStatement(sqlQuery3);
            ResultSet verify = statement.executeQuery(sqlQuery3);

            //Check if customer has any orders
            if(verify.next()){
                //Search for other orders placed in the same zipcode as given customer
                int zipcode = verify.getInt("cust_location");
                String sqlQuery4 = "SELECT product_id, product_quantity FROM orders WHERE cust_location = " + zipcode +  " AND cust_email != '" + governance.getHash(email,connection) + "'";
                PreparedStatement statement2 = connection.prepareStatement(sqlQuery4, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet catalog = statement2.executeQuery(sqlQuery4);

                //Check if other customers with the same zipcode exist
                if(catalog.next()){
                    //Get random product id from result set
                    output = getRandomProduct(catalog);
                } else {
                    //Get first 10 orders with the nearest zipcodes
                    String sqlQuery5 = "SELECT product_id, product_quantity FROM orders WHERE cust_location != " + zipcode + " ORDER BY ABS(cust_location - " + zipcode + ") LIMIT 10";
                    PreparedStatement statement3 = connection.prepareStatement(sqlQuery5, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet neighbor = statement3.executeQuery(sqlQuery5);

                    //Get random product id from result set
                    output = getRandomProduct(neighbor);

                }
            } else {
                //Find a random product from inventory table
                String sqlQuery4 = "SELECT * FROM inventory";
                PreparedStatement statement2 = connection.prepareStatement(sqlQuery4, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet catalog = statement2.executeQuery(sqlQuery4);

                //Get random product id from result set
                output = getRandomProduct(catalog);
            }
            //Return product id
            return output;
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "Error.";
    }

    /* This method handles the backend logic for the remarketing emails. The method accepts a product id and a user email and generates
     * a recommended product id based on what other customers who bought the same product bought alongside it.
     * It first searches for every other order for the same product aside from the current customer.
     * If no one else ordered the same product before, recommendation will be based on location rather than previous orders (using getRecommend)
     * Else, it will select a random entry from the resulting query and get the date and customer email from that order entry.
     * It will then search for other products purchased by that customer on that date.
     * If no other product was purchased that day, it will instead search for the product purchased closest to the date.
     * If there are still no products found, it will search by location (using getRecommend) */
    public String RemarketRecommend(String product_id, String email){
        String recommend;

        //Generate sql query string to search for other orders purchasing the same product
        String sqlQuery = "SELECT * FROM orders WHERE product_id = '" + product_id + "' AND cust_email != '" + governance.getHash(email,connection) + "'";

        //Create DateFormat object for formatting Date object
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try{
            //Execute initial query
            PreparedStatement statement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet customers = statement.executeQuery();

            //Search by location using getRecommend if query returns no results
            if (customers.first() == false){
                recommend = getRecommend(email);
            } else {
                //Go to last row to get size of result set
                customers.last();
                int size = customers.getRow();

                //Generate a random number from the size of the result set
                Random rand = new Random();
                int int_random = rand.nextInt(size) + 1;

                //Go to row number in result set equal to the random number generated
                customers.absolute(int_random);

                //Get date from result set entry
                String date = df.format(customers.getDate("date"));

                //Generate and execute sql query to search for other products based on date gotten from 2nd query 
                String sqlQuery2 = "SELECT * FROM orders WHERE cust_email = '" + customers.getString("cust_email") + "' AND date(date) = '" + date + "' AND product_id != '" + product_id + "'";
                PreparedStatement statement2 = connection.prepareStatement(sqlQuery2, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet catalog = statement2.executeQuery();

                //Search by closest date if no other products purchased on date gotten from 2nd query
                if  (catalog.first() == false) {
                    String sqlQuery3 = "SELECT * FROM orders WHERE cust_email = '" + customers.getString("cust_email") + "' AND product_id != '" + product_id + "' ORDER BY ABS(`order_id` - '" + customers.getString("order_id") + "')";
                    PreparedStatement statement3 = connection.prepareStatement(sqlQuery3, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet catalog2 = statement3.executeQuery();

                    //Search by location using getRecommend if query returns no results
                    if (catalog2.first() == false) {
                        recommend = getRecommend(email);
                    }
                    else {
                        //Get random entry from result set
                        catalog2.last();
                        int size3 = catalog2.getRow();
                        int_random = rand.nextInt(size3) + 1;
                        catalog2.absolute(int_random);
                        recommend = catalog2.getString("product_id");
                    }
                } else {
                    //Get random entry from result set
                    catalog.last();
                    int size2 = catalog.getRow();
                    int_random = rand.nextInt(size2) + 1;
                    catalog.absolute(int_random);
                    recommend = catalog.getString("product_id");
                }
            }
            //Return resulting product id
            return recommend;
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "Error";
    }


    /* This method generates a list of 10 products that are under $20 from the inventory table
     * The products searched for are based on the page number passed to the method.
     * Ex: page = 1 searches for the first 10 products under $20, page = 2 searches for the next 20 and so on. */
    public String getUnder20(int page){
        //Generate sql query string to search for all products in inventory table less than or equal to $20
        String sqlQuery = "SELECT * FROM inventory WHERE sale_price <= 20";
        String output = "Products Under $20 - Page " + page + ":\n\n"
                + String.format("%-18s%-15s\n", "Product ID","Sale Price")
                + "----------------------------\n\n";
        try{
            //Execute query
            PreparedStatement statement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet catalog = statement.executeQuery(sqlQuery);

            //Instantiate objects to store line of text, product id, and sale price
            String text;
            String product_id;
            double sale_price;

            //Set upper and lower bounds for row numbers in result set
            int upper = page * 10;
            int lower = upper - 10;

            //Generate list of products under $20
            for (int i = lower; i < upper; i++){
                //Go to row number in result set based on i
                catalog.absolute(i+1);
                product_id = catalog.getString("product_id");
                sale_price = catalog.getDouble("sale_price");
                
                //Generate text entry
                text = String.format("%-18s%-15s\n\n", product_id, sale_price);

                //Append text entry to output string
                output += text;
            }
            return output;
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "Error.";
    }

    /* This method returns a random product id from a given result set. */
    public String getRandomProduct (ResultSet catalog){
        try{
            //Go to last row to get size of result set
            catalog.last();
            int size = catalog.getRow();

            //Generate a random number from the size of the result set
            Random rand = new Random();
            int int_random = rand.nextInt(size) + 1;

            //Go to row number in result set equal to the random number generated
            catalog.absolute(int_random);

            //Get product id from result set entry and return
            String product_id = catalog.getString("product_id");
            return product_id;
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "Error.";
    }

    public String getOrders(String email){
        //Note that we currently use plaintext emails in the orders database. This code will be changed when we switch to hashed ones.
        String sqlQuery2 = "SELECT order_id, date, cust_email, cust_location, product_id, product_quantity FROM orders WHERE"
                + " cust_email = '" + governance.getHash(email,connection) + "'";
        String output = "Orders:\n";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery2);
            ResultSet results = statement.executeQuery(sqlQuery2);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            while (results.next()) {
                int order_id = results.getInt("order_id");
                String date = df.format(results.getDate("date"));
                String cust_email = results.getString("cust_email");
                int cust_location = results.getInt("cust_location");
                String product_id = results.getString("product_id");
                int product_quantity = results.getInt("product_quantity");
                cust_email = email;
                //System.out.printf("%-15s%-20s%-15s%-20s%-15s\n", date, cust_email, cust_location, product_id, product_quantity);
                /*String text = "Date: " + date + " Email: " + cust_email + " Location: " + cust_location+ " Product: "
                        + product_id + " Quantity: " + product_quantity;*/
                String text = String.format("%-7s%-15s%-30s%-10s%-15s%-4s\n",order_id, date, cust_email, cust_location, product_id, product_quantity);
                output = output + text + "\n";
            }
            return output;
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "Error.";
    }

    //Takes a Day and returns the number of orders that occurred that day.
    public int getNumOrdersForDay(Day date){
        //Search the orders database for orders made on that date.
        String sqlQuery2 = "SELECT date, cust_email, cust_location, product_id, product_quantity FROM orders WHERE"
                + " date LIKE '" + date.getDay() + "%'";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery2);
            ResultSet results = statement.executeQuery(sqlQuery2);
            //For each result, increment i
            int i = 0;
            while (results.next()) {
                i++;
            }
            //Return our number of orders for the day
            return i;
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    //Takes a Day and returns the revenue from the orders for that day.
    public double getRevenueForDay(Day date){
        //Search the orders database for orders made on that date.
        String sqlQuery = "SELECT date, cust_email, cust_location, product_id, product_quantity FROM orders WHERE"
                + " date LIKE '" + date.getDay() + "%'";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet results = statement.executeQuery(sqlQuery);
            //For each result, increment totalMoney by the cost of the quantity multiplied by the product's cost.
            double totalMoney = 0.0;
            while (results.next()) {
                String product = results.getString("product_id");
                int quantity = results.getInt("product_quantity");
                double productCost;
                //get the cost of the item in the inventory
                String sqlQuery2 = "SELECT product_id,  quantity, wholesale_cost, "
                        + "sale_price, supplier_id FROM inventory "
                        + "WHERE product_id= '" + product + "'";
                PreparedStatement statement2 = connection.prepareStatement(sqlQuery2);
                ResultSet results2 = statement2.executeQuery(sqlQuery2);
                if(results2.next()){
                    productCost = results2.getDouble("sale_price");
                } else {
                    //if the product doesn't exist, notify the user of the problem and default to 0 cost.
                    System.out.println("Product does not exist in inventory for: " + product);
                    productCost = 0;
                }
                totalMoney = totalMoney + (quantity * productCost);
            }
            //return the total revenue.
            return totalMoney;
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    //Get the date an order was made by the orderID AND the email. Not just one or the other.
    public String getOrderDate(String email, String orderID){
        try{
            String sql = "SELECT date FROM orders WHERE order_id = '" + orderID + "' and cust_email = '" + email.hashCode() + "'";
            PreparedStatement s2 = connection.prepareStatement(sql);
            ResultSet rs = s2.executeQuery();
            rs.next();
            return rs.getString("date");
        } catch (SQLException e){
            return "Error.";
        }
    }

}
