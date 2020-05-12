
import java.util.*;
import java.sql.*;

public class Purchase{

    private GeneralMethods g = new GeneralMethods();

    private boolean type; //true is debit, false is credit
    
    private double bal;
    private double amount;
    private double limit = 0;
    private int sec;
    private String expDate;
    private String cardNumber;
    private int vendorId;

    private int acctId = 0;
    

    public Purchase (Connection con, PreparedStatement s, Scanner scan){
	int exitCheck = 0;
	String dorc = "";
	while(!dorc.equalsIgnoreCase("Q")){
	    dorc = g.getString(scan, "Is the card you are using a debit or credit card? Type D for Debit, C for Credit and Q to Exit back to main menu");
	if(dorc.equalsIgnoreCase("D")){
	    this.type = true;
	    break;
	}else if(dorc.equalsIgnoreCase("C")){
	    this.type = false;
	    break;
	}else if (dorc.equalsIgnoreCase("Q")){
	    System.out.println("Exiting to main menu");
	    return;
	}else{
	    System.out.println("Incorrect Input, please try again.");
	}
	}
	exitCheck = setCardNumber(con, s, scan, "Please input your card number. Type Q to exit");
	if(exitCheck == 1){
            System.out.println("Exit made, returning to main menu");
            return;
        }

	
        exitCheck = vendorCheck(con, s, scan);
	if(exitCheck == 1){
            System.out.println("Exit made, returning to main menu");
            return;
        }

	this.amount = g.getDouble(scan, "Please enter in the amount your purchase is. This must be a POSITIVE number.");
	if(this.amount < 0){
	    System.out.println("You were not supposed to enter in a negative number. Please contact bank personnel if you would like to back-charge a purchase. Exiting back to main menu");
	    return;
	}

	Transaction t = new Transaction(con, s, this.amount);
	if(t.getTransactionid() == 0){
            System.out.println("Could not find a transaction ID. Please contact the bank personnel. Exiting to main menu");
            return;
        }
	s = g.prepareS(con, s, t.executeMainInsert());
        g.setInt(1, t.getTransactionid(), s, con);
        g.setDouble(2, t.getAmount(), s, con);
        g.executeU(con, s);
	exitCheck = executeOtherInserts(con, s, t.getTransactionid());
	if(exitCheck == 1){
	    String query = "delete from transaction where transaction_id = ?"; //transaction id
	    s = g.prepareS(con, s, query);
	    g.setInt(1, t.getTransactionid(), s, con);
	    g.executeU(con, s);
            System.out.println("Exit made, returning to main menu");
            return;
        }
	
	System.out.println("\n\nPurchase complete and recorded, returning to main menu");
    }

    public int executeOtherInserts(Connection con, PreparedStatement s, int id){
	String query = "";
	double newBal = 0;
	if(limit < amount){
	    if(type == true){
		System.out.println("Could not complete the transaction. Insufficient Funds. Exiting to main menu");
	    }else{
		System.out.println("Could not complete the transaction. Transaction would exceed the credit limit. Exiting to main menu");
	    }
	    return 1;
	}
	query = "insert into external values (?)"; //transaction id
	s = g.prepareS(con, s, query);
	g.setInt(1, id, s, con);
	g.executeU(con, s);
	query = "insert into purchase values (?,?)"; //transaction id, card number
	s = g.prepareS(con, s, query);
	g.setInt(1, id, s, con);
	g.setString(2, this.cardNumber, s, con);
	g.executeU(con, s);
	query = "insert into companies values (?, ?)";// transaction id, vendor id
	s = g.prepareS(con, s, query);
	g.setInt(1, id, s, con);
	g.setInt(2, this.vendorId, s, con);
	g.executeU(con, s);
	if(this.type == true){
	    newBal = (this.limit + this.bal) - amount;
	    query = "update bank_account set acct_balance = ? where account_id = ?"; //newbal, account_id
	    s = g.prepareS(con, s, query);
	    g.setDouble(1, newBal, s, con);
	    g.setInt(2, acctId, s, con);
	}else{
	    newBal = this.bal + amount;
	    query = "update credit_card set running_balance = ? where card_number = ?";
	    s = g.prepareS(con, s, query);
            g.setDouble(1, newBal, s, con);
            g.setString(2, this.cardNumber, s, con);
	}
	g.executeU(con, s);

	return 0;
    }

    public int vendorCheck (Connection con, PreparedStatement s, Scanner scan){
	int check = g.getInteger(scan, "Please type 1 if you know your Vendor ID, type 2 if you need to search to see if you know the Vendor ID, type 3 if you would like to create a new Vendor, and type 0 to exit");
	if(check == 0){
	    return 1;
	}else if (check == 1){
	    return setVendorId(con, s, scan, "Please input the Vendor ID your purchase is going towards. Type 0 to exit back to main menu");
	}else if(check == 2){
	    String search = g.getString(scan, "Please enter in the name of your vendor");
	    String query = "select * from vendor where vendor_name like ?"; //search
	    s = g.prepareS(con, s, query);
	    g.setString(1, search + "%", s, con);
	    ResultSet res = g.executeQ(con, s);
	    if(g.checkNext(res) == false){
		System.out.println("The following are the results from the search\n");
		do{
		    int vid = 0;
		    String vname = "";
		    try{
			vid = res.getInt("vendor_id");
			vname = res.getString("vendor_name");
		    }catch(Exception e){
			//e.printStackTrace();
			System.out.println("There was an error searching for the vendors. Please contact bank personnel. Sending you back to main menu");
			return 1;
		    }
		    System.out.println("Vendor ID: " + vid + ", Vendor Name: " + vname);
		}while(g.checkNext(res) == false);
	    }else{
		System.out.println("No results for the search found. Please make a new selection");
		return vendorCheck(con, s, scan);
	    }
	    return vendorCheck(con, s, scan);
	}else if(check == 3){
	    return newVendor(con, s, scan);
	}else{
	    System.out.println("Incorrect input. Please try again");
	    return vendorCheck(con, s, scan);
	}
    }

