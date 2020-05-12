
import java.sql.*;
import java.util.*;

public class AccountDorW {
    private GeneralMethods g = new GeneralMethods();
    
    private int userid;
    private int accountid;
    private double minBal = 0;
    private int locationid;
    private double currAcctBal;

    private double amount;
    private boolean dorw; //true means deposit, false means withdrawal
    private boolean sorc; //true means checking, false means savings
    private boolean loc; //true means teller, false means ATM 

    public AccountDorW (Connection con, PreparedStatement s, Scanner scan){
	int exitCheck = 0;
	String choice1 = "";
	String choice2 = "";
	while(!choice1.equalsIgnoreCase("Q")){
	    choice1 = g.getString(scan, "Is the Transaction a Deposit or a Withdrawal? Type D for deposit, Type W for withdrawal and Type Q to exit back to main menu");
	if(choice1.equalsIgnoreCase("D")){
	    this.dorw = true;
	    loc = true;
	    break;
	}else if (choice1.equalsIgnoreCase("W")){
	    this.dorw = false;
	    while(!choice1.equalsIgnoreCase("Q")){
	    choice1 = g.getString(scan, "Is the Withdrawal occuring at an ATM or a Teller? Type A for ATM, T for Teller and Q to exit back to main menu");
	    if(choice1.equalsIgnoreCase("A")){
		this.loc = false;
		break;
	    }else if (choice1.equalsIgnoreCase("T")){
		this.loc = true;
		break;
	    }else if (choice1.equalsIgnoreCase("Q")){
		System.out.println("Exiting back to main menu");
		return;
	    }else{
		System.out.println("Incorrect input, please try again");
	    }
	    }
	    break;
	}else if(choice1.equalsIgnoreCase("Q")){
	    System.out.println("Exiting back to main menu");
	    return;
	}else{
	    System.out.println("Incorrect input, please try again");
	}
	}
	
	while(!choice2.equalsIgnoreCase("Q")){
	    choice2 = g.getString(scan, "Is the account for your Transaction a checking or savings account? Type C for checking, S for savings and Q to exit back to main menu");
	if(choice2.equalsIgnoreCase("C")){
	    this.sorc = true;
	    break;
        }else if (choice2.equalsIgnoreCase("S")){
	    this.sorc = false;
	    break;
        }else if(choice2.equalsIgnoreCase("Q")){
            System.out.println("Exiting to main menu");
            return;
        }else{
	    System.out.println("Incorrect input, please try again");
	}
	}

	exitCheck = setUserid(con, s, scan, "Please enter in your Customer ID. Type 0 if you want to exit back to main menu");
	if(exitCheck == 1){
	    System.out.println("Exit made, returning to main menu");
	    return;
	}
	exitCheck = setAccountid(con, s, scan, "Please enter in your Account ID. Type 0 if you want to exit back to main menu");
	if(exitCheck == 1){
	    System.out.println("Exit made, returning to main menu");
            return;
	}

	if(this.dorw == true){
            exitCheck = setLocationid(con, s, scan, this.dorw, "Please enter in the Teller ID (Number was shown when you first launched the interface, but can be any number 1-20). Type 0 if you want to exit back to main menu");
	    if(exitCheck == 1){
		System.out.println("Exit made, returning to main menu");
		return;
	    }
	}else{
	    if(loc == true){
		exitCheck = setLocationid(con, s, scan, this.dorw, "Please enter in the Teller ID (Number was shown when you first launched the interface, but can be any number 1-20). Type 0 if you want to exit back to main menu");
		if(exitCheck == 1){
		    System.out.println("Exit made, returning to main menu");
		    return;
		}
	    }else{
		exitCheck = setLocationid(con, s, scan, this.dorw, "Please enter in the ATM ID (Number was shown when you first launched the interface, but can be any number 1-20). Type 0 if you want to exit back to main menu");
		if(exitCheck == 1){
		    System.out.println("Exit made, returning to main menu");
		    return;
		}
	    }
	}
        this.amount = g.getDouble(scan, "Please enter in the transaction amount. If the transaction is a WITHDRAWAL, type in a NEGATIVE integer. Type 0 to return to main menu");

	if(this.amount == 0 && g.getTest() == false){
	    System.out.println("Exit made, returning to main menu");
	    return;
	}
	
	if(this.amount > 50000000 && dorw == true){
	    System.out.println("We cannot let you deposit this amount of money at this time. Please contact bank personnel if you need to deposit this amount of money so we can verify that you do indeed have this amount. As a result of this you are being sent back to main menu");
	    return;
	}
	
	if(this.amount < -100000 && dorw == false){
	    System.out.println("We cannot let you withdrawal this amount of money at this time. Please contact bank personnel if you need to withdrawal this amount of money so we can figure out that process. As a result of this you are being sent back to main menu");
	}
	
	if(this.amount > 0 && dorw == false){
	    System.out.println("You did not set your withdrawal value to negative. As a result you are being kicked back to main menu. You cannot steal money from us!!!!!!!!!!!!");
	    return;
	}
	
	if(this.amount < 0 && dorw == true){
	    System.out.println("You did not set your deposit values to positive. What are you even trying to do?!!!!!!!! You are being kicked back to main menu");
	    return;
	}

	Transaction t = new Transaction(con, s, this.amount);
	if(t.getTransactionid() == 0){
	    System.out.println("Could not find a Transaction ID. Please contact the bank personnel. Exiting to main menu");
	    return;
	}
	s = g.prepareS(con, s, t.executeMainInsert());
	g.setInt(1, t.getTransactionid(), s, con);
	g.setDouble(2, t.getAmount(), s, con);
	g.executeU(con, s);
	exitCheck = executeOtherInserts(con, s, t.getTransactionid());
	if(exitCheck == 1){
	    String query = "delete from transaction where transaction_id = ?";
	    s = g.prepareS(con, s, query);
	    g.setInt(1, t.getTransactionid(), s, con);
	    g.executeU(con, s);
	    System.out.println("Problem with changing your account balance. Please contact bank personnel. Exiting to main menu");
	    return;
	}else if(exitCheck == 2){
	    String query = "delete from transaction where transaction_id = ?";
	    s = g.prepareS(con, s, query);
	    g.setInt(1, t.getTransactionid(), s, con);
            g.executeU(con, s);
            System.out.println("Problem with changing your account balance. Insufficient Funds found. Exiting to main menu");
	    return;
	}
	System.out.println("\n\nTransaction accurately recorded and updated. :) Enjoy \n");
    }

