import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import javax.security.auth.login.LoginException;
import java.io.Console;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.sql.SQLException;

public class DiscordBot extends ListenerAdapter {

    private static Orders db;
    private static CustomerReplyandCancel crp;
    private ArrayList<OnlineUser> users = new ArrayList<>();

    public static void main(String[] args) throws LoginException {
        //starting the discord bot requires database login information and the token of the discord bot.
        Console cons = System.console();
        String jdbcURL = "jdbc:mysql://localhost:3306/test";
        //get login for database
        System.out.println("Please provide login info");
        if (cons != null) {
            System.out.println("Username: ");
            String username = cons.readLine();
            System.out.println("Password: ");
            char[] databasePassword = cons.readPassword();
            db = new Orders(username, new String(databasePassword));
            crp = new CustomerReplyandCancel(username, new String(databasePassword));
            //NOTE: NEVER EVER DIRECTLY INPUT THE TOKEN INTO THE CODE. SOMEONE BROWSING GITHUB **WILL** FIND IT.
            //YOU DO NOT WANT THAT. THEY WILL MAKE YOUR BOT DO THINGS YOU NEVER WANTED IT TO.
            System.out.println("Please input the token: ");
            //We are dealing with actual sensitive data that we might have to present. As such I am using the console class.
            //It will look like you are pasting nothing into the console, but you really are.
            char[] inputToken = cons.readPassword();
            ArrayList<GatewayIntent> intentions = new ArrayList<>();
            intentions.add(GatewayIntent.DIRECT_MESSAGES);
            JDABuilder builder = JDABuilder.create(new String(inputToken), intentions);
            builder.addEventListeners(new DiscordBot());
            builder.build();
            java.util.Arrays.fill(inputToken, ' '); //Helps to minimize the amount of time the token is in memory.
            java.util.Arrays.fill(databasePassword, ' ');
        } else {
            Scanner input = new Scanner(System.in);
            System.out.println("Username: ");
            String username = input.nextLine();
            System.out.println("Password: ");
            String password = input.nextLine();
            System.out.println("Please input the token: ");
            String token = input.nextLine();
            db = new Orders(username, password);
            crp = new CustomerReplyandCancel(username, password);
            ArrayList<GatewayIntent> intentions = new ArrayList<>();
            intentions.add(GatewayIntent.DIRECT_MESSAGES);
            JDABuilder builder = JDABuilder.create(token, intentions);
            builder.addEventListeners(new DiscordBot());
            builder.build();
        }
    }

    //Finds the OnlineUser object associated with the userId.
    private OnlineUser findUser(long userId){
        for (OnlineUser user : users) {
            if (userId == user.getDiscordId()) {
                return user;
            }
        }
        return new OnlineUser(0,"","");
    }

