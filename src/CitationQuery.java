import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CitationQuery {
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
            pst = con.prepareStatement("SELECT sourcePaperId, targetPaperId "
            		+ "FROM Citations");
            rs = pst.executeQuery();
            while (rs.next()) {
            	String data = rs.getInt(1)+" "+rs.getInt(2)+"\n";
                write(data);
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
	
	
	
	public static void write(String data){
    	try{ 
            //creating file object from given path
    		File file = new File("/Users/chaoran/Desktop/Citations.edges");
    		System.out.println(data);
    		//if file doesnt exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    		}

            //FileWriter second argument is for append if its true than FileWritter will
            //write bytes at the end of File (append) rather than beginning of file
            FileWriter fileWriter = new FileWriter(file,true);
          
            //Use BufferedWriter instead of FileWriter for better performance
            BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);
            fileWriter.append(data);
          
            //Don't forget to close Streams or Reader to free FileDescriptor associated with it
            bufferFileWriter.close();

    	}catch(IOException e){
    		e.printStackTrace();
    	}
	
			
	}
}