    public int newVendor(Connection con, PreparedStatement s, Scanner scan){
	String vendorName = g.getString(scan, "Please enter in the name of the new Vendor");
	int vid = -1;
	String query = "select MAX(vendor_id) v_id from vendor";
	s = g.prepareS(con, s, query);
        ResultSet res = g.executeQ(con, s);
	if(g.checkNext(res) == false){
	    try{
		vid = res.getInt("v_id");
	    }catch(Exception e){
		System.out.println("Error occured. Could not find the next Vendor ID value. Please contact bank personnel for help");
		return 1;
	    }
	}else{
	    System.out.println("Error occured. Could not find the next Vendor ID value. Please contact bank personnel for help");
	    return 1;
	}
        vid++;
	query = "insert into vendor values (?, ?)"; //vid, vendorName
	s = g.prepareS(con, s, query);
	g.setInt(1, vid, s, con);
	g.setString(2, vendorName, s, con);
	g.executeU(con, s);
	this.vendorId = vid;
	System.out.println("Your Vendor ID for this company is " + vid + ". Please remember this for future reference");
	return 0;
    } 
	

 
    public int checkVendorId (Connection con, PreparedStatement s, Scanner scan){
	String query = "";
        ResultSet check;
	query = "select vendor_id from vendor where vendor_id = ?"; //this.vendorId
	s = g.prepareS(con, s, query);
	g.setInt(1, this.vendorId, s, con);
	check = g.executeQ(con, s);
	if(g.checkNext(check) == true){
	    return vendorCheck(con, s, scan);
	}
	return 0;
    }



    public int checkCardNumber(Connection con, PreparedStatement s, Scanner scan){
	String query = "";
	ResultSet check;
	query = "select card_number, expiration, security_code from card where card_number = ? and trunc(expiration, 'MM') = to_date(?, 'MM/YY') and security_code = ? and active = 1"; //cardnumber, expDate, sec
	s = g.prepareS(con, s, query);
	g.setString(1, this.cardNumber, s, con);
	g.setString(2, this.expDate, s, con);
	g.setInt(3, this.sec, s, con);
	check = g.executeQ(con, s);
	if(g.checkNext(check) == true){
	    System.out.println("One of the items you entered does not match our records on file. Please try again or exit by typing Q\n");
	    return setCardNumber(con, s, scan, "Please input your card number. Type Q to exit");
	}
	if(this.type == true){
	    query = "select card_number, account_id, acct_balance, min_balance from debit natural join bank_account natural join checking where card_number = ?"; //cardnumber
	    s = g.prepareS(con, s, query);
	    g.setString(1, this.cardNumber, s, con);
	    check = g.executeQ(con, s);
	    if(g.checkNext(check) == true){
		this.type = false;
		System.out.println("Your card number was not found as a debit card but was found in our system. We have changed the staus to credit card.  Please try again or exit by Typing Q\n");
		return setCardNumber(con, s, scan, "Please input your card number. Type Q to exit");
	    }
	    try{
		this.acctId = check.getInt("account_id");
		this.bal = check.getDouble("min_balance");
		this.limit = check.getDouble("acct_balance") - this.bal;
            }catch (Exception e){
                System.out.println("Could not read data properly. Please contact bank personnel");
		return 1;
            }
	}else{
	    query = "select card_number, running_balance, limit from credit_card where card_number = ?"; //cardnumber
	    s = g.prepareS(con, s, query);
	    g.setString(1, this.cardNumber, s, con);
	    check = g.executeQ(con, s);
	    if(g.checkNext(check) == true){
		this.type = true;
		System.out.println("Your card number was not found as a credit card but was found in our system. We have changed your status to a debit card.  Please try again or exit by typing Q\n");
		return setCardNumber(con, s, scan, "Please input your card number. Type Q to exit");
	    }
	    try{
		this.bal = check.getDouble("running_balance");
		this.limit = check.getDouble("limit") - this.bal;
	    }catch(Exception e){
		System.out.println("Could not read data properly. Please contact bank personnel");
		return 1;
	    }
	}
	return 0;
    }



    public int setVendorId (Connection con, PreparedStatement s, Scanner scan, String input){
	this.vendorId = g.getInteger(scan, input);
	if(this.vendorId == 0){
	    return 1;
	}
	return checkVendorId(con, s, scan);
    }

    public int setCardNumber(Connection con, PreparedStatement s, Scanner scan, String input){
	this.cardNumber = g.getString(scan, input);
	if(this.cardNumber.length() != 16 && !this.cardNumber.equalsIgnoreCase("Q")){
	    System.out.println("The card number you entered was incorrect formatting for a card number. Please try again\n");
            return setCardNumber(con, s, scan, "Please input your card number. Type Q to exit");
        }else if(this.cardNumber.equalsIgnoreCase("Q")){
            return 1;
        }
	String exp = "";
	while(exp.length() < 4 || exp.length() > 5){
	    exp = g.getString(scan, "Please enter in the expiraton date in the format of MM/YY");
	}
	this.expDate = exp;
	int code =  g.getInteger(scan, "Please enter in your 3 digit security code");
	while(code < 100 || code > 999){
	    code = g.getInteger(scan, "Please enter in your 3 digit security code");
	} 
	this.sec = code;
	return checkCardNumber(con, s, scan);
    }
       

}