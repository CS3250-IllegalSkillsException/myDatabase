import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import javax.security.auth.login.LoginException;
import java.io.Console;
import java.util.ArrayList;
import java.sql.*;

public class DiscordBot extends ListenerAdapter {

    private static Connection connection;
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
        try {
            connection = DriverManager.getConnection(jdbcURL, username, new String(databasePassword));
            connection.setAutoCommit(false);
        } catch (SQLException ex){
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
                String ordersList = user.getOrders(connection);
                userChannel.sendMessage(ordersList).queue();
                break;
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
                if (user.getDiscordId() == 0){
                    userChannel.sendMessage("Someone is already logged in as that user!").queue();
                    break;
                }
                OnlineUser login = new OnlineUser(userId, email, password);
                int loginStatus = login.attemptLogin(connection);
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
