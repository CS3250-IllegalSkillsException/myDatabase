import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import javax.security.auth.login.LoginException;
import java.io.Console;
import java.util.ArrayList;
import java.sql.*;

public class DiscordBot extends ListenerAdapter {

    private static Orders db;
    private ArrayList<OnlineUser> users = new ArrayList<>();

    public static void main(String[] args) throws LoginException {
        Console cons = System.console();
        String jdbcURL = "jdbc:mysql://localhost:3306/test";
        //get login for database
        System.out.println("Please provide login info");
        System.out.println("Username: ");
        String username = cons.readLine();
        System.out.println("Password: ");
        char[] databasePassword = cons.readPassword();
        db = new Orders(username,new String(databasePassword));
        //NOTE: NEVER EVER DIRECTLY INPUT THE TOKEN INTO THE CODE. SOMEONE BROWSING GITHUB **WILL** FIND IT.
        //YOU DO NOT WANT THAT. THEY WILL MAKE YOUR BOT DO THINGS YOU NEVER WANTED IT TO.
        System.out.println("Please input the token: ");
        //We are dealing with actual sensitive data that we might have to present. As such I am using the console class.
        //It will look like you are pasting nothing into the console, but you really are.
        //Also this means that you can only run this from the command line.
        char[] inputToken = cons.readPassword();
        ArrayList<GatewayIntent> intentions = new ArrayList<>();
        intentions.add(GatewayIntent.DIRECT_MESSAGES);
        JDABuilder builder = JDABuilder.create(new String(inputToken), intentions);
        builder.addEventListeners(new DiscordBot());
        builder.build();
        java.util.Arrays.fill(inputToken, ' '); //Minimizes the amount of time the token is in memory.
        java.util.Arrays.fill(databasePassword, ' ');

    }

    private OnlineUser findUser(long userId){
        for (OnlineUser user : users) {
            if (userId == user.getDiscordId()) {
                return user;
            }
        }
        return new OnlineUser(0,"","");
    }

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
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);
                if (user.getDiscordId() == 0){
                    userChannel.sendMessage("You are not logged in! Use !login").queue();
                    break;
                }
                String ordersList = "```" + db.getOrders(user.getEmail()) + "```";
                userChannel.sendMessage(ordersList).queue();
                break;
            }
            case "!recommend": {
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);
                if (user.getDiscordId() == 0){
                    userChannel.sendMessage("You are not logged in! Use !login").queue();
                    break;
                }
                String productRecommend = db.getRecommend(user.getEmail());
                userChannel.sendMessage(productRecommend).queue();
                break;
            }
            case "!under20": {
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);
                if (user.getDiscordId() == 0){
                    userChannel.sendMessage("You are not logged in! Use !login").queue();
                    break;
                }
                if (parameters.length == 2) {
                    int page = Integer.parseInt(parameters[1]);
                    String under20 = "```" + db.getUnder20(page) + "```";
                    userChannel.sendMessage(under20).queue();
                    break;
                } else {
                    userChannel.sendMessage("Usage: !under20 (page number)").queue();
                    break;
                }
            }
            case "!login": {
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);
                if (user.getDiscordId() != 0){
                    userChannel.sendMessage("You are already logged in! Use !logout first.").queue();
                    break;
                }
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
                user = findUserByEmail(email);
                if (user.getDiscordId() != 0){
                    userChannel.sendMessage("Someone is already logged in as that user!").queue();
                    break;
                }
                OnlineUser login = new OnlineUser(userId, email, password);
                int loginStatus = login.attemptLogin(db.getConnection());
                switch (loginStatus) {
                    case 1:
                        userChannel.sendMessage("Login successful for " + email).queue();
                        users.add(login);
                        break;
                    case 2:
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
                long userId = event.getAuthor().getIdLong();
                OnlineUser user = findUser(userId);
                if (user.getDiscordId() == 0){
                    userChannel.sendMessage("You are not logged in! Use !login").queue();
                    break;
                }
                users.remove(user);
                userChannel.sendMessage("You have been successfully logged out.").queue();
                break;
        }
    }
}
