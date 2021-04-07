import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Users extends Database{

    public Users(Database db){
        super(db.getUsername(),db.getPassword());
    }

    private final DataGovernance hashing = new DataGovernance();

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
            int hash_User = hashing.getHash(email);
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
            int hash_Pass = hashing.getHash(password);
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

    public void exportUsersCSV() {
        String home = System.getProperty("user.home");
        try {
            PrintWriter pw = new PrintWriter(new File(home + "\\Downloads\\Users.csv"));
            StringBuilder sb = new StringBuilder();
            ResultSet rs = null;
            sb.append("user_id");
            sb.append(",");
            sb.append("hash_User");
            sb.append(",");
            sb.append("hash_Pass");
            sb.append(",");
            sb.append("isAdmin");
            sb.append("\n");

            String query = "select * from users";
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                sb.append(rs.getString("user_id"));
                sb.append(",");
                sb.append(rs.getString("hash_User"));
                sb.append(",");
                sb.append(rs.getString("hash_Pass"));
                sb.append(",");
                sb.append(rs.getString("isAdmin"));
                sb.append("\r\n");
            }

            pw.write(sb.toString());
            pw.close();
            System.out.println("Export complete. Find Users.csv in your Downloads folder.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
