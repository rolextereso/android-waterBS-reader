package com.database.helper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.database.model.Barangay;
import com.database.model.Customer;
import com.database.model.Reading;
import com.database.model.Users;

public class DBHelper<ArrayInteger> extends SQLiteOpenHelper {

	
   public static final String DATABASE_NAME = "wbs.db";
   String where;
   //Table for users...
   public static final String USERS_TABLE_NAME = "users";
   public static final String USERS_COLUMN_ID = "id";
   public static final String USERS_COLUMN_FNAME = "fname";
   public static final String USERS_COLUMN_LNAME = "lname";
   public static final String USERS_COLUMN_USERNAME = "username";
   public static final String USERS_COLUMN_PASSWORD = "password";
   public static final String USERS_COLUMN_UTYPE = "user_type";
   public static final String USERS_COLUMN_USTAT = "user_stat";
   
   //Table for customer
   public static final String CUS_TABLE_CUSTOMER = "customer";
   public static final String CUS_COLUMN_CID = "id";
   public static final String CUS_COLUMN_IDCUS = "customerid";
   public static final String CUS_COLUMN_INFO_IDCUS = "customer_info_id";
   public static final String CUS_COLUMN_CFNAME = "fname";
   public static final String CUS_COLUMN_CMNAME = "mname";
   public static final String CUS_COLUMN_CLNAME = "lname";
   public static final String CUS_COLUMN_CORP = "corporate";
   public static final String CUS_COLUMN_BRGY = "barangay";//7
   public static final String CUS_COLUMN_CTYPE = "connection_type";
   public static final String CUS_COLUMN_METERNO = "meter_no";
   public static final String CUS_COLUMN_METERID = "meter_id";
   public static final String CUS_COLUMN_PREVAMNT = "prev_amount";
   public static final String CUS_COLUMN_PRESDATE = "prev_reading_date";
   public static final String CUS_COLUMN_PRESVAL = "prev_reading_value";
   public static final String CUS_COLUMN_METERBAL = "cur_meter_bal";
   public static final String CUS_COLUMN_PENALTY = "penalty_amount";
   public static final String CUS_COLUMN_RECONNECTION = "reconnection";
   public static final String CUS_COLUMN_BRGYID = "barangay_id";
   public static final String CUS_COLUMN_STAT = "reading_stat";
   public static final String CUS_COLUMN_PUROK_NO = "purok_no";
   
   
   //Table for water_formula_values
   public static final String W_TABLE_NAME = "water_formula_values";
   public static final String W_COLUMN_WID = "wid";
   public static final String W_COLUMN_MINIMUM = "min_val";
   public static final String W_COLUMN_VPC = "value_per_cubic";
   public static final String W_COLUMN_ADDITIONAL = "add_amount";
   public static final String W_COLUMN_DUE_DATE = "due_date_range";
   public static final String W_COLUMN_PENALTY = "penalty";
   
 //Table for reading 
   public static final String R_TABLE_NAME = "wbsreading";
   public static final String R_COLUMN_ID = "rid";//20
   public static final String R_COLUMN_CUSID = "customerid";
   public static final String R_COLUMN_PRESRDATE = "pres_reading_date";
   public static final String R_COLUMN_PRESRVALUE= "pres_reading_value";//23
   public static final String R_COLUMN_RAMOUNT = "reading_amount";
   public static final String R_COLUMN_USER = "reader";//25
   public static final String R_COLUMN_TIMEDATE= "reg_date";//26
   
 //Table for water_formula_values
   public static final String PRINTER_TABLE_NAME = "WBS_printer";
   public static final String PRINTER_COLUMN_ID = "PrinterId";
   public static final String PRINTER_COLUMN_NAME = "PrinterName";
   


   private HashMap<?, ?> hp;

