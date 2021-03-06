import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Inventory extends Database {

    public Inventory(Database db){
        super(db.getUsername(),db.getPassword());
    }
    private final DataGovernance governance = new DataGovernance();

    //Go line by line through a csv and import the inventory data into the database.
    public void importFromCsvFile() {
        try {
            BufferedReader lineReader = new BufferedReader(new FileReader(csvFilePath));
            String lineText = null;
            lineReader.readLine(); // skip header line
            String sql = "INSERT INTO inventory (product_id, quantity, wholesale_cost, sale_price, supplier_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            int count = 0;
            while ((lineText = lineReader.readLine()) != null) {
                //Convert each line into arguments for the MySQL statement.
                String[] data = lineText.split(",");
                statement.setString(1, data[0]);
                statement.setString(2, data[1]);
                statement.setString(3, data[2]);
                statement.setString(4, data[3]);
                statement.setString(5, data[4]);
                statement.addBatch();
                //Every 20, execute the batch.
                if (count >= 20) {
                    statement.executeBatch();
                    count = 0;
                }
                count++;
            }
            lineReader.close();
            //Execute whatever remains in the batch.
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
/*  This function uses a string builder to create and download a csv copy of the Inventory table.
  It is specific to the Inventory table since the column names are different on each table. If any changes
  are made to the Inventory table columns, column names will need adjusting here as well.
  The downloaded file can be found in local Downloads folder. */
    public void exportInvCSV() {
        String home = System.getProperty("user.home");
        try {
            PrintWriter pw = new PrintWriter(new File(home + "\\Downloads\\Inventory.csv"));
            StringBuilder sb = new StringBuilder();
            ResultSet rs = null;
            // update here if table columns are changed
            sb.append("product_id");
            sb.append(",");
            sb.append("quantity");
            sb.append(",");
            sb.append("wholesale_cost");
            sb.append(",");
            sb.append("sale_price");
            sb.append(",");
            sb.append("supplier_id");
            sb.append("\n");

            String query = "select * from inventory";
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            // and here
            while (rs.next()) {
                sb.append(rs.getString("product_id"));
                sb.append(",");
                sb.append(rs.getString("quantity"));
                sb.append(",");
                sb.append(rs.getString("wholesale_cost"));
                sb.append(",");
                sb.append(rs.getString("sale_price"));
                sb.append(",");
                sb.append(rs.getString("supplier_id"));
                sb.append("\r\n");
            }

            pw.write(sb.toString());
            pw.close();
            System.out.println("Export complete. Find Inventory.csv in your Downloads folder.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /* This function displays the orders that match either the Product ID or Supplier ID 
     * and prints them to the console. Then it asks the User if they would like to read any other
     * entries before exiting this part of the menu.
     */
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

    /*
    Search function allows you to search the Inventory table for specific entries using filters
    set by the user. User can set one or multiple filters to be applied before displaying matching entries,
    using a string builder and SQL statements to generate results.
     */
    public void searchInventory() {
        Scanner searchFilter = new Scanner(System.in);
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
                        "[I] Product ID \n" +
                        "[S] Supplier ID \n" +
                        "[Q] Quantity \n" +
                        "[C] Wholesale Cost \n" +
                        "[P] Sale Price");

                char choice = searchFilter.next().charAt(0);
                searchFilter.nextLine();
                switch (choice) {
                    case 'I':
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


                    case 'S':
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


                    case 'Q':
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


                    case 'C':
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


                    case 'P':
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
            } while (option.equals("Y"));


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

    /* This function manipulates the data in the db tables, allowing the user to change quantities, costs/prices, or IDs.
    Each case statement is for each value that can be modified using SQL statements.
     */
    public void modify() {
        PreparedStatement preparedStatement;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Product ID to edit: ");
        String prodIDinput = scanner.nextLine();
        try {
            String loop;
            do {
                System.out.println("Which column would you like to update?" +
                        "\n [Q] Quantity \n [C] Wholesale Cost \n [P] Sale Price \n [I] Supplier ID");

                char choice = scanner.next().charAt(0);
                scanner.nextLine();
                switch (choice) {
                    // quantity
                    case 'Q':
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

                    // wholesale cost
                    case 'C':
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

                    // sale price
                    case 'P':
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

                    // supplier ID
                    case 'I':
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

}
