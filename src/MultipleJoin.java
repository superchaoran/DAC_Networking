import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MultipleJoin {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        String hostname = "dacnetworkanalysis.c8kxvfryaahi.us-west-2.rds.amazonaws.com";
        String port = "3306";
        String dbName = "DACNetworkAnalysis";
        String userName = "ebroot";
        String password = "citations1";
        
        Class.forName("com.mysql.jdbc.Driver").newInstance(); // register driver.
        String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
        try {
            con = DriverManager.getConnection(jdbcUrl);
            pst = con.prepareStatement("SELECT * FROM Authors");
            rs = pst.executeQuery();

            while (rs.next()) {
                System.out.print(rs.getInt(1));
                System.out.print(": ");
                System.out.println(rs.getString(2));
            }

        } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Multiple.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Multiple.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
}
