import java.sql.*;

public class OnlineUser {
    private long discordId;
    private String email;
    private String password;
    private boolean loggedIn;
    public OnlineUser(long id, String em, String pw){
        discordId = id;
        email = em;
        password = pw;
    }
    public void setDiscordId(long id){
        discordId = id;
    }
    public long getDiscordId(){
        return discordId;
    }
    public void setEmail(String em){
        email = em;
    }
    public String getEmail(){
        return email;
    }
    public void setPassword(String pw){
        password = pw;
    }
    public String getPassword(){
        return password;
    }
    public boolean checkLoggedIn(){
        return loggedIn;
    }
    //returns 1 on success, 2 on default success, and 3 on login failure, 4 on error.
    public int attemptLogin(Connection connection){
        try {
            int hash_User = email.hashCode();
            String queryCheck = "SELECT * from users WHERE hash_User = '" + hash_User + "'";
            PreparedStatement statement = connection.prepareStatement(queryCheck);
            ResultSet set = statement.executeQuery(queryCheck);
            if(set.next()) {
                int hashU = set.getInt("hash_User");
                int hashP = set.getInt("hash_Pass");
                if(password.hashCode() == hashP){
                    System.out.println("Login success for email: " + email);
                    loggedIn = true;
                    return 1;
                } else {
                    System.out.println("Login failure: " + email);
                    loggedIn = false;
                    return 3;
                }
            }else{
                System.out.println("User for email does not exist: " + email);
                System.out.println("Login was permitted due to no password.");
                loggedIn = true;
                return 2;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 4;
    }
    public String getOrders(Connection connection){
        //Note that we currently use plaintext emails in the orders database. This code will be changed when we switch to hashed ones.
        String sqlQuery2 = "SELECT date, cust_email, cust_location, product_id, product_quantity FROM orders WHERE"
                + " cust_email = '" + email + "'";
        String output = "Orders:\n";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery2);
            ResultSet results = statement.executeQuery(sqlQuery2);
            while (results.next()) {
                String date = results.getString("date");
                String cust_email = results.getString("cust_email");
                int cust_location = results.getInt("cust_location");
                String product_id = results.getString("product_id");
                int product_quantity = results.getInt("product_quantity");
                //System.out.printf("%-15s%-20s%-15s%-20s%-15s\n", date, cust_email, cust_location, product_id, product_quantity);
                /*String text = "Date: " + date + " Email: " + cust_email + " Location: " + cust_location+ " Product: "
                        + product_id + " Quantity: " + product_quantity;*/
                String text = String.format("%-15s%-20s%-15s%-20s%-15s\n", date, cust_email, cust_location, product_id, product_quantity);
                output = output + text + "\n";
            }
            return output;
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "Error.";
    }
}
