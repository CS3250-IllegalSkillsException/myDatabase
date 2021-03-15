import java.util.Scanner;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
	private static final Boolean True = null;

	public static void main(String[] args) throws SQLException, ParseException {
		menuLoop();	
		
	}
	static Scanner input = new Scanner(System.in);

	public static void menuLoop() throws ParseException, SQLException {
		String menuConfirm = "Y";
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
		while(menuConfirm.contentEquals("Y") | menuConfirm.contentEquals("y")) {
			System.out.println("Would you like to use a database other than the local database? (Y/N) Enter X to quit.");
			String csvReply = input.nextLine();
			if (csvReply.contentEquals("Y")){
				System.out.println("Please provide login info");
				Scanner input = new Scanner(System.in);
				System.out.println("Username: ");
				String username = input.nextLine();
				System.out.println("Password: ");
				String password = input.nextLine();
				System.out.println("Database URL: ");
				String url = input.nextLine();
			} else if (csvReply.contentEquals("N")) {
				System.out.println("Which table would you like to edit? \n"
            			+ " 1. Inventory Table\n"
            			+ " 2. Users Table\n"
            			+ " 3. Orders Table");
				int choice = input.nextInt();
				input.nextLine();
				switch(choice) {
					case 1: 
						inventoryTable();
						System.out.println("Would you like to edit another database? (Y/N)");
						menuConfirm = input.nextLine();
					break;
					
					case 2:
						usersTable();
						System.out.println("Would you like to edit another database? (Y/N)");
						menuConfirm = input.nextLine();
						
					break;
					
					case 3:
						ordersTable();
						System.out.println("Would you like to edit another database? (Y/N)");
						menuConfirm = input.nextLine();
					break;
				}
			}  else if (csvReply.contentEquals("X") | menuConfirm.contentEquals("N")) {
				System.out.println("Exiting program...");
				break;
			} else {
				System.out.println("Invalid Response. Please enter Y or N.");
			}
		}
		if(menuConfirm.contentEquals("N") | menuConfirm.contentEquals("n")) {
			System.out.println("Exiting..");
		}
	}
	public static void inventoryTable() {
		System.out.println("Please provide login info");
		Scanner input = new Scanner(System.in);
		System.out.println("Username: ");
		String username = input.nextLine();
		System.out.println("Password: ");
		String password = input.nextLine();
		Database db = new Database(username, password);
		String notDone = "";
		while(notDone != "N") {
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
				String editOption = input.nextLine();
				if(editOption.contentEquals("Q")) {
					System.out.println("------Importing... Please wait------");
					db.importFromCsvFile();
					System.out.println("-----------Complete!-----------");
				} else if(editOption.contentEquals("I")) {
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
				} else if(editOption.contentEquals("D")) {
					System.out.println("-----------Delete Entry-----------");
					System.out.println("Enter Product ID: ");
					String product_id = input.nextLine();
					db.delete(product_id);
				} else if(editOption.contentEquals("M")) {
					System.out.println("-----------Modify Entry-----------");
					db.modify();
				} else if(editOption.contentEquals("R")) {
					System.out.println("-----------Read Entry-----------");
					db.read();
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
	
	public static void usersTable() {
		System.out.println("Please provide login info");
		Scanner input = new Scanner(System.in);
		System.out.println("Username: ");
		String username = input.nextLine();
		System.out.println("Password: ");
		String password = input.nextLine();
		dataGovernance data = new dataGovernance(username, password);
		System.out.println("Would you like to: \n"+
							"1. Create a new user \n" + 
							"2. Find User Email");
		int choice = input.nextInt();
		input.nextLine();
		switch(choice) {
			case 1: 
				String adminPass = "rootUser";
				System.out.println("Please input Admin password: ");
				String pass1 = input.nextLine();
				if (pass1.contentEquals(adminPass)) {
					boolean isAdmin = true;
					System.out.println("-----New User-----");
					System.out.println("User ID: ");
					String newID = input.nextLine();
					System.out.println("Enter Email: ");
					String newEmail = input.nextLine();
					int email = newEmail.hashCode();
					System.out.println("Enter Password: ");
					String newPass = input.nextLine();
					int pass = newPass.hashCode();
					data.insertUser(newID, email, pass, isAdmin);
					
					System.out.println("New Hashed Values");
					System.out.println("Hashed Email" + email);
					System.out.println("Hashed Password" + pass);
					
					//print hashed password
				} else {
					boolean isAdmin = false;
					System.out.println("-----Access Denied-----");
					System.out.println("Request Sign-Up");
					System.out.println("Enter Email: ");
					String reqEmail = input.nextLine();
					int reqE = reqEmail.hashCode();
					System.out.println("Enter Password: ");
					String reqPass = input.nextLine();
					int reqP = reqPass.hashCode();
					data.insertUser("",reqE,reqP,isAdmin);
				}
			break;
			
			case 2:
				System.out.println("-----Login Info-----");
				System.out.println("Email: ");
				String logEmail = input.nextLine();
				int hashE = logEmail.hashCode();
				System.out.println("Password: ");
				String logPass = input.nextLine();
				int hashP = logPass.hashCode();
				data.userExists(logEmail);
				if(data.userExists(logEmail)) {
					if(data.passExists(logPass)) {
						System.out.println("Email exists in USER table");
					} else {
						System.out.println("Incorrect Password\n");
						for(int numAttempt = 2; numAttempt > 0; numAttempt--) 
						{
						System.out.println("Attempts left: "+ numAttempt);
						System.out.println("Enter Password Again: ");
						String tryAgain = input.nextLine();
						if(!(data.passExists(tryAgain))) {
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
		}
	}
	
	public static void ordersTable() throws ParseException, SQLException {
		System.out.println("Please provide login info");
		Scanner input = new Scanner(System.in);
		System.out.println("Username: ");
		String username = input.nextLine();
		System.out.println("Password: ");
		String password = input.nextLine();
		Database db = new Database(username, password);
		CustomerReplyandCancel test = new CustomerReplyandCancel(username, password);
		String notDone = "";
		while(notDone != "N") {
		System.out.println("Would you like to edit your CSV file? (Y/N)");
		String editReply = input.nextLine();
			if (editReply.contentEquals("Y")) {
				notDone = "";
				System.out.println("Please enter one of the following options.");
				System.out.println("[S] to import customer orders from csv");
				System.out.println("[I] to insert new customer order");
				System.out.println("[D] to delete a customer order");
        System.out.println("[G] to generate report with filter");
				String editOption = input.nextLine();
				if(editOption.contentEquals("S")) {
					System.out.println("-----------Importing orders-----------");
					db.importCustomerData();
					System.out.println("-----------Complete!-----------");
				} else if(editOption.contentEquals("I")) {
					System.out.println("-----------New Entry-----------");
					System.out.println("Customer email: ");
					String cust_email = input.nextLine();
					System.out.println("Customer location: ");
					String cust_location = input.nextLine();
					System.out.println("Product ID: ");
					String product_id = input.nextLine();
					System.out.println("Quantity: ");
					String quantity = input.nextLine();
					//work in progress
                    SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            		String date = temp.format(new Date());
					while(Integer.parseInt(quantity) <= 0){
						System.out.println("Invalid quantity. Please enter a quantity greater than 0.");
						quantity = input.nextLine();
					}
					db.insertOrders(date,cust_email, cust_location, product_id, quantity);
					test.customerConfirm(cust_email, date, product_id, quantity);
				} else if(editOption.contentEquals("D")) {
					System.out.println("-----------Delete Entry-----------");
					System.out.println("Order ID: ");
					String order_id = input.nextLine();
					System.out.println("Email: ");
					String cust_email = input.nextLine();
					String orderDate = test.findDate(order_id);
					if(test.withinCancellatioWindow(orderDate)) {
						test.customerCancel(cust_email,order_id);
						System.out.println("Cancellation successful!");
					} else {
						System.out.println("Sorry, passed cancellation window");
					}
					
        } else if(editOption.contentEquals("G")) {
					System.out.println("-----------Generate Report-----------");
					db.search();
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