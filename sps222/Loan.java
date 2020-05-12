

import java.util.*;
import java.sql.*;

public class Loan{

    private GeneralMethods g = new GeneralMethods();

    private int custId;
    private int loanId;
    private double amount;
    private String address;

    private boolean mortgage; //it is a mortgage if true, else it is false
    private double interestRate;

    public Loan (Connection con, PreparedStatement s, Scanner scan){
        int exitCheck = 0;
	String choice = "";
	while(!choice.equalsIgnoreCase("Q")){
	choice = g.getString(scan, "Are you taking a Mortgage on a house or a general loan? Type M for Mortgage, G for general loan and Q to exit back to main menu");
	if(choice.equalsIgnoreCase("M")){
	    this.mortgage = true;
	    this.address = g.getString(scan, "Please type in the FULL address of the home you are taking the loan on. You will have to contact bank personnel to correct an error");
	    break;
	}else if(choice.equalsIgnoreCase("G")){
	    this.mortgage = false;
	    break;
	}else if (choice.equalsIgnoreCase("Q")){
	    System.out.println("Exiting back to main menu");
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
	
	this.loanId = getNewLoanId(con, s);
        if(this.loanId <= 0){
            System.out.println("There was an issue creating your Loan ID, please contact bank personnel");
            return;
        }
        System.out.println("Your Loan ID is: " + this.loanId + ". Please write this down and DO NOT forget it");

	this.amount = g.getDouble(scan, "Please enter in the amount you need for the Loan");
        if(this.amount < 0){
            System.out.println("You cannot make a Loan for NEGATIVE dollars. DO NOT EVEN TRY AND STEAL MONEY FROM US. As a result you're being kicked back to main menu without a loan");
            return;
        }else if(this.amount > 50000000){
	    System.out.println("HOLY DO YOU THINK WE ARE RICH! We cannot let you borrow that much money. As a result you're being kicked back to main menu without a loan");
	    return;
	}else if(this.amount < 1000){
	    System.out.println("That was too little money to be worth signing a loan for. Just put it on a credit card. As a result you're being kicked back main menu without a loan");
	    return;
	}
	
	int creditScore = g.getInteger(scan, "Please type in your credit score. Please be HONEST as we will be verifying your score with a credit agency");
	getInterest(creditScore);
	System.out.println("You have been approved the loan at an interest rate of " + this.interestRate + "%");
	choice = g.getString(scan, "Would you like to accept this loan? Type Y or yes to say accept or any other character to decline the loan. You can always reapply");
	if(!choice.equalsIgnoreCase("Y") && !choice.equalsIgnoreCase("yes")){
	    System.out.println("You did no accept the loan offer. Exiting to main menu");
	    return;
	}
	executeInserts(con, s);
	System.out.println("Loan created successfully. Hope you enjoy your loan :) Exiting to main menu");
    }

    private void executeInserts (Connection con, PreparedStatement s){
	double monthPayment = this.amount / 100;
	String query = "insert into loan values (?, ?, ?, ?, sysdate)"; //loan id, interestrate, amount, monthpayment
	s = g.prepareS(con, s, query);
	g.setInt(1, this.loanId, s, con);
	g.setDouble(2, this.interestRate, s, con);
	g.setDouble(3, this.amount, s, con);
	g.setDouble(4, monthPayment, s, con);
	g.executeU(con, s);
	if(this.mortgage == true){
	    query = "insert into mortgage values (?, ?)"; //loan id, address
	    s = g.prepareS(con, s, query);
	    g.setInt(1, this.loanId, s, con);
	    g.setString(2, this.address, s, con);
	    g.executeU(con, s);
	}
	query = "insert into debt values (?, ?)"; //loan id, cust id
	s = g.prepareS(con, s, query);
	g.setInt(1, this.loanId, s, con);
	g.setInt(2, this.custId, s, con);
	g.executeU(con, s);
    }

    private int getNewLoanId(Connection con, PreparedStatement s){
        String query = "select MAX(loan_id) L_id from loan";
	s = g.prepareS(con, s, query);
        ResultSet res = g.executeQ(con, s);
        int id = -1;
        if(g.checkNext(res) == true){
            return id;
        }
        try{
	    id = res.getInt("L_id");
        }catch(Exception e){
            return id;
        }
        id++;
        return id;
    }

    private int checkUser(Connection con, PreparedStatement s, Scanner scan){
        String query = "select customer_id from customer where customer_id = ?"; //custid
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

    
    public void getInterest(int score){
	if(score > 800){
	    this.interestRate = 3;   
	}else if(score > 750){
	    this.interestRate = 4;
	}else if(score > 700){
	    this.interestRate = 5;
	}else if(score > 650){
	    this.interestRate = 6;
	}else if(score > 600){
	    this.interestRate = 7;
	}else if(score > 550){
	    this.interestRate = 8;
	}else if(score > 500){
	    this.interestRate = 9;
	}else{
	    this.interestRate = 10;
	}
    }

    
}