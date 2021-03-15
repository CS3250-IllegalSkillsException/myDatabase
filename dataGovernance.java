import java.sql.*;
import java.util.Scanner;

public class dataGovernance extends DatabaseConnection{
	
	
    
    public dataGovernance(String user, String pass) {
        super(user, pass);
        // TODO Auto-generated constructor stub
    }

    public void insertUser(String user_Id, int hash_User, int hash_Pass, boolean isAdmin) {
        try {
            String sql = "INSERT INTO users (user_id, hash_User,hash_Pass, isAdmin) VALUES (?, ?, ?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user_Id);
            statement.setInt(2, hash_User);
            statement.setInt(3, hash_Pass);
            statement.setBoolean(4, isAdmin);
            // execute the remaining queries
            statement.addBatch();
            statement.executeBatch();
            connection.commit();

        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
public boolean userExists(String email) {
    	try {
    		int hash_User = email.hashCode();
        	String queryCheck = "SELECT * from users WHERE hash_User = '" + hash_User + "'";
            PreparedStatement statement = connection.prepareStatement(queryCheck);
        	ResultSet set = statement.executeQuery(queryCheck);
        	if(set.next()) {
        		int hashU = set.getInt("hash_User");
                return true;
        	}else{
               return false;
            }
    	} catch (SQLException ex) {
        ex.printStackTrace();
        	try {
        		connection.rollback();
        	} catch (SQLException e) {
        		e.printStackTrace();
        	}
    	}
		return false;
    }
    	
	    
    public boolean passExists(String password) {
    	try{
	        int hash_Pass = password.hashCode();
	        String sql = "SELECT hash_Pass FROM users WHERE hash_Pass= '" + hash_Pass + "'";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        ResultSet set = statement.executeQuery(sql);
	        if(set.next()) {
	        	int hashP = set.getInt("hash_Pass");
	        	return true;
	        } else {
	        	return false;
	        }
	    	} catch (SQLException ex) {
	        ex.printStackTrace();
	        	try {
	        		connection.rollback();
	        	} catch (SQLException e) {
	        		e.printStackTrace();
	        	}
	    	}
		return false;
	    }
}
