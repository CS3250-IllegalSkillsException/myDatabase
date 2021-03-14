import java.util.*;
import java.util.Scanner;
import javax.mail.*;
import javax.activation.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.*;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class CustomerOrders {
    private static String email;
    private static String password;
    private static String shippingAddress;
    private static String productID;
    private static int quantity;

    public String getEmail(){
        return email;
    }

    public String getShippingAddress(){
        return shippingAddress;
    }

    public String getProductID(){
        return productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public static void getCustomerOrders(String recepient) throws MessagingException{
        Scanner input = new Scanner(System.in);
        System.out.println("Email address:");
        email = input.nextLine();                  //illegalskillsexceptiont3@gmail.com
        System.out.println("Password:");
        password = input.nextLine();               //ro0t1234
        System.out.println("Shipping address:");
        shippingAddress = input.nextLine();
        System.out.println("Product ID:");
        productID = input.nextLine();
        System.out.println("Quantity:");
        quantity = input.nextInt();

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        Message message = prepareMessage(session, email, recepient);

        Transport.send(message);
        System.out.println("Order sent successfully....");

        System.out.println("Thank you for placing your order! Press 1 to order again or 2 to exit");
        int orderAgain = input.nextInt();
        if(orderAgain == 1){
            getCustomerOrders(recepient);
        }
        else{
            System.out.print("Thank you for shopping! See you next time!");
        }
    }

    private static Message prepareMessage(Session session, String email, String recepient){
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
            message.setSubject("New order");
            message.setText("Shipping Address: " + shippingAddress  + " Product ID: " + productID + " Quantity: "+ quantity);
            return message;
        } catch (Exception ex) {
            Logger.getLogger(CustomerOrders.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static void main(String[] args) throws MessagingException{
        getCustomerOrders("illegalskillsexceptiont3@gmail.com");
    }
}
