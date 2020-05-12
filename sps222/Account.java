
import java.util.*;
import java.sql.*;

public class Account{
    
    private GeneralMethods g = new GeneralMethods();
    
    private int custId;
    private int acctId;
    private double amount;
    
    
    private boolean sorc; //checking if true, savings if false

    public Account(Connection con, PreparedStatement s, Scanner scan){

	//String testSQLException = "afksfjhaksfhakfha";
	//s = g.prepareS(con, s, testSQLException);
	//g.executeU(con, s);
	

	int exitCheck = 0;
	String choice = "";
	while(!choice.equalsIgnoreCase("Q")){
	choice = g.getString(scan, "Are you opening a new checking or savings account? Type C for checking, S for savings and Q to exit to main menu");
        if(choice.equalsIgnoreCase("C")){
            this.sorc = true;
	    System.out.println("\nThank you for selecting a checking account. All new checking accounts at Nickel Bank do not recieve interest and have a minimum balance of $-20 before fees are applied and debit card payments are declined");
	    break;
        }else if (choice.equalsIgnoreCase("S")){
            this.sorc = false;
	    System.out.println("\nThank you for selecting a savings account. All new savings accounts at Nickel Bank have an interest rate of 1% per year and a minimum balance of $0.");
	    break;
        }else if (choice.equalsIgnoreCase("Q")){
            System.out.println("Exit made, returning to main menu");
            return;
        }else{
	    System.out.println("Incorrect input, please try again");
	}
	}
	System.out.println("\nIf you want to apply for an increased interest rate or a decrease in minimum balance, please contact the bank personnel where they can take you through the applicaton process\n");
	
        exitCheck = setUserid(con, s, scan, "Please enter in your Customer ID. Type 0 if you want to exit back to main menu");
        if(exitCheck == 1){
            System.out.println("Exit made, returning to main menu");
            return;
        }

	this.acctId = getNewAcctId(con, s);
	if(this.acctId <= 0){
	    System.out.println("There was an issue creating your Account ID, please contact bank personnel");                                                                                     
	    return;                                                                
	}
	System.out.println("Your Account ID is: " + this.acctId + ". Please write this down and DO NOT forget it");
	
	this.amount = g.getDouble(scan, "Please enter in the initial deposit you would like to make");
	if(this.amount < 0){
	    System.out.println("You cannot make a deposit of NEGATIVE dollars. As a result you are being kicked back to main menu without your new account");
	    return;
	}
	
	if(this.amount > 500000000){
	    System.out.println("We cannot let you deposit this amount of money at this time. Please contact bank personnel if you need to deposit this amount of money so we can verify that you do indeed have this amount. As a result of this you are being sent back to main menu\n");
	    return;
	}
	
	executeInserts(con, s);
	System.out.println("Successfully created your new account. Enjoy :) Exiting to main menu");	
    }

    private void executeInserts(Connection con, PreparedStatement s){
	String query = "insert into bank_account values (?, ?, sysdate)"; //account id and amount
	s = g.prepareS(con, s, query);
	g.setInt(1, this.acctId, s, con);
	g.setDouble(2, this.amount, s, con);
	g.executeU(con, s);
	query = "insert into cus_accounts values (?, ?)";
	s = g.prepareS(con, s, query);
	g.setInt(1, this.custId, s, con);
	g.setInt(2, this.acctId, s, con);
	g.executeU(con, s);
	if(this.sorc == true){
	    query = "insert into checking values (?, -20)"; //acctId
	}else{
	    query = "insert into savings values (?, 1)";
	}
	s = g.prepareS(con, s, query);
	g.setInt(1, this.acctId, s, con);
	g.executeU(con, s);
	
    }

    private int getNewAcctId(Connection con, PreparedStatement s){
	String query = "select MAX(account_id) A_id from bank_account";
	s = g.prepareS(con, s, query);
        ResultSet res = g.executeQ(con, s);                                                                                                                                                                                           
        int id = -1;                                                                                                                                                                                                                         
        if(g.checkNext(res) == true){                                                                                                                                                                                                        
            return id;
        }       
	try{
        id = res.getInt("A_id");
	}catch(Exception e){
	    return id;
	}
        id++;
        return id;   
    }


    private int checkUser(Connection con, PreparedStatement s, Scanner scan){
        String query = "select customer_id from customer where customer_id = ?";
	s = g.prepareS(con, s, query);
	g.setInt(1, this.custId, s, con);
        ResultSet check = g.executeQ(con, s);
        if(g.checkNext(check) == true){
            return setUserid(con, s, scan, "Your User ID was incorrect. Please try again");
        }
        return 0;
    }

    private int setUserid (Connection con, PreparedStatement s, Scanner scan, String input){
        this.custId = g.getInteger(scan, input);
        if(this.custId == 0){
            return 1;
        }
        return checkUser(con, s, scan);
    }


} 