   public DBHelper(Context context)
   {
      super(context, DATABASE_NAME , null, 1);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      // TODO Auto-generated method stub
      db.execSQL(
      "create table " +CUS_TABLE_CUSTOMER+
      "("+CUS_COLUMN_CID+" integer primary key, " +CUS_COLUMN_IDCUS+" integer," +
      CUS_COLUMN_INFO_IDCUS+ " text," +
      CUS_COLUMN_CFNAME+ " text," +
      CUS_COLUMN_CMNAME+"  text," +
      CUS_COLUMN_CLNAME+"  text," +
      CUS_COLUMN_CORP+"  text," +
      CUS_COLUMN_BRGY+"  text," +
      CUS_COLUMN_CTYPE+"  text," +
      CUS_COLUMN_METERID+"  text," +
      CUS_COLUMN_PREVAMNT+"  double," +
      CUS_COLUMN_PRESDATE+"  text," +
      CUS_COLUMN_PRESVAL+" double,"+
      CUS_COLUMN_METERBAL+" double,"+
      CUS_COLUMN_PENALTY+" double,"+
      CUS_COLUMN_RECONNECTION+" double,"+
      CUS_COLUMN_BRGYID+" integer,"+
      CUS_COLUMN_METERNO+" text,"+
      CUS_COLUMN_STAT+" integer," +
      CUS_COLUMN_PUROK_NO+" text"+
      		")"
      );
   
      db.execSQL(
    	      "create table " +R_TABLE_NAME+
    	      "("+R_COLUMN_ID+" integer primary key, " +R_COLUMN_CUSID+" text," +
    	      R_COLUMN_PRESRDATE+ " text," +
    	      R_COLUMN_PRESRVALUE+"  int," +
    	      R_COLUMN_RAMOUNT+"  double," +
    	      R_COLUMN_USER+"  text," +
    	      R_COLUMN_TIMEDATE+ " text)" 
    	 
    	     
      );
      
      db.execSQL(
    	      "create table " +USERS_TABLE_NAME+
    	      "("+USERS_COLUMN_ID+" integer primary key, " +USERS_COLUMN_FNAME+" text," +
    	       USERS_COLUMN_LNAME+ " text," +
    	       USERS_COLUMN_USERNAME+"  text," +
    	       USERS_COLUMN_PASSWORD+"  text," +
    	       USERS_COLUMN_UTYPE+" text,"+
    	       USERS_COLUMN_USTAT+" text)"
      );
      
      
      db.execSQL(
    	      "create table " +W_TABLE_NAME+
    	      "("+W_COLUMN_WID+" integer primary key, " +W_COLUMN_MINIMUM+" float," +
    	      W_COLUMN_VPC+ " float," +
    	      W_COLUMN_DUE_DATE+ " int,"+
    	      W_COLUMN_PENALTY+ " int,"+W_COLUMN_ADDITIONAL+"  float)"
      );
      
      db.execSQL(
    	      "create table " +PRINTER_TABLE_NAME+
    	      "("+PRINTER_COLUMN_ID+" integer primary key, " +PRINTER_COLUMN_NAME+" text)"
      );
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // TODO Auto-generated method stub
      db.execSQL("DROP TABLE IF EXISTS "+USERS_TABLE_NAME);
      db.execSQL("DROP TABLE IF EXISTS "+CUS_TABLE_CUSTOMER);
      db.execSQL("DROP TABLE IF EXISTS "+W_TABLE_NAME);
      db.execSQL("DROP TABLE IF EXISTS "+R_TABLE_NAME);
      
      onCreate(db);
   }
  // for user table
   public Boolean insertUser(Users users)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();

      contentValues.put(USERS_COLUMN_FNAME, users.getFname());
      contentValues.put(USERS_COLUMN_LNAME, users.getLname());
      contentValues.put(USERS_COLUMN_USERNAME, users.getUsername());	
      contentValues.put(USERS_COLUMN_PASSWORD, users.getPassword());
      contentValues.put(USERS_COLUMN_UTYPE, users.getUser_type());
      contentValues.put(USERS_COLUMN_USTAT, users.getUser_stat());

