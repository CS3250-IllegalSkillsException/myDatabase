import java.util.*;
import java.sql.*;
import java.io.*;

public class dataBaseConnection {
    protected String jdbcURL = "jdbc:mysql://localhost:3306/test";
    protected String username;
    protected String password;
    protected String csvFilePath = "inventory_team5.csv";
    protected Connection connection;

        public dataBaseConnection(String user, String pass){
            username = user;
            password = pass;
            initializeConnection();
        }

        //non-default constructor for cases where we are accessing a different database.
        public dataBaseConnection(String user, String pass, String url){
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
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return connection;
        }
        return connection;
    }
}
