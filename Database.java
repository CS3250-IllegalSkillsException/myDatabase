import java.util.*;


public static void modify() {
    PreparedStatement preparedStatement;
    Connection connection;
    String jdbcURL = "jdbc:mysql://localhost:3306/test";
    String username = "root";
    String password = "SMapi3407";
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter Product ID to edit: ");
    String prodIDinput = scanner.nextLine();
    try {
        connection = DriverManager.getConnection(jdbcURL, username, password);

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

        System.out.println("Updates complete.");


    } catch (SQLException e) {
        e.printStackTrace();
    }
    scanner.close();
}