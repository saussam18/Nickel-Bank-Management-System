

import java.util.*;
import java.sql.*;


public class GeneralMethods{

    private boolean test = false;

    public GeneralMethods(){

    }
    
    public void setString(int i, String value, PreparedStatement s, Connection c){
	try{
            s.setString(i,value);
	}catch(Exception e){
            //e.printStackTrace();
            System.out.println("Critical Error: Could not execute prepare query. Need to exit system. Please try again. If problem persits, please contact bank personnel");
            System.out.println("Closing System");
            close(c, s);
            System.exit(0);
	}
    }

    public void setDouble(int i, double value, PreparedStatement s, Connection c){
	try{
            s.setDouble(i,value);
	}catch(Exception e){
            //e.printStackTrace();
            System.out.println("Critical Error: Could not execute prepare query. Need to exit system. Please try again. If problem persits, please contact bank personnel");
            System.out.println("Closing System");
            close(c, s);
            System.exit(0);
	}
    }

    public  void setInt(int i, int value, PreparedStatement s, Connection c){
	try{
	    s.setInt(i, value);
	}catch(Exception e){
	    //e.printStackTrace();
	    System.out.println("Critical Error: Could not execute prepare query. Need to exit system. Please try again. If problem persits, please contact bank personnel");
            System.out.println("Closing System");
            close(c, s);
            System.exit(0);
	}
    }

    public  PreparedStatement prepareS(Connection c, PreparedStatement s, String query){
	try{
	    s  = c.prepareStatement(query);
	}catch (Exception e){
	    //e.printStackTrace();
	    System.out.println("Critical Error: Could not execute prepare query. Need to exit system. Please try again. If problem persits, please contact bank personnel");
            System.out.println("Closing System");
            close(c, s);
            System.exit(0);
	}
	return s;
    }


    public  void close (Connection c, PreparedStatement s){
	try{
	s.close();
	c.close();
	}catch(Exception e){
	    System.out.println("Could not close the connection. Closing Program");
	    System.exit(0);
	}
    }
    
    public  ResultSet executeQ(Connection c, PreparedStatement s){
        ResultSet set = null;
        try{
            set = s.executeQuery();
            return set;
        }catch(Exception e){
            //e.printStackTrace();
	    System.out.println("Critical Error: Could not execute database query. Need to exit system. Please try again. If problem persits, please contact bank personnel");
	    System.out.println("Closing System");
	    close(c, s);
	    System.exit(0);
        }
        return set;
    }


    public  void executeU(Connection c, PreparedStatement s){
        try{
            s.executeUpdate();
        }catch(Exception e){
            //e.printStackTrace();
	    System.out.println("Critical Error: Could not execute database query. Need to exit system. Please try again. If problem persits, please contact bank personnel");
            System.out.println("Closing System");
            close(c, s);
            System.exit(0);
        }
    }

    public  double getDouble(Scanner scan, String input){
	String tem = "";
        double output = -1;
        System.out.println(input);
        tem = scan.nextLine();
        try{
            output = Double.parseDouble(tem);
        }catch (Exception e){
            output = -1;
	    this.test = true;
        }
        while((tem.isEmpty() || output == -1) && this.test == true){
            System.out.println(input);
            tem = scan.nextLine();
            try{
                output = Double.parseDouble(tem);
		this.test = false;
            }catch (Exception e){
                output = -1;
            }
        }
        return output;
    }
    
    public boolean getTest(){
	return this.test;
    }

    public  int getInteger(Scanner scan, String input){
        String tem = "";
        int output = -1;
        System.out.println(input);
        tem = scan.nextLine();
        try{
            output = Integer.parseInt(tem);
        }catch (Exception e){
            output = -1;
        }
        while(tem.isEmpty() || output == -1){
            System.out.println(input);
            tem = scan.nextLine();
            try{
                output = Integer.parseInt(tem);
            }catch (Exception e){
                output = -1;
            }
        }
        return output;
    }

    public  String getString(Scanner scan, String input){
        String output = "";
        System.out.println(input);
        output = scan.nextLine();
        while(output.isEmpty()){
            System.out.println(input);
            output = scan.nextLine();
        }
        return output;
    }

    public  boolean checkNext(ResultSet set){
        boolean check = false;
        try{
            if(!set.next()){
                check = true;
            }
        }catch(Exception e){
            //e.printStackTrace();
	    System.out.println("Critical Error: Could not execute database query. Need to exit system. Please try again. If problem persits, please contact bank personnel");
            System.out.println("Closing System");
            System.exit(0);
        }
        return check;
    }
    

}