import java.sql.*;
import java.util.Random;
import java.util.Arrays;

import com.mysql.cj.protocol.Resultset;

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
    public String getRecommend(Connection connection){
        String sqlQuery3 = "SELECT cust_email, cust_location, product_id, product_quantity FROM orders WHERE cust_email = '" + email + "'";
        String output = "";
        try{
            PreparedStatement statement = connection.prepareStatement(sqlQuery3);
            ResultSet verify = statement.executeQuery(sqlQuery3);
            // Check if customer has any orders
            if(verify.next()){
                // Check if there are any other orders in the same location
                int zipcode = verify.getInt("cust_location");
                String sqlQuery4 = "SELECT product_id, product_quantity FROM orders WHERE cust_location = " + zipcode +  " AND cust_email != '" + email + "'";
                PreparedStatement statement2 = connection.prepareStatement(sqlQuery4, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet catalog = statement2.executeQuery(sqlQuery4);
                if(catalog.next()){
                    // Recommend random product from other users in the same location
                    String product_id = getRandomProduct(catalog, connection);
                    if (product_id.contentEquals("Error.")){
                        output = "Error.";
                    } else {
                    output = "Here's a product purchased in your area!\nProduct ID: " + product_id;
                    }
                } else {
                    // Get nearest 10 orders closest to customer zipcode
                    String sqlQuery5 = "SELECT product_id, product_quantity FROM orders WHERE cust_location != " + zipcode + " ORDER BY ABS(cust_location - " + zipcode + ") LIMIT 10";
                    PreparedStatement statement3 = connection.prepareStatement(sqlQuery5, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet neighbor = statement3.executeQuery(sqlQuery5);
                    
                    String product_id = getRandomProduct(neighbor, connection);
                    if (product_id.contentEquals("Error.")){
                        output = "Error.";
                    } else {
                    output = "Here's a product purchased near your area!\nProduct ID: " + product_id;
                    }
                }
            } else {
                // find random product to get started
                String sqlQuery4 = "SELECT * FROM inventory";
                PreparedStatement statement2 = connection.prepareStatement(sqlQuery4, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet catalog = statement2.executeQuery(sqlQuery4);

                String product_id = getRandomProduct(catalog, connection);
                if (product_id.contentEquals("Error.")){
                    output = "Error.";
                } else {
                    output = "Looks like you haven't made a purchase yet. Here's something to get you started!\nProduct ID: " + product_id;
                }
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

    public String getUnder20(Connection connection, int page){
        String sqlQuery = "SELECT * FROM inventory WHERE sale_price <= 20";
        String output = "Products Under $20 - Page " + page + ":\n\n";
        try{
            PreparedStatement statement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet catalog = statement.executeQuery(sqlQuery);

            String text;
            String product_id;
            double sale_price;
            int upper = page * 10;
            int lower = upper - 10;
            for (int i = lower; i < upper; i++){
                catalog.absolute(i+1);
                product_id = catalog.getString("product_id");
                sale_price = catalog.getDouble("sale_price");
                text = String.format("%-18s%-15s\n\n", product_id, sale_price);
                output += text;
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

    public String getRandomProduct (ResultSet catalog, Connection connection){
        try{
            catalog.last();
            int size = catalog.getRow();
            Random rand = new Random();
            int int_random = rand.nextInt(size) + 1;
            catalog.absolute(int_random);
            String product_id = catalog.getString("product_id");
            return product_id;
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
