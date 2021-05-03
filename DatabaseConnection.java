import java.util.*;
import java.sql.*;
import java.io.*;

public class DatabaseConnection {
    protected String jdbcURL = "jdbc:mysql://localhost:3306/test";
    protected String username;
    protected String password;
    protected String csvFilePath = "inventory_team3.csv";
    protected String customerOrderCsv = "customer_orders_team3.csv";
    protected Connection connection;

        public DatabaseConnection(String user, String pass){
            username = user;
            password = pass;
            initializeConnection();
        }

        //non-default constructor for cases where we are accessing a different database.
        public DatabaseConnection(String user, String pass, String url){
            username = user;
            password = pass;
            jdbcURL = url;
            initializeConnection();
        }

        public void setUsername(String user){
            username = user;
        }

        public String getUsername(){
            return username;
        }

        public void setPassword(String pass){
            password = pass;
        }

        public String getPassword(){
            return password;
        }

        public void setJdbcUrl(String url){
            jdbcURL = url;
        }

        public String getJdbcUrl(){
            return jdbcURL;
        }

        public Connection getConnection() { return connection; }

        public void setCsvFilePath(String filepath){
            csvFilePath = filepath;
        }

        public String getCsvFilePath(){
            return csvFilePath;
        }

        public void closeConnection(){
            try{
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    public Connection initializeConnection(){
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);
        } catch (SQLException ex){
            //Error handling for invalid login credentials
            if (ex.getErrorCode() == 1045){
                //Prompt user for login information again
                System.out.println("Wrong Username and Password! Please try again");
                Scanner input = new Scanner(System.in);
                Console console = System.console();
				System.out.println("Username: ");
				username = input.nextLine();
				char[] pwd = console.readPassword("Password: ");
				password = new String(pwd);
                //Call initializeConnection method again
                initializeConnection();
            }
            else {
                ex.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return connection;
            }
        }
        return connection;
    }
}