    private int executeOtherInserts(Connection con, PreparedStatement s, int id){
	String query = "";
	String query2 = "";
	ResultSet set;
	if(this.loc == true){
	    query = "insert into teller_transaction values(?)"; //transaction id
	    query2 = "insert into teller_location values (?, ?)";//trasnasction id and location id
	}else{
	    query = "insert into bank_transaction values(?)";//transaction id
	    query2 = "insert into bank_location values (?, ?)"; //transaction id and location id
	}
	s = g.prepareS(con, s, query);
	g.setInt(1, id, s, con);
	g.executeU(con, s);
	s = g.prepareS(con, s, query2);
	g.setInt(1, id, s, con);
	g.setInt(2, this.locationid, s, con);
	g.executeU(con, s);
	
	if(this.dorw == true){
	    query = "insert into deposit values (?, ?)"; //transaction id and account id
	}else{
	    query = "insert into withdrawal values (?,?)";// trasnaction id and account is
	}
	s = g.prepareS(con, s, query);
	g.setInt(1, id, s, con);
	g.setInt(2, this.accountid, s, con);
	g.executeU(con, s);

	query = "select acct_balance from bank_account where account_id = ? "; //account id
	s = g.prepareS(con, s, query);
	g.setInt(1, this.accountid, s, con);
	set = g.executeQ(con, s);
	try{
	if(!set.next()){
	    return 1;
	}
	    this.currAcctBal = set.getDouble("acct_balance");
	}catch (Exception e){
	    return 1;
	}
	double newBal = this.currAcctBal + amount;
	
	if(newBal < this.minBal){
	    if(this.sorc == true){
		System.out.println("You have overdrawn your checking account. As a result you are incurring a fee of $47. You will incur a fee for every Deposit/Withdrawal that does not put you balance positive. Your Account may be closed by bank personnel and you will be billed for the negative balance plus the fee(s)");
		newBal = newBal - 47;
	    }else{
		return 2;
	    }
	} 
	query = "update bank_account set acct_balance = ? where account_id = ?"; //new bal and account id
	s = g.prepareS(con, s, query);
	g.setDouble(1, newBal, s, con);
	g.setInt(2, this.accountid, s, con);
	g.executeU(con, s);
	return 0;
    }

