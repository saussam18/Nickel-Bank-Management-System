import java.sql.*;
import java.util.*;

public class delete {
    public static void main(String[] args) {
        try(
            Connection con=DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", "sps222", "MSFTstockup777^");
            Statement s=con.createStatement();
            ) {
		String query;
                System.out.println("Connection successfully made");
		query = "delete from customer";
		s.executeQuery(query);
		query ="delete from bank_account";
		s.executeQuery(query);
		query ="delete from transaction";
		s.executeQuery(query);
		query ="delete from card";
		s.executeQuery(query);
		query ="delete from loan";
		s.executeQuery(query);
		s.close();
		con.close();
		System.out.println("Connection close");
	    }catch(Exception e){
	    e.printStackTrace();
	}
    }
}