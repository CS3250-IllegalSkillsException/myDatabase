import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
			menuLoop();
	}
	static Scanner input = new Scanner(System.in);

	public static void menuLoop() {
		String menuConfirm = "Y";
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
		System.out.println("Would you like to: \n"+
							"1. Create a new user \n" + 
							"2. Find User ID");
		int choice = input.nextInt();
		input.nextLine();
		switch(choice) {
			case 1: 
				String adminPass = "rootUser";
				System.out.println("Please input Admin password: ");
				String pass1 = input.nextLine();
				if (pass1.contentEquals(adminPass)) {
					
				} else {
					System.out.println("Access Denied");
				}
			break;
			
			case 2:
				System.out.println("-----Login Info-----");
				System.out.println("Email: ");
				String email = input.nextLine();
				System.out.println("Password: ");
				String pass2 = input.nextLine();
				
				System.out.println("User ID: ");
			break;
		}
	}
	
	public static void ordersTable() {
		
	}
}
