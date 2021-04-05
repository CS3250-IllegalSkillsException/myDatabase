import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class CustomerReplyandCancel {
	private String jdbcURL = "jdbc:mysql://localhost:3306/test";
    private String username;
    private String password;
    private Connection connection;
	
	public CustomerReplyandCancel(String user, String pass){
        username = user;
        password = pass;
        initializeConnection();
    }
	
	public boolean initializeConnection(){
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);
        } catch (SQLException ex){
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
    
	public void sendEmail(String email, String subject, String body) {
		String sender = "illegalskillsexception3250@gmail.com";
		String recipient = email;
		String password = ""; //need to hide password
		
		Properties prop = System.getProperties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.socketFactory.port", "465");
		
		prop.put("mail.smtp.socketFactory.class",    
                "javax.net.ssl.SSLSocketFactory");    
		prop.put("mail.smtp.auth", "true");    
		prop.put("mail.smtp.port", "465");   
		
		Session session = Session.getDefaultInstance(prop,
		
		new javax.mail.Authenticator() {    
	    protected PasswordAuthentication getPasswordAuthentication() {
	    	return new PasswordAuthentication(sender,password);
	    }
		});
	           
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			message.setSubject(subject);
			message.setText(body);
			Transport.send(message);
			System.out.println("Email Sent!");
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	
	public void customerConfirm(String cust_email, String date, String product_id, String quantity, String cust_location) {
		//produce confirmation email when order is made
		String body = "Thanks you for your order!\n\n"
				+ "Order Details\n"
				+ "----------------------\n"
				+ "Order place on: " + date + "\n"
				+ "Product ID: " + product_id + "\n"
				+ "Order Quanitity: " + quantity + "\n"
				+ "ZIP code of shipping address: " + cust_location + "\n"
				+ "All orders may be cancelled within 24 hours";
		sendEmail(cust_email, "Order Confirmation", body);
	}
	
	public void customerCancel(String cust_email, String orderID,String date) {
		//produce cancellation email
		String body = "Your order has been successfully cancelled!\n"
				+ "--------------------------------------------\n"
				+ "Cancellation of order: " + orderID + "\n"
				+ "Order cancelled on: " + date + "\n\n"
				+ "For any questions, please contact customer service.\n"
				+ "";
		sendEmail(cust_email, "Order Cancellation", body);
	}

	public boolean withinCancellatioWindow(String orderDate) throws ParseException  {
		//check if timeStamp is within cancel time
		SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currDate = temp.format(new Date());
		
		Date date1 = temp.parse(currDate);
		Date date2 = temp.parse(orderDate);
		long diff = date1.getTime() - date2.getTime();
		if(diff <= 86400000 ) {
			return true;
		} else {
			return false;
		}
	}
	
	public String findDate(String order_id) throws SQLException {
		PreparedStatement preparedStatement;
		String orderDate = "SELECT date FROM orders WHERE order_id= '"
		+ order_id + "'";
		PreparedStatement statement = connection.prepareStatement(orderDate);
		ResultSet set = statement.executeQuery(orderDate);
		if(set.next()) {
			String date = set.getString("date");
			return date;
		} 
		return "error";
	}
	
}
