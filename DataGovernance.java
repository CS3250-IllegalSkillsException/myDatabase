import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataGovernance {

    public DataGovernance() {

    }

    //Create a hash for the email
    public int getHash(String inputData, Connection connection){
        int hash = inputData.hashCode();
        try {
            //If the hash already exists in the emails table, lets just get it from there.
            String sql = "SELECT unhashed_email FROM emails WHERE hashed_email = '" + hash + "'";
            PreparedStatement statement2 = connection.prepareStatement(sql);
            ResultSet results = statement2.executeQuery(sql);
            if (results.next()){
                return hash;
            }
            //If the hash is not in the emails table, lets add it to it.
            sql = "INSERT INTO emails (unhashed_email, hashed_email) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, inputData);
            statement.setString(2, ""+hash);
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
    	return hash;
	}

	//Find the plaintext associated with the email.
	public String unHash(int hash, Connection connection){
        String sqlQuery = "SELECT unhashed_email FROM emails WHERE"
                + " hashed_email = '" + hash + "'";
        try {
            //Find the email associated with the hash in the emails table and return it.
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet results = statement.executeQuery(sqlQuery);
            results.next();
            return results.getString("unhashed_email");
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //If it's not in the table, return an error.
        return "Error";
    }

    //If unhash is called with a string instead of an integer, parse the string as an integer before passing it.
    public String unHash(String hash, Connection connection){
        return unHash(Integer.parseInt(hash),connection);
    }

}
