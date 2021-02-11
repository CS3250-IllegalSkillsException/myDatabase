import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		menuLoop();		
	}
	static Scanner input = new Scanner(System.in);

	public static void menuLoop() {
		String error = "error";
		while(error.contentEquals("error")) {
		System.out.println("Would you like to use a database different than 'root'? (Y/N)");
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
			db.importFromCsvFile();
			optionLoop(db);
		} else if (csvReply.contentEquals("N")) {
			System.out.println("Please provide login info");
			Scanner input = new Scanner(System.in);
			System.out.println("Username: ");
			String username = input.nextLine();
			System.out.println("Password: ");
			String password = input.nextLine();
			Database db = new Database(username, password);
			db.importFromCsvFile();
			optionLoop(db);
		} else {
			error = "error";
			System.out.println("Invalid Response. Please enter Y or N.");
			//needs to loop back
		}
	}
	}
	public static void optionLoop(Database db) {
		String notDone = "";
		while(notDone != "N") {
		System.out.println("Would you like to edit your CSV file? (Y/N)");
		String editReply = input.nextLine();
			if (editReply.contentEquals("Y")){
				notDone = "";
				System.out.println("Please enter one of the following options.");
				System.out.println("[I] to insert new entry");
				System.out.println("[D] to delete an entry");
				System.out.println("[M] to modify an entry");
				String editOption = input.nextLine();
				if(editOption.contentEquals("I")) {
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
					System.out.println("Enter Prod"
							+ "uct ID: ");
					String product_id = input.nextLine();
					System.out.println("Enter field: ");
					String choice = input.nextLine();
					db.modify();
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



