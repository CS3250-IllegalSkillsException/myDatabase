import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import javax.security.auth.login.LoginException;
import java.io.Console;
import java.util.ArrayList;

public class DiscordBot extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {
        //NOTE: NEVER EVER DIRECTLY INPUT THE TOKEN INTO THE CODE. SOMEONE BROWSING GITHUB **WILL** FIND IT.
        //YOU DO NOT WANT THAT. THEY WILL MAKE YOUR BOT DO THINGS YOU NEVER WANTED IT TO.
        System.out.println("Please input the token: ");
        //We are dealing with actual sensitive data that we might have to present. As such I am using the console class.
        //It will look like you are pasting nothing into the console, but you really are.
        //Also this means that you can only run this from the command line.
        Console cons = System.console();
        System.out.println(cons);
        char[] inputToken = cons.readPassword();
        ArrayList<GatewayIntent> intentions = new ArrayList<>();
        intentions.add(GatewayIntent.DIRECT_MESSAGES);
        JDABuilder builder = JDABuilder.create(new String(inputToken), intentions);
        builder.addEventListeners(new DiscordBot());
        builder.build();
        java.util.Arrays.fill(inputToken, ' '); //Minimizes the amount of time the token is in memory.
    }

@Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event){
        System.out.println(event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());
    }
}
