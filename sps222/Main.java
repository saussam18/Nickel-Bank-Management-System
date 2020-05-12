
import java.util.*;
import java.sql.*;


public class Main {

    public static void main (String [] args){
	try{
	    Scanner scan = new Scanner (System.in);
	String userid = getString (scan, "Enter your oracle User ID");
	String pw = getString(scan, "Enter your Oracle Password");
        try(
	    Connection con=DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", userid, pw);
            PreparedStatement s = null;
            ){
		Random rand = new Random();
		System.out.println("\nWelcome to Nickel Bank's Management Interface");
		int locId = rand.nextInt(19) + 1;
		interfaceInteraction(con, s, scan, locId);
		//Closing Statements
		//s.close();
		con.close();
	    }catch(Exception e){
	    //e.printStackTrace();
	    String choice = "";
	    do{
	    choice = getString(scan, "Login failed. If you want to try again Type R and if you would like to just quit Type Q.");
	    if(choice.equalsIgnoreCase("R")){
		main(null);
		break;
	    }else if(choice.equalsIgnoreCase("Q")){
		System.out.println("Exiting");
		System.exit(0);
	    }else{
		System.out.println("Invalid input, please try again");
		continue;
	    }
	    }while(!choice.equalsIgnoreCase("Q"));
	}
	}catch(Exception e){
	    System.out.println("Scanner Failure, unfortunately need to close the program. We apologize for this inconvenience");
	    System.exit(0);
	}
    }

    public static int getInteger(Scanner scan, String input){
	String tem = "";
	int output = 0;
	System.out.println(input);
	tem = scan.nextLine();
	output = Integer.parseInt(tem);
	while(tem.isEmpty() || output == 0){
	    System.out.println(input);
	    tem = scan.nextLine();
	    output = Integer.parseInt(tem);
	}
	return output;
    } 

    public static String getString(Scanner scan, String input){
	String output = "";
	System.out.println(input);
	output = scan.nextLine();
	while(output.isEmpty()){
            System.out.println(input);
            output = scan.nextLine();
        }
	return output;
    }

    public static void interfaceInteraction(Connection con, PreparedStatement s, Scanner scan, int locId){
	String control = "";
	do{
	    interfacePrint(locId);
	    control= getString(scan, "Please type the character with your designed action");
	    if(control.equals("1")){
		System.out.println("We are currently not offering this feature for our interface. Please select a different option");
		continue;
	    }else if(control.equals("2")){
		AccountDorW a = new AccountDorW(con, s, scan);
		continue;
	    }else if(control.equals("3")){
		System.out.println("We are currently not offering this feature for our interface. Please select a different option");
		continue;
	    }else if(control.equals("4")){
		Account ah = new Account(con, s, scan);
		continue;
	    }else if(control.equals("5")){
		System.out.println("We are currently not offering this feature for our interface. Please select a different option");
		continue;
	    }else if(control.equals("6")){
		Loan l = new Loan(con, s, scan);
		continue;
	    }else if(control.equals("7")){
		Purchase p = new Purchase(con, s, scan);
		continue;
	    }else if(control.equalsIgnoreCase("B")){
		batch(con, scan);
		continue;
	    }else if(control.equalsIgnoreCase("Q")){
		break;
	    }else if(control.equalsIgnoreCase("H")){
		help();
		continue;
	    }else{
		System.out.println("Uh oh, you typed in something that was not a command. Please try again");
		continue;
	    }
	}while(!control.equalsIgnoreCase("Q"));
	System.out.println("Exiting System");
    }


    public static void batch(Connection con, Scanner scan){
	String control = "";
	GeneralMethods g = new GeneralMethods();
	do{
	    System.out.println("");
	    System.out.println("Extra Procedures Implemented that I did for fun");
	    System.out.println("1. Update Card Activity Status based on Expiration Date");
	    System.out.println("2. Give interest to all savings accounts based on their interest rate");
	    System.out.println("3. MASS CREDIT CARD DEBT FORGIVNESS, DO NOT RUN UNLESS YOU WANT ALL RUNNING CREDIT CARD BALANCES TO BE 0");
	    System.out.println("Type \"E\" to exit back to the main menu");
	    System.out.println("");
	    control = getString(scan, "Please type the character with your designed action");
	    if(control.equals("1")){
		try{
	        CallableStatement pro = con.prepareCall ("begin cardexpiration; end;");
		pro.executeUpdate();
		System.out.println("Procedure completed. Thank you for updating the database");
		pro.close();
		}catch(Exception e){
		    System.out.println("Could not find procedure. Please contact bank personel");
		    continue;
		}
	    }else if(control.equals("2")){
		try{
		    CallableStatement pro = con.prepareCall ("begin giveonetimeinterest; end;");
		    pro.executeUpdate();
		    System.out.println("Procedure completed. Thank you for updating the database");
		    pro.close();
                }catch(Exception e){
                    System.out.println("Could not find procedure. Please contact bank personel");
                    continue;
                }
	    }else if(control.equals("3")){
		try{
		    CallableStatement pro = con.prepareCall ("begin creditforgiveness; end;");
		    pro.executeUpdate();
		    System.out.println("Procedure completed. Thank you for updating the database");
		    pro.close();
                }catch(Exception e){
                    System.out.println("Could not find procedure. Please contact bank personel");
                    continue;
                }
	    }else if(control.equalsIgnoreCase("E")){
		System.out.println("Returning to main menu");
		break;
	    }else{
		System.out.println("Uh oh, you typed in something that was not a command. Please try again");
		continue;
	    }
	}while(!control.equalsIgnoreCase("E"));
    }

    
    public static void help(){
	System.out.println("\nHello user, what was printed before you is the main interface for our bank management system. Each interface has an option symbol on the far left that you should be able to see.\n You select an interface by doing what you did to print out this statement, by typing that special symbol and pressing the enter key.\n Once you select an interface, you should be able to follow the statements that are printed out to you and type the commands.\n Each interface is protected against a lot of bad input, so don’t be afraid to type something in that may be wrong, you should not break the interface,\n but if you do, it will simply tell you so and as a result safely quit the programs where you can then restart and try again.\n Please send an email to sps222@lehigh.edu if you have further questions and I hope you enjoy the interface. Thank you :)\n");
    }

    public static void interfacePrint(int locId){
	System.out.println("");
	System.out.println("Nickel Bank Management System: Main Interface\n");
	System.out.println("Your Teller/ATM ID number is: " + locId + ". Please Remember this for potential Deposits and Withdrawals.\n");
	System.out.println("1. Access the Bank Management System (Coming Soon!)");
	System.out.println("2. Account Deposit/Withdrawal");
	System.out.println("3. Payment on Loan/Credit Card (Coming Soon!)");
	System.out.println("4. Open a new Account");
	System.out.println("5. Obtain a new Credit or Debit Card (Coming Soon!)");
	System.out.println("6. Take out a new Loan");
	System.out.println("7. Make Purchase with a Card");
	System.out.println("B. Type \"B\" to see what extra features and procedures are implemented");
	System.out.println("Q. Type \"Q\" to Quit");
	System.out.println("H. Type \"H\" to print the help Statement");
	System.out.println("");
    }



}