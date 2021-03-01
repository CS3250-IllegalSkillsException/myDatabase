import java.util.Scanner;
import java.sql.SQLException;
import java.util.Base64;

public class Main {
	private static final Boolean True = null;

	public static void main(String[] args) throws SQLException {
		menuLoop();

	}

	static Scanner input = new Scanner(System.in);

	public static void menuLoop() {
		String username;
		String password;
		dataGovernance db;
		String menuConfirm = "Y";
		System.out.println("-----Welcome to the inventory database editor-----");
		while (menuConfirm.contentEquals("Y") || menuConfirm.contentEquals("y")) {
			System.out
					.println("Would you like to use a database other than the local database? (Y/N) Enter X to quit.");
			String csvReply = input.nextLine();
			if (csvReply.contentEquals("Y")) {
				System.out.println("Please provide login info");
				Scanner input = new Scanner(System.in);
				System.out.println("Username: ");
				username = input.nextLine();
				System.out.println("Password: ");
				password = input.nextLine();
				System.out.println("Database URL: ");
				String url = input.nextLine();
			} else if (csvReply.contentEquals("N")) {
				System.out.println("Please provide Data Base login info");
				Scanner input = new Scanner(System.in);
				System.out.println("Username: ");
				username = input.nextLine();
				System.out.println("Password: ");
				password = input.nextLine();
				db = new dataGovernance(username, password);
				System.out.println("Please provide your username and password...");
				System.out.println("-----Login Info-----");
				System.out.println("Email: ");
				String logEmail = input.nextLine();
				int hashE = logEmail.hashCode();
				System.out.println("Password: ");
				String logPass = input.nextLine();
				int hashP = logPass.hashCode();
				db.userExists(logEmail);
				if(db.userExists(logEmail)) {
					if(db.passExists(logPass)) {
						System.out.println("-----Logging in-----");
						System.out.println("Checking if user is Admin...");
						db.checkAdmin(logEmail);
						if(db.checkAdmin(logEmail)){
							System.out.println("User has admin privleges!");
							System.out.println("Which table would you like to edit? \n" 
									+ " 1. Inventory Table\n"
									+ " 2. Users Table\n" 
									+ " 3. Orders Table");
							int choice = input.nextInt();
							input.nextLine();
							switch (choice) {
								case 1:
									inventoryTable(db);
									System.out.println("Would you like to edit another database? (Y/N)");
									menuConfirm = input.nextLine();
									break;

								case 2:
									usersTable(db);
									System.out.println("Would you like to edit another database? (Y/N)");
									menuConfirm = input.nextLine();

									break;

								case 3:
									ordersTable(db);
									System.out.println("Would you like to edit another database? (Y/N)");
									menuConfirm = input.nextLine();
									break;
							}
						}else{
							System.out.println("User is not an admin and can only edit the Orders Table");
							System.out.println("If user would like to be assigned admin role, \n" 
												+ "user must make an appointment with local admin.");
							System.out.println("Would you like to edit the Orders Table(Y/N)?");
							String choice = input.nextLine();
							String stillChoosing = "";
							while(stillChoosing != "N"){
								if(choice.contentEquals("Y") || choice.contentEquals("y")){
									ordersTable(db);
									System.out.println("Would you like to edit another database? (Y/N)");
									menuConfirm = input.nextLine();
									break;
								}else if(choice.contentEquals("n") || choice.contentEquals("N")){
									System.out.println("Would you like to edit another database? (Y/N)");
									menuConfirm = input.nextLine();
									stillChoosing = "N";
								}else{
									System.out.println("Invalid response");
								}
							}
						}
					} else {
						System.out.println("Incorrect Password\n");
						for(int numAttempt = 2; numAttempt > 0; numAttempt--) 
						{
						System.out.println("Attempts left: "+ numAttempt);
						System.out.println("Enter Password Again: ");
						String tryAgain = input.nextLine();
						if(!(db.passExists(tryAgain))) {
							System.out.println("Incorrect Password");
						} else {
							System.out.println("Email exists in USER table");
							}
						}
					}
				} else {
					System.out.println("Email not found: Please sign up or see Admin");
					break;
				}
			} else if (csvReply.contentEquals("X") || menuConfirm.contentEquals("N")) {
				System.out.println("Exiting program...");
				break;
			} else {
				System.out.println("Invalid Response. Please enter Y or N.");
			}
		}
		if (menuConfirm.contentEquals("N") || menuConfirm.contentEquals("n")) {
			System.out.println("Exiting..");
		}
	}

	public static void inventoryTable(dataGovernance data) {
		String notDone = "";
		while (notDone != "N") {
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
				if (editOption.contentEquals("Q")) {
					System.out.println("------Importing... Please wait------");
					data.importFromCsvFile();
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
					data.insert(product_id, quantity, wholesale_cost, sale_price, supplier_id);
				} else if(editOption.contentEquals("D")) {
					System.out.println("-----------Delete Entry-----------");
					System.out.println("Enter Product ID: ");
					String product_id = input.nextLine();
					data.delete(product_id);
				} else if(editOption.contentEquals("M")) {
					System.out.println("-----------Modify Entry-----------");
					data.modify();
				} else if(editOption.contentEquals("R")) {
					System.out.println("-----------Read Entry-----------");
					data.read();
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
	
	public static void usersTable(dataGovernance data) {
		System.out.println("Would you like to: \n"+
							"1. Create a new user \n" + 
							"2. Find User Email");
		int choice = input.nextInt();
		input.nextLine();
		switch (choice) {
			case 1: 
				String adminPass = "rootUser";
				System.out.println("Please input Admin password: ");
				String pass1 = input.nextLine();
				if (pass1.contentEquals(adminPass)) {
					boolean isAdmin = false;
					System.out.println("-----New User-----");
					System.out.println("User ID: ");
					String newID = input.nextLine();
					System.out.println("Enter Email: ");
					String newEmail = input.nextLine();
					int email = newEmail.hashCode();
					System.out.println("Enter Password: ");
					String newPass = input.nextLine();
					int pass = newPass.hashCode();
					System.out.println("Is user an admin?(Y/N)");
					String isAdminChoice = input.nextLine();
					String stillChoosingAdmin = "";
					while(stillChoosingAdmin != "N"){
						if(isAdminChoice.contentEquals("Y")||isAdminChoice.contentEquals("y")){
							isAdmin = true;
							stillChoosingAdmin = "N";
						}else if(isAdminChoice.contentEquals("N") || isAdminChoice.contentEquals("n")){
							isAdmin = false;
							stillChoosingAdmin = "N";
						}else{
							System.out.println("Not a valid choice");
						}
					}
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
				System.out.println("Checking if user is Admin...");
				data.checkAdmin(logEmail);
				if(data.checkAdmin(logEmail)){
					System.out.println("User is an admin and has admin privlages!");
				}else{
					System.out.println("User is not an admim and must be assigned admin role by another admin...");
				}
			break;
		}
	}
	
	public static void ordersTable(dataGovernance data) {
		return;
	}
}