      db.insert(USERS_TABLE_NAME, null, contentValues);
      db.close(); 
      return true;
   }

   public Boolean updateUser (Users users)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put(USERS_COLUMN_FNAME, users.getFname());
      contentValues.put(USERS_COLUMN_LNAME, users.getLname());
      contentValues.put(USERS_COLUMN_USERNAME, users.getUsername());	
      contentValues.put(USERS_COLUMN_PASSWORD, users.getPassword());
      contentValues.put(USERS_COLUMN_UTYPE, users.getUser_type());
      contentValues.put(USERS_COLUMN_USTAT, users.getUser_stat());

          
      db.update(USERS_TABLE_NAME, contentValues, USERS_COLUMN_ID+" = ? ", new String[] { Integer.toString(users.getId()) } );
      db.close();
	return true;
   }
   
     
   public Boolean block_editstat (Users users)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put(USERS_COLUMN_USTAT, users.getUser_stat());	
      
      db.update(USERS_TABLE_NAME, contentValues, USERS_COLUMN_ID+" = ? ", new String[] { Integer.toString(users.getId()) } );
      db.close();
	return true;
   }
   
   public Boolean block_editUser (Users users)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put(USERS_COLUMN_USTAT, users.getUser_stat());
     // if(!users.getPassword().isEmpty()){
    	  contentValues.put(USERS_COLUMN_PASSWORD, users.getPassword());	
      //}
      
      
      db.update(USERS_TABLE_NAME, contentValues, USERS_COLUMN_ID+" = ? ", new String[] { Integer.toString(users.getId()) } );
      db.close();
	return true;
   }
   
   
   public List<Users> validate_user_all(String username,String password){
	   
	    List<Users> userList = new ArrayList<Users>();
		// Select All Query
	    String where="";
	    if((!username.isEmpty() && !password.isEmpty()) || (!username.equals("") && !password.equals(""))) {
	    	   where=" where "+USERS_COLUMN_USERNAME+"='"
	 	    		  +username+"' and "+USERS_COLUMN_PASSWORD+" ='"+password+"' and "+USERS_COLUMN_USTAT+"='Active' ";
	     }
		String selectQuery = "SELECT  * FROM users "+where+" ORDER BY fname";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Users users = new Users();
				users.setId(Integer.parseInt(cursor.getString(0)));
				users.setFname(cursor.getString(1));
				users.setLname(cursor.getString(2));
				users.setUsername(cursor.getString(3));
				users.setPassword(cursor.getString(4));
				users.setUser_type(cursor.getString(5));
				users.setUser_stat(cursor.getString(6));
			
				userList.add(users);
			} while (cursor.moveToNext());
		}
		// close inserting data from database
		db.close();
		// return contact list
		return userList;
	     
  }
   
   public List<Users> All_User(){
	   
	    List<Users> userList = new ArrayList<Users>();
		// Select All Query
	    
		String selectQuery = "SELECT  * FROM users WHERE user_type!='Administrator' ORDER BY fname";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Users users = new Users();
				users.setId(Integer.parseInt(cursor.getString(0)));
				users.setFname(cursor.getString(1));
				users.setLname(cursor.getString(2));
				users.setUsername(cursor.getString(3));
				users.setPassword(cursor.getString(4));
				users.setUser_type(cursor.getString(5));
				users.setUser_stat(cursor.getString(6));
			
				userList.add(users);
			} while (cursor.moveToNext());
		}
		// close inserting data from database
		db.close();
		// return contact list
		return userList;
	     
 }
   
   public Cursor select_all_user(String usertype){
	      SQLiteDatabase db = this.getReadableDatabase();
	      String where=" ";
	      if(!usertype.isEmpty() || usertype=="" || usertype.equals("")){
	    	   where=" where "+USERS_COLUMN_ID+"='"+usertype+"' OR "+USERS_COLUMN_UTYPE+"='"
		    		  +usertype+"';";
	      }
	     
		  Cursor res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" "+where,null);
	   	      
	      return res;
   }

  
   
   //end user table
   //start reading table
   public int updateStat (int id,int stat)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
     
      contentValues.put(CUS_COLUMN_STAT, stat );
      int row_update= db.update(CUS_TABLE_CUSTOMER, contentValues, CUS_COLUMN_IDCUS+" = ? ", new String[] { Integer.toString(id) } );
      return row_update;
   }
   
   public Cursor customer_leftjoin_reading(int idcustomer){
	      SQLiteDatabase db = this.getReadableDatabase();
     
		  Cursor res =  db.rawQuery("Select * from customer as c LEFT JOIN wbsreading as w on w.customerid=c.customerid where c.customerid="+idcustomer+"" ,null);
	   	 
	      return res;
   }
   public Cursor customer_leftjoin_readingreport(String barangay){
	      SQLiteDatabase db = this.getReadableDatabase();
	      String whereclause="";
	      if(barangay.equals("all")){
	    	  whereclause="";
	      }else{
	    	  whereclause=" and c.barangay='"+barangay+"'";
	      }
  
		  Cursor res =  db.rawQuery("Select * from customer as c LEFT JOIN wbsreading as w on w.customerid=c.customerid where (c.reading_stat=2 or c.reading_stat=1) "+whereclause ,null);
	   	 
	      return res;
}
   public Cursor customer_leftjoin_readingval(){
	      SQLiteDatabase db = this.getReadableDatabase();
	            
		  Cursor res =  db.rawQuery("Select * from customer as c LEFT JOIN wbsreading as w on w.customerid=c.customerid where c.reading_stat!=0 " ,null);
	   	 
	      return res;
   }
   
   public Cursor select_barangay(String barangay){
	      SQLiteDatabase db = this.getReadableDatabase();
	      String whereClause="";
	      if(!barangay.isEmpty()){
	    	 whereClause=" WHERE barangay = "+barangay;
	      }
	            
		  Cursor res =  db.rawQuery("Select Distinct barangay from customer "+whereClause+" ORDER BY barangay asc" ,null);
	   	 
	      return res;
   }

   public int insertReading  (Reading reading)
   {
	   
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();

      contentValues.put(R_COLUMN_CUSID, reading.getCustomerid());
      contentValues.put(R_COLUMN_PRESRDATE, reading.getPres_reading_date());
      contentValues.put(R_COLUMN_PRESRVALUE, reading.getPres_reading_value());
      contentValues.put(R_COLUMN_RAMOUNT, reading.getReading_amount());	
      contentValues.put(R_COLUMN_USER, reading.getReader());
      contentValues.put(R_COLUMN_TIMEDATE, reading.getDatetime());
      int i= (int) db.insert(R_TABLE_NAME, null, contentValues);
      db.close(); 
      
      return i;
      
      
   }
   
   public int deleteReading(String id){
	   SQLiteDatabase db = this.getReadableDatabase();
	   int del=   db.delete(R_TABLE_NAME, R_COLUMN_CUSID+" = ? ", new String[] {id});
	   
	   return del;
   }
   
   public Cursor compute_water_amount(int reading_val){
	      SQLiteDatabase db = this.getReadableDatabase();
	          String query;
	      if(reading_val<=10){
	    	   query="Select 50 as reading_value";
	      }else{
	    	   query="select ((("+reading_val+"-min_val)*value_per_cubic)+add_amount) as reading_value from "+W_TABLE_NAME+"  ";
	      }
		  Cursor res =  db.rawQuery(query ,null);
	   	 
	      return res;
   }

   
   
   
   //end reading table
   //start of customer table
   public Cursor count_all_import(){
	      SQLiteDatabase db = this.getReadableDatabase();
	          
		  Cursor res =  db.rawQuery( "select count(*) as count_all, sum(penalty_amount) as penalty," +
		  							 "sum(prev_amount) as prev_amount," +
		  							 "sum(cur_meter_bal) as meter_balance," +
		  							 "sum(reconnection) as reconnection from "+CUS_TABLE_CUSTOMER+" ",null);
	   	 
	      return res;
   }
   public Cursor count_all_export(){
	      SQLiteDatabase db = this.getReadableDatabase();
	          
		  Cursor res =  db.rawQuery( "select count(*) as count_all, sum(pres_reading_value) as reading_amount from "+R_TABLE_NAME+" ",null);
	   	 //sum(reading_amount)
	      return res;
   }
   public Cursor unprintedBillCustomer(){
	      SQLiteDatabase db = this.getReadableDatabase();
	          
		  Cursor res =  db.rawQuery( "select count(*) as notPrinted  FROM customer where reading_stat=1; ",null);
	   	 //sum(reading_amount)
	      return res;
   }
   public Cursor countUnreadCustomer(){
	      SQLiteDatabase db = this.getReadableDatabase();
	          
		  Cursor res =  db.rawQuery( "select count(*) as unRead  FROM customer where reading_stat =0 or reading_stat=null; ",null);
	   	 //sum(reading_amount)
	      return res;
   } 
   public int insertCustomer  (Customer customer)
   {
	
	   
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();

      contentValues.put(CUS_COLUMN_IDCUS, customer.getCustomerid());
      contentValues.put(CUS_COLUMN_INFO_IDCUS, customer.getCustomer_info_id());
      contentValues.put(CUS_COLUMN_CFNAME, customer.getFname());
      contentValues.put(CUS_COLUMN_CMNAME, customer.getMname());
      contentValues.put(CUS_COLUMN_CLNAME, customer.getLname());	
      contentValues.put(CUS_COLUMN_CORP, customer.getCorporate());
      contentValues.put(CUS_COLUMN_BRGY, customer.getBarangay());
      contentValues.put(CUS_COLUMN_CTYPE, customer.getConnection_type());
      contentValues.put(CUS_COLUMN_METERNO, customer.getMeter_no());
      contentValues.put(CUS_COLUMN_METERID, customer.getMeter_id());
      contentValues.put(CUS_COLUMN_PREVAMNT, customer.getPrev_amount());
      contentValues.put(CUS_COLUMN_PRESDATE, customer.getPres_reading_date());
      contentValues.put(CUS_COLUMN_PRESVAL, customer.getPres_reading_value());
      contentValues.put(CUS_COLUMN_METERBAL, customer.getCur_meter_bal());
      contentValues.put(CUS_COLUMN_PENALTY, customer.getPenalty_amount());
      contentValues.put(CUS_COLUMN_RECONNECTION, customer.getReconnection());
      contentValues.put(CUS_COLUMN_BRGYID, customer.getBarangay_id());
      contentValues.put(CUS_COLUMN_STAT, customer.getStat());
      contentValues.put(CUS_COLUMN_PUROK_NO, customer.getPurok_no());
     
     int i= (int) db.insert(CUS_TABLE_CUSTOMER, null, contentValues);
      db.close(); 
      
      return i;
      
      
   }

   public boolean updateCustomer (Integer id,Integer custid, String fname, String lname, String corp, String ctype,String meterno)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();

      contentValues.put(CUS_COLUMN_IDCUS, custid);
      contentValues.put(CUS_COLUMN_CFNAME, fname);
      contentValues.put(CUS_COLUMN_CLNAME, lname);	
      contentValues.put(CUS_COLUMN_CORP, corp);
      contentValues.put(CUS_COLUMN_CTYPE, ctype);
      contentValues.put(CUS_COLUMN_METERNO, meterno);
      db.update(CUS_TABLE_CUSTOMER, contentValues, CUS_COLUMN_CID+" = ? ", new String[] { Integer.toString(id) } );
      return true;
   }
   
    
   public List<Customer> select_all_customer(){
	   
	    List<Customer> customerList = new ArrayList<Customer>();
		// Select All Query
	    String search="";
	    /*if(!query.isEmpty() || query!="" || !query.equals("")|| !query.equals(null)){
	    	search=" OR "+CUS_COLUMN_CFNAME+" like '"
		    		  +query+"%' OR "+CUS_COLUMN_CFNAME+" like '"+query+"%' OR "+CUS_COLUMN_CLNAME+" like '"+
	    			   query+"%' OR "+CUS_COLUMN_CORP+" like '"+query+"%' OR "+CUS_COLUMN_METERNO+" like '"+query+"%';";
	     }*/
		String selectQuery = "SELECT  * FROM customer where reading_stat =0 or reading_stat=null ORDER BY reading_stat desc";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Customer contact = new Customer();
				contact.setId(Integer.parseInt(cursor.getString(0)));
				contact.setCustomerid(cursor.getString(1));
				contact.setCustomer_info_id(cursor.getString(2));
				contact.setFname(cursor.getString(3));
				contact.setMname(cursor.getString(4));
				contact.setLname(cursor.getString(5));
				contact.setCorporate(cursor.getString(6));
				contact.setConnection_type(cursor.getString(8));
				contact.setBarangay(cursor.getString(7));
				contact.setMeter_no(cursor.getString(17));
				contact.setStat(Integer.parseInt(cursor.getString(18)));
				contact.setPurok_no(cursor.getString(19));
				//contact.setImage(cursor.getBlob(2));
				// Adding contact to list
				customerList.add(contact);
			} while (cursor.moveToNext());
		}
		// close inserting data from database
		db.close();
		// return contact list
		return customerList;
	     
   }
   public Cursor select_all_barangay(){

		String selectQuery = "SELECT DISTINCT(barangay) as brgy FROM customer  ORDER BY barangay";

		SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(selectQuery, null);
	   	 //sum(reading_amount)
	    return res;
	     
 }
   public List<Customer> select_all_readorunreadcustomer(){
	   
	    List<Customer> customerList = new ArrayList<Customer>();
		
		String selectQuery = "Select * from customer as c LEFT JOIN wbsreading as w on w.customerid=c.customerid" +
				"					 where reading_stat=2 or reading_stat=1 ORDER BY reading_stat asc, datetime(reg_date) asc";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Customer contact = new Customer();
				contact.setId(Integer.parseInt(cursor.getString(0)));
				contact.setCustomerid(cursor.getString(1));
				contact.setCustomer_info_id(cursor.getString(2));
				contact.setFname(cursor.getString(3));
				contact.setMname(cursor.getString(4));
				contact.setLname(cursor.getString(5));
				contact.setCorporate(cursor.getString(6));
				contact.setConnection_type(cursor.getString(8));
				contact.setMeter_no(cursor.getString(17));
				contact.setStat(Integer.parseInt(cursor.getString(18)));
				contact.setBarangay(cursor.getString(7));
				contact.setPurok_no(cursor.getString(19));
				//contact.setImage(cursor.getBlob(2));
				// Adding contact to list
				customerList.add(contact);
			} while (cursor.moveToNext());
		}
		// close inserting data from database
		db.close();
		// return contact list
		return customerList;
	     
  }
   
   public int drop_all_customer(){
	   SQLiteDatabase db = this.getWritableDatabase();
	   db.delete(R_TABLE_NAME, null, new String[] { });
	   int value=db.delete(CUS_TABLE_CUSTOMER, null, new String[] { });
	   return value;
	   
   }
   

   //end of customer table
   //start with water_formula_values
   public boolean insertWaterformulaValues  (float minval, float vpc, float additionalval)
   {
	 
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();

      contentValues.put(W_COLUMN_MINIMUM, minval);
      contentValues.put(W_COLUMN_VPC, vpc);
      contentValues.put(W_COLUMN_ADDITIONAL, additionalval);	
      

      db.insert(W_TABLE_NAME, null, contentValues);
      return true;
   }

   public boolean updateWaterformulaValues (String id, String minval, String vpc,String additionalval)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put(W_COLUMN_MINIMUM, minval);
      contentValues.put(W_COLUMN_VPC, vpc);
      contentValues.put(W_COLUMN_ADDITIONAL, additionalval);
      db.update(W_TABLE_NAME, contentValues, W_COLUMN_WID+" = ? ", new String[] { id } );
      return true;
   }
   
   public boolean updatedue_datePenalty (String id, String penalty,String due_date)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      
      contentValues.put(W_COLUMN_PENALTY, Integer.parseInt(penalty));
      contentValues.put(W_COLUMN_DUE_DATE, Integer.parseInt(due_date));
      db.update(W_TABLE_NAME, contentValues, W_COLUMN_WID+" = ? ", new String[] { id } );
      return true;
   }
   

   
   public Cursor viewWaterformulaValues(){
	      SQLiteDatabase db = this.getReadableDatabase();
	  
	     
		  Cursor res =  db.rawQuery( "select * from "+W_TABLE_NAME+" Limit 1; ",null);
	   	      
	      return res;
   }
   public Cursor viewSelectPrinter(){
	      SQLiteDatabase db = this.getReadableDatabase();
	  
	     
		  Cursor res =  db.rawQuery( "select * from "+PRINTER_TABLE_NAME+" Limit 1; ",null);
	   	      
	      return res;
   }
   public boolean updateSelectPrinter (String id, String printerName)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put(PRINTER_COLUMN_NAME, printerName);
      db.update(PRINTER_TABLE_NAME, contentValues, PRINTER_COLUMN_ID+" = ? ", new String[] { id } );
      return true;
   }
   public boolean insertSelectPrinter  (String printerName)
   {
	 
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();

      contentValues.put(PRINTER_COLUMN_NAME, printerName);

      db.insert(PRINTER_TABLE_NAME, null, contentValues);
      return true;
   }
   
  
}