    private int checkAcct (Connection con, PreparedStatement s, Scanner scan){
	String query = "";
	ResultSet check;
	if(this.sorc == true){
	    query = "select account_id, min_balance from checking where account_id = ?"; //account id
	    s = g.prepareS(con, s, query);
	    g.setInt(1, this.accountid, s, con);
	    check = g.executeQ(con, s);
	    if(g.checkNext(check) == true){
		return setAccountid(con, s, scan, "Your Account ID was incorrect. Please try again");
	    }
	    
	    try{
	    this.minBal = check.getDouble("min_balance");
	    }catch (Exception e){
		System.out.println("Could not properly read the table. Need to return to main menu. Please contact bank personnel");
		return 1;
	    }
	}else{
	    query = "select account_id from savings where account_id = ?"; //account id
	    s = g.prepareS(con, s, query);
	    g.setInt(1, this.accountid, s, con);
	    check = g.executeQ(con, s);
	    if(g.checkNext(check) == true){
		return setAccountid(con, s, scan, "Your Account ID was incorrect. Please try again");
	    }
	    this.minBal = 0;
	}
	query = "select customer_id, account_id from cus_accounts where account_id = ? and customer_id = ?"; //account id and user id
	s = g.prepareS(con, s, query);
	g.setInt(1, this.accountid, s, con);
	g.setInt(2, this.userid, s, con);
	check = g.executeQ(con, s);
	if(g.checkNext(check) == true){
	    return setAccountid(con, s, scan, "Your Account ID was valid, but is not associated with your customer ID. Please try again");
	}
	return 0;
    }
    
    private int checkUser(Connection con, PreparedStatement s, Scanner scan){
	String query = "select customer_id from customer where customer_id = ?"; //userid
	s = g.prepareS(con, s, query);
	g.setInt(1, this.userid, s, con);
	ResultSet check = g.executeQ(con, s);
	if(g.checkNext(check) == true){
	    return setUserid(con, s, scan, "Your User ID was incorrect. Please try again");
	}
	if(this.sorc == true){
	    query = "select account_id, acct_balance from cus_accounts natural join checking natural join bank_account where customer_id = ?"; //userid
	}else{
	    query = "select account_id, acct_balance from cus_accounts natural join savings natural join bank_account  where customer_id = ?"; //userid
	}
	s = g.prepareS(con, s, query);
	g.setInt(1, this.userid, s, con);
	check = g.executeQ(con, s);
	try{
	    System.out.println("Here are the accounts of your chosen type listed under your Customer ID. If there is no results below then you have no accounts of that type.");
	    while(check.next()){
		int aId = check.getInt("account_id");
		double b = check.getDouble("acct_balance");
		System.out.println("Account ID: " + aId + "\t Balance: " + b);
	    }
	}catch(Exception e){
	    System.out.println("Something went wrong with the searching for your account. Please contact bank personnel. Sending you back to the main menu");
	    return 1;
	}
	return 0;
    }

    private int checkLoc(Connection con, PreparedStatement s, boolean type,  Scanner scan){
	String query = "";
	ResultSet check;
	if(type == true){
	    query = "select teller_id from Teller where teller_id = ?"; //locationid
	    s = g.prepareS(con, s, query);
	    g.setInt(1, this.locationid, s, con);
	    check = g.executeQ(con, s);
	    if(g.checkNext(check) == true){
		return setLocationid(con, s, scan, type, "Your Teller ID was incorrect. Please try again");
	    }
	}else{
	    if(this.loc == true){
		query = "select teller_id from Teller where teller_id = ?"; //locationid
		s = g.prepareS(con, s, query);
		g.setInt(1, this.locationid, s, con);
		check = g.executeQ(con, s);
		if(g.checkNext(check) == true){
		    return setLocationid(con, s, scan, type, "Your Teller ID was incorrect. Please try again");
		}
	    }else{
		query = "select branch_id from bank where branch_id = ?";
		s = g.prepareS(con, s, query);
		g.setInt(1, this.locationid, s, con);
		check = g.executeQ(con, s);
		if(g.checkNext(check) == true){
		    return setLocationid(con, s, scan, type, "Your ATM ID was incorrect. Please try again");
		}
	    }
	}
	return 0;
    }

    private int setUserid(Connection con, PreparedStatement s, Scanner scan, String input){
	this.userid = g.getInteger(scan, input);
	if(this.userid == 0){
	    return 1;
	}
	return checkUser(con, s, scan);
    }

    private int setAccountid (Connection con, PreparedStatement s, Scanner scan, String input){
	this.accountid = g.getInteger(scan, input);
	if(this.accountid == 0){
	    return 1;
	} 
	return checkAcct(con, s, scan);
    }
    
    private int setLocationid (Connection con, PreparedStatement s, Scanner scan, boolean type, String input){
	this.locationid = g.getInteger(scan, input);
	if(this.locationid == 0){
	    return 1;
	}
	return checkLoc(con, s, type, scan);
    }
    
}