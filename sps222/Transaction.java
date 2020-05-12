
import java.util.*;
import java.sql.*;

public class Transaction {
    private GeneralMethods g = new GeneralMethods();
    
    private int transactionid;
    private double amount;

    public Transaction(Connection con, PreparedStatement s, double amo){
	this.transactionid = findTransactionid(con, s);
	this.amount = amo;
    }
    
    public int findTransactionid(Connection con, PreparedStatement s){
	String query = "select MAX(Transaction_id) T_id from Transaction";
	s = g.prepareS(con, s, query);
	ResultSet res = g.executeQ(con, s);
	int id = -1;
	if(g.checkNext(res) == true){
	    return id;
	}
	try{
	id = res.getInt("T_id");
	}catch (Exception e){
	    return id;
	}
	id++;
	return id;
    }
    
    public int getTransactionid (){
	return this.transactionid;
    }

    public double getAmount(){
	return this.amount;
    }

    public String executeMainInsert(){
	return "insert into Transaction values (?, ?, CURRENT_TIMESTAMP)"; //transaction id, amount
    }

   
}