    //Finds the OnlineUser object associated with the email address.
    private OnlineUser findUserByEmail(String email){
        for (OnlineUser user : users) {
            if (email.equals(user.getEmail())) {
                return user;
            }
        }
        return new OnlineUser(0,"","");
    }

@Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event){
        System.out.println(event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());
        String command = event.getMessage().getContentDisplay();
        PrivateChannel userChannel = event.getChannel();
        String[] parameters = command.split(" ");
        switch (parameters[0]) {
            case "!orders": {
                //first, find the OnlineUser class associated with the user who sent this message.
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);
                if (user.getDiscordId() == 0){
                    userChannel.sendMessage("You are not logged in! Use !login").queue();
                    break;
                }
                //now, get the list of orders this user made from the database and output it.
                String ordersList = "```" + db.getOrders(user.getEmail()) + "```";
                userChannel.sendMessage(ordersList).queue();
                break;
            }
            case "!newOrder": {
                //Check if there's the appropriate number of arguments.
                if (parameters.length != 4) {
                    userChannel.sendMessage("Usage: !newOrder (location) (product_id) (quantity)").queue();
                    break;
                }
                //Gets location, product, and amount from customer
                String location = parameters[1];
                String product_id = parameters[2];
                String quantity = parameters[3];


                //Gets current date
                SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = temp.format(new Date());

                //Gets subtotal of products
                try {
                    Double subtotal = db.getSubtotal(product_id, Integer.parseInt(quantity));
                }catch (SQLException e){
                    e.printStackTrace();
                }

                //Get the user's object.
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);

                //Checks to see if user is logged in
                if (user.getDiscordId() == 0){
                    userChannel.sendMessage("You are not logged in! Use !login").queue();
                    break;
                }

                //Fetches user's email
                String email = user.getEmail();
                db.insertOrders(date, email, location, product_id, quantity);

                //Sends order confirmation to customer email
                crp.customerConfirm(email, date, product_id, quantity, location);
                //Generates a recommendation for the customer and sends the email
                String productRecommend = db.RemarketRecommend(product_id, email);
                crp.customerRecommend(email, productRecommend);
                userChannel.sendMessage("Thank you for your order!").queue();
                break;
            }
            case "!cancelOrder": {
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);

                //Checks to see if user is logged in
                if (user.getDiscordId() == 0){
                    userChannel.sendMessage("You are not logged in! Use !login").queue();
                    break;
                }

                //check for appropriate arguments
                if (parameters.length != 2) {
                    userChannel.sendMessage("Usage: !cancelOrder (orderID)").queue();
                    userChannel.sendMessage("Hint: Use !orders to find the ids for your orders.").queue();
                    break;
                }

                //Gets customer email, orderID, and date
                String email = user.getEmail();
                String orderID = parameters[1];
                SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = db.getOrderDate(email,orderID);

                //Cancels customer order if within cancel period time
                try{
                     if(crp.withinCancellatioWindow(date)){
                         crp.customerCancel(email, orderID, date);
                         db.delete("orders", "order_id", orderID);
                         userChannel.sendMessage("Your order has been successfully cancelled!").queue();
                     }
                     else{
                        userChannel.sendMessage("This order can no longer be cancelled.").queue();
                    }
                }catch(ParseException e) {
                    e.printStackTrace();
                }
                break;
            }
            //Handles Recommend functionality

            case "!recommend": {
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);
                //Check if user is logged in
                if (user.getDiscordId() == 0){
                    userChannel.sendMessage("You are not logged in! Use !login").queue();
                    break;
                }
                //Run getRecommend method and output recommended product id.
                String productRecommend = "Here's a recommended product for you!\nProduct ID: " + db.getRecommend(user.getEmail());
                userChannel.sendMessage(productRecommend).queue();
                break;
            }
            case "!under20": {
                //Handles functionality for finding products under $20
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);
                //Check if user is logged in
                if (user.getDiscordId() == 0){
                    userChannel.sendMessage("You are not logged in! Use !login").queue();
                    break;
                }
                //Check format of command
                if (parameters.length == 2) {
                    //Get page number from command input
                    int page = Integer.parseInt(parameters[1]);
                    //Execute getUnder20 method
                    String under20 = "```" + db.getUnder20(page) + "```";
                    userChannel.sendMessage(under20).queue();
                    break;
                } else {
                    userChannel.sendMessage("Usage: !under20 (page number)").queue();
                    break;
                }
            }
            case "!login": {
                //Check to see if the user is already logged in.
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);
                if (user.getDiscordId() != 0){
                    userChannel.sendMessage("You are already logged in! Use !logout first.").queue();
                    break;
                }

                //Get the email and password from the user.
                String email = "";
                String password = "";
                if (parameters.length == 3) {
                    email = parameters[1];
                    password = parameters[2];
                } else if (parameters.length == 2) {
                    email = parameters[1];
                    password = "";
                } else {
                    userChannel.sendMessage("Usage: !login (username) (password)").queue();
                    break;
                }

                //Check to see if that user is already in the system.
                user = findUserByEmail(email);
                if (user.getDiscordId() != 0){
                    userChannel.sendMessage("Someone is already logged in as that user!").queue();
                    break;
                }

                //Attempt to log in as the user.
                OnlineUser login = new OnlineUser(userId, email, password);
                int loginStatus = login.attemptLogin(db.getConnection());
                switch (loginStatus) {
                    case 1:
                        userChannel.sendMessage("Login successful for " + email).queue();
                        users.add(login);
                        break;
                    case 2:
                        //This occurs for new users and simulated users.
                        userChannel.sendMessage("Login successful for " + email).queue();
                        userChannel.sendMessage("WARNING: No password set for your login! Contact admin.").queue();
                        users.add(login);
                        break;
                    case 3:
                        userChannel.sendMessage("Incorrect password.").queue();
                        break;
                    case 4:
                        userChannel.sendMessage("A MySQL error has occurred. Please try again later.").queue();
                        break;
                }
                break;
            }
            case "!logout":
                //check if the user is logged in
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);
                if (user.getDiscordId() == 0){
                    userChannel.sendMessage("You are not logged in! Use !login").queue();
                    break;
                }
                //if they are, remove that user to log them out.
                users.remove(user);
                userChannel.sendMessage("You have been successfully logged out.").queue();
                break;
        }
    }
}
