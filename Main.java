import java.util.Scanner;
import java.io.Console;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    private static final Boolean True = null;

    public static void main(String[] args) throws SQLException, ParseException, FileNotFoundException {
        menuLoop();
    }

    static Scanner input = new Scanner(System.in);

    public static void menuLoop() throws ParseException, SQLException, FileNotFoundException {
        String username = null;
        String password = null;
        String menuConfirm = "Y";
        Console console = System.console();
        System.out.printf("\n.___.__  .__                      .__    ___________   .__.__  .__          \n" +
                "|   |  | |  |   ____   _________  |  |  /   _____/  | _|__|  | |  |   ______\n" +
                "|   |  | |  | _/ __ \\ / ___\\__  \\ |  |  \\_____  \\|  |/ /  |  | |  |  /  ___/\n" +
                "|   |  |_|  |_\\  ___// /_/  > __ \\|  |__/        \\    <|  |  |_|  |__\\___ \\ \n" +
                "|___|____/____/\\___  >___  (____  /____/_______  /__|_ \\__|____/____/____  >\n" +
                "                   \\/_____/     \\/             \\/     \\/                 \\/ \n" +
                "___________                           __  .__               \n" +
                "\\_   _____/__  ___ ____  ____ _______/  |_|__| ____   ____  \n" +
                " |    __)_\\  \\/  // ___\\/ __ \\\\____ \\   __\\  |/  _ \\ /    \\ \n" +
                " |        \\>    <\\  \\__\\  ___/|  |_> >  | |  (  <_> )   |  \\\n" +
                "/_______  /__/\\_ \\\\___  >___  >   __/|__| |__|\\____/|___|  /\n" +
                "        \\/      \\/    \\/    \\/|__|                       \\/ \n\n");
        System.out.println("-----Welcome to the inventory database editor-----");
        while (menuConfirm.contentEquals("Y") | menuConfirm.contentEquals("y")) {
            System.out.println("Would you like to use a database other than the local database? (Y/N) Enter X to quit.");
            String csvReply = input.nextLine();
            if (csvReply.contentEquals("Y")) {
                System.out.println("Please provide login info");
                Scanner input = new Scanner(System.in);
                System.out.println("Username: ");
                username = input.nextLine();
                if (console != null) {
                    char[] pwd = console.readPassword("Password: ");
                    password = new String(pwd);
                } else {
                    System.out.println("Password: ");
                    password = input.nextLine();
                }
                System.out.println("Database URL: ");
                String url = input.nextLine();
            } else if (csvReply.contentEquals("N")) {
                System.out.println("Please provide login info");
                Scanner input = new Scanner(System.in);
                System.out.println("Username: ");
                username = input.nextLine();

                /* This is the console bypass that will allow us to use the Console within the command
                line to hide inputted passwords. If Console class cannot be used (running within many IDEs)
                it will default to not hiding the password. */

                // for running in command line
                if (console != null) {
                    char[] pwd = console.readPassword("Password: ");
                    password = new String(pwd);

                    // for running in IDE
                } else {
                    System.out.println("Password: ");
                    password = input.nextLine();
                }
                Database data = new Database(username, password);
                /* This is the menu that allows the user to select which table to manipulate */
                String databaseConfirm = "Y";
                while (databaseConfirm.contentEquals("Y") | databaseConfirm.contentEquals("y")) {
                    System.out.println("Which table would you like to edit? \n"
                            + " A. Inventory Table\n"
                            + " B. Users Table\n"
                            + " C. Orders Table");
                    char choice = input.next().charAt(0);
                    input.nextLine();
                    switch (choice) {
                        case 'A':
                            inventoryTable(data);
                            System.out.println("Would you like to keep editing the current database? (Y/N)");
                            databaseConfirm = input.nextLine();
                            break;

                        case 'B':
                            usersTable(data);
                            System.out.println("Would you like to keep editing the current database? (Y/N)");
                            databaseConfirm = input.nextLine();

                            break;

                        case 'C':
                            ordersTable(data);
                            System.out.println("Would you like to keep editing the current database? (Y/N)");
                            databaseConfirm = input.nextLine();
                            break;
                    }
                }
                System.out.println("Would you like to edit another database?");
                menuConfirm = input.nextLine();
            } else if (csvReply.contentEquals("X") | menuConfirm.contentEquals("N")) {
                System.out.println("Exiting program...");
                break;
            } else {
                System.out.println("Invalid Response. Please enter Y or N.");
            }
        }
        if (menuConfirm.contentEquals("N") | menuConfirm.contentEquals("n")) {
            System.out.println("Exiting..");
        }
    }
    
    public static void inventoryTable(Database genericDB) {
        Inventory db = new Inventory(genericDB);
        String notDone = "";
        while (!notDone.equals("N")) {
            System.out.println("Would you like to edit your CSV file? (Y/N)");
            String editReply = input.nextLine();
            if (editReply.contentEquals("Y")) {
                notDone = "";
                System.out.println("Please enter one of the following options.");
                System.out.println("[Q] to import from csv file");
                System.out.println("[I] to insert new entry");
                System.out.println("[D] to delete an entry");
                System.out.println("[M] to modify an entry");
                System.out.println("[R] to read an entry");
                System.out.println("[E] to export CSV file");
                System.out.println("[G] to generate report with filters");
                System.out.println("[X] to leave menu");
                String editOption = input.nextLine();
                if (editOption.contentEquals("Q")) {
                    System.out.println("------Importing... Please wait------");
                    db.importFromCsvFile();
                    System.out.println("-----------Complete!-----------");
                } else if (editOption.contentEquals("I")) {
                    System.out.println("-----------New Entry-----------");
                    System.out.println("New Product ID: ");
                    String product_id = input.nextLine();
                    System.out.println("New Quantity: ");
                    String quantity = input.nextLine();
                    System.out.println("New Wholesale Cost: ");
                    String wholesale_cost = input.nextLine();
                    System.out.println("New Sale Price: ");
                    String sale_price = input.nextLine();
                    System.out.println("New Supplier ID: ");
                    String supplier_id = input.nextLine();
                    db.insert(product_id, quantity, wholesale_cost, sale_price, supplier_id);
                } else if (editOption.contentEquals("D")) {
                    //Execute delete method
                    System.out.println("-----------Delete Entry-----------");
                    System.out.println("Enter Product ID: ");
                    String product_id = input.nextLine();
                    boolean verify = db.exists("inventory", "product_id", product_id);
                    if (!verify) {
                        System.out.println("That product id does not exist.");
                    }
                    else {
                        //Execute delete method
                        db.delete("inventory", "product_id", product_id);
                        System.out.println("Deleting Product ID: " + product_id);
                    }
                } else if (editOption.contentEquals("M")) {
                    System.out.println("-----------Modify Entry-----------");
                    db.modify();
                } else if (editOption.contentEquals("R")) {
                    System.out.println("-----------Read Entry-----------");
                    db.read();
                } else if (editOption.contentEquals("E")) {
                    System.out.println("-----------Exporting CSV-----------");
                    db.exportInvCSV();
                } else if (editOption.contentEquals("G")) {
                    System.out.println("-----------Generate Report-----------");
                    db.searchInventory();
                } else if (editOption.contentEquals("X")) {
                    System.out.println("\n");
                    notDone = "N";
                } else {
                    System.out.println("Invalid Response. Please enter a valid option.");
                }
            } else if (editReply.contentEquals("N")) {
                notDone = "N";
            } else {
                System.out.println("Invalid Response. Please enter Y or N.");
            }
        }
    }

    public static void usersTable(Database genericDB) {
        Users db = new Users(genericDB);
      	System.out.println("Would you like to: \n" +
				        "A. Create a new user \n" +
				        "B. Find User Email \n" +
				        "C. Export CSV file \n" +
      			"X. Exit menu");
        char choice = input.next().charAt(0);
        input.nextLine();
        switch (choice) {
            case 'A':
                System.out.println("Is this an admin user? (Y/N): ");
                String pass1 = input.nextLine();
                if (pass1.contentEquals("Y") || pass1.contentEquals("y")) {
                    System.out.println("-----New User-----");
                    System.out.println("User ID: ");
                    String newID = input.nextLine();
                    System.out.println("Enter Email: ");
                    String newEmail = input.nextLine();
                    int email = newEmail.hashCode();
                    System.out.println("Enter Password: ");
                    String newPass = input.nextLine();
                    int pass = newPass.hashCode();
                    db.insertUser(newID, email, pass, true);
                } else {
                    System.out.println("-----New User-----");
                    System.out.println("User ID: ");
                    String newID = input.nextLine();
                    boolean isAdmin = false;
                    System.out.println("Enter Email: ");
                    String reqEmail = input.nextLine();
                    int reqE = reqEmail.hashCode();
                    System.out.println("Enter Password: ");
                    String reqPass = input.nextLine();
                    int reqP = reqPass.hashCode();
                    db.insertUser(newID, reqE, reqP, isAdmin);
                }
                break;

            case 'B':
                System.out.println("-----Login Info-----");
                System.out.println("Email: ");
                String logEmail = input.nextLine();
                int hashE = logEmail.hashCode();
                System.out.println("Password: ");
                String logPass = input.nextLine();
                int hashP = logPass.hashCode();
                db.userExists(logEmail);
                if (db.userExists(logEmail)) {
                    if (db.passExists(logPass)) {
                        System.out.println("Email exists in USER table");
                    } else {
                        System.out.println("Incorrect Password\n");
                        for (int numAttempt = 2; numAttempt > 0; numAttempt--) {
                            System.out.println("Attempts left: " + numAttempt);
                            System.out.println("Enter Password Again: ");
                            String tryAgain = input.nextLine();
                            if (!(db.passExists(tryAgain))) {
                                System.out.println("Incorrect Password");
                            } else {
                                System.out.println("Email exists in USER table");
                            }
                        }
                    }
                } else {
                    System.out.println("Email not found: Please sign up or see Admin");
                }
                break;
            case 'C':
				        System.out.println("-----------Exporting CSV-----------");
				        db.exportUsersCSV();
				        break;
            case 'X':
				        System.out.println("Leaving menu....\n");
			          break;
        }
    }

    public static void ordersTable(Database genericDB) throws ParseException, SQLException, FileNotFoundException {
        Orders db = new Orders(genericDB);
        String notDone = "";
        Orders orders = new Orders(genericDB);
        BestCustomer customer = new BestCustomer(genericDB);
        while (!notDone.equals("N")) {
            System.out.println("Would you like to edit your CSV file? (Y/N)");
            CustomerReplyandCancel test = new CustomerReplyandCancel(db.getUsername(), db.getPassword());
            String editReply = input.nextLine();
            if (editReply.contentEquals("Y")) {
                notDone = "";
                System.out.println("Please enter one of the following options.");
                System.out.println("[S] to import customer orders from csv");
                System.out.println("[I] to insert new customer order");
                System.out.println("[D] to delete a customer order");
                System.out.println("[P] to place a customer order");
                System.out.println("[C] to cancel a customer order");
                System.out.println("[G] to generate report with filter");
                System.out.println("[E] to export CSV file");
                System.out.println("[F] to display finance reports");
                System.out.println("[X] to leave menu");
                String editOption = input.nextLine();
                if (editOption.contentEquals("S")) {
                    System.out.println("-----------Importing orders-----------");
                    db.importCustomerData();
                    System.out.println("-----------Complete!-----------");
                } else if (editOption.contentEquals("I")) {
                    //Get new order information from user
                    System.out.println("-----------New Entry-----------");
                    System.out.println("Customer email: ");
                    String cust_email = input.nextLine();
                    System.out.println("Customer location: ");
                    String cust_location = input.nextLine();
                    System.out.println("Product ID: ");
                    String product_id = input.nextLine();
                    //Verify product id exists
                    boolean verify = db.exists("inventory", "product_id", product_id);
                    while (!verify){
                        System.out.println("Invalid product id. Please enter a valid product id:");
                        product_id = input.nextLine();
                        verify = db.exists("inventory", "product_id", product_id);
                    }
                    System.out.println("Quantity: ");
                    String quantity = input.nextLine();
                    //Get current datetime and format
                  SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = temp.format(new Date());
                    //Verify positive quantity
                    while (Integer.parseInt(quantity) <= 0) {
                        System.out.println("Invalid quantity. Please enter a quantity greater than 0.");
                        quantity = input.nextLine();
                    }
                    //Execute insertOrders method
                    db.insertOrders(date, cust_email, cust_location, product_id, quantity);
                } else if (editOption.contentEquals("D")) {
                    //Get order id from user
                    System.out.println("-----------Delete Entry-----------");
                    System.out.println("Order ID: ");
                    String order_id = input.nextLine();
                    //Verify order id exists
                    boolean verify = db.exists("orders", "order_id", order_id);
                    if (!verify) {
                        System.out.println("That order id does not exist.");
                    }
                    else {
                        //Execute deleteOrders method
                        db.delete("orders", "order_id", order_id);
                        System.out.println("Deleting Order ID: " + order_id);
                    }
                } else if (editOption.contentEquals("P")) {
                    System.out.println("-----------New Order-----------");
                    System.out.println("Customer email: ");
                    String cust_email = input.nextLine();
                    System.out.println("Customer location: ");
                    String cust_location = input.nextLine();
                    System.out.println("Product ID: ");
                    String product_id = input.nextLine();
                    //Verify product id exists
                    boolean verify = db.exists("inventory", "product_id", product_id);
                    while (!verify){
                        System.out.println("Invalid product id. Please enter a valid product id:");
                        product_id = input.nextLine();
                        verify = db.exists("inventory", "product_id", product_id);
                    }
                    System.out.println("Quantity: ");
                    String quantity = input.nextLine();
                    SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                  
                    String date = temp.format(new Date());
                    while (Integer.parseInt(quantity) <= 0) {
                        System.out.println("Invalid quantity. Please enter a quantity greater than 0.");
                        quantity = input.nextLine();
                    }
                    String productRecommend = db.RemarketRecommend(product_id, cust_email);
                    db.insertOrders(date, cust_email, cust_location, product_id, quantity);
                    test.customerConfirm(cust_email, date, product_id, quantity, cust_location);
                    test.customerRecommend(cust_email, productRecommend);

                } else if (editOption.contentEquals("C")) {
                    System.out.println("-----------Cancel Order-----------");
                    System.out.println("Order ID: ");
                    String order_id = input.nextLine();
                    System.out.println("Email: ");
                    String cust_email = input.nextLine();
                    String orderDate = test.findDate(order_id);
                    if (test.withinCancellatioWindow(orderDate)) {
                        test.customerCancel(cust_email, order_id, orderDate);
                        System.out.println("Cancellation successful!");
                        db.delete("orders", "order_id", order_id);
                    } else {
                        System.out.println("Sorry, passed cancellation window");
                    }

                } else if (editOption.contentEquals("G")) {
                    System.out.println("-----------Generate Report-----------");
                    db.searchOrders();
                } else if (editOption.contentEquals("E")) {
                    System.out.println("What report would you like to export?");
                    System.out.println("[F] Full orders report");
                    System.out.println("[B] Best-selling products report");
                    System.out.println("[C] Highest-spending customers report");
                    String subOption = input.nextLine();
                    if (subOption.contentEquals("F")) {
                        System.out.println("-----------Exporting CSV-----------");
                        db.exportOrdersCSV();
                    } else if (subOption.contentEquals("B")) {
                        orders.orderReport();
                    } else if (subOption.contentEquals("C")) {
                    	customer.customerReport();
                    }
                } else if (editOption.contentEquals("F")) {
                    System.out.println("Input the start date yyyy-MM-dd");
                    String start = input.nextLine();
                    System.out.println("Input the end date yyyy-MM-dd");
                    String end = input.nextLine();
                    System.out.println("Displaying finance reports");
                    FinanceReports report = new FinanceReports(db,start,end);
                    report.displayFinanceReports();
                    System.out.println("Enter anything to see the order report");
                    input.nextLine();
                    FinanceReports report2 = new FinanceReports(db,start,end);
                    report2.displayOrderReports();
                } else if(editOption.contentEquals("X")) {
					          System.out.println("\n");	
					          notDone = "N";
                } else {
                    System.out.println("Invalid Response. Please enter a valid option.");
                }
            } else if (editReply.contentEquals("N")) {
                notDone = "N";
            } else {
                System.out.println("Invalid Response. Please enter Y or N.");
            }
        }
    }
}
