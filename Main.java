import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		menuLoop();
	}
	static Scanner input = new Scanner(System.in);

	public static void menuLoop() {
		String error = "error";
		while(error.contentEquals("error")) {
			System.out.println("Welcome to the inventory database editor.");
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
				Database db = new Database(username, password, url);
				optionLoop(db);
			} else if (csvReply.contentEquals("N")) {
				System.out.println("Please provide login info");
				Scanner input = new Scanner(System.in);
				System.out.println("Username: ");
				String username = input.nextLine();
				System.out.println("Password: ");
				String password = input.nextLine();
				Database db = new Database(username, password);
				optionLoop(db);
			} else if (csvReply.contentEquals("X")) {
				System.out.println("Exiting program...");
				break;
			} else {
				error = "error";
				System.out.println("Invalid Response. Please enter Y or N.");
			}
		}
	}
	public static void optionLoop(Database db) {
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
				System.out.println("[G] to generate report with filters");
				System.out.println("[S] to simulate customer orders");
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
				} else if(editOption.contentEquals("G")) {
					System.out.println("-----------Generate Report-----------");
					db.search();
				} else if(editOption.contentEquals("S")) {
					System.out.println("-----------Simulating orders-----------");
					db.importCustomerData();
					System.out.println("-----------Complete!-----------");
				} else {
					System.out.println("Invalid Response. Please enter a valid option.");
				}
			} else if (editReply.contentEquals("N")) {
				notDone = "N";
			} else {
				System.out.println("Invalid Response. Please enter Y or N.");
			}
		}
		db.closeConnection(); //Cleans up out connection
	}
}









