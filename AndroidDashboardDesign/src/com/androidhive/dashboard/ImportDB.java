package com.androidhive.dashboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidhive.dashboard.R;
import au.com.bytecode.opencsv.CSVReader;

import com.database.adapter.importexportAdapter;
import com.database.helper.DBHelper;
import com.database.model.Customer;
import com.navigation.drawer.activity.BaseActivity;



public class ImportDB extends BaseActivity {
	 /** Called when the activity is first created. */
	private static final int REQUEST_PATH = 1;
	 
	String curFileName,curPath;
	String[] values={"0","0","0","0","0"};
	EditText edittext;
	TextView numrow;
	String path;
	DBHelper mydb;
	int value=0,count=0;boolean values2,values3;
	String countall="0",penalty="0",prev_amount="0", meter_bal="0",reconnection="0";
	Boolean importsucc;
	int error;
	 ListView list;
	 String[] name={
			 "No. of rows",
			 "Total Penalty",
			 "Total Previous Amount Unpaid",
			 "Meter Balance",
			 "Reconnection fee"
	 };
	 String[] descrip={
			 "Total number of rows imported in the database",
			 "Total Amount of penalty imported in the database",
			 "Total previous unpaid amount imported in the database",
			 "Total meter amortization balance imported in the database",
			 "Total reconnection fee imported in the database"
	 };
	 
	 Integer[] imageId={
			 R.drawable.numrows,
			 R.drawable.penalty,
			 R.drawable.water_consumption,
			 R.drawable.meter_bal,
			 R.drawable.t_reconnection
	 };
	 importexportAdapter importdb;
	 //String sfname, slname;
	 //FileOperations file=new FileOperations();
	 
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.importdb_layout, frameLayout);
		
		/**
		 * Setting title and itemChecked  
		 */
		mDrawerList.setItemChecked(4, true);
		setTitle(listArray[4]);
        //setContentView(R.layout.importdb_layout); 
        edittext = (EditText)findViewById(R.id.importdbtext);
       
        mydb=new DBHelper(this);
        
        count_all_imported();
    }

	private void count_all_imported() {
		Cursor rs = mydb.count_all_import();
			rs.moveToFirst();
		     countall = rs.getString(rs
					.getColumnIndex("count_all"));
			   if(!countall.equals("0") ){
				     penalty = rs.getString(rs
								.getColumnIndex("penalty"));
				     prev_amount = rs.getString(rs
								.getColumnIndex("prev_amount"));
				     meter_bal= rs.getString(rs
								.getColumnIndex("meter_balance"));
				     reconnection= rs.getString(rs
								.getColumnIndex("reconnection"));
				   
				     
		        }
			   if (!rs.isClosed()) {
					rs.close();
				}
        
		 values[0]=NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(countall));
		 values[1]=NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(penalty));
		 values[2]=NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(prev_amount));
		 values[3]=NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(meter_bal));
		 values[4]=NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(reconnection));
		 importdb = new importexportAdapter(this, name,descrip, imageId,values);
		 list=(ListView)findViewById(R.id.importdbs);
		 list.setAdapter(importdb);
		
	}
 
    public void getfile(View view){ 
    	Intent intent1 = new Intent(this, FileChooser.class);
        startActivityForResult(intent1,REQUEST_PATH);
    }
 // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
    	if (requestCode == REQUEST_PATH){
    		if (resultCode == RESULT_OK) { 
    			curFileName = data.getStringExtra("GetFileName"); 
    			curPath = data.getStringExtra("GetPath"); 
            	edittext.setText(curPath+"/"+curFileName);
    		}
    	 }
    }
    
    public void importdb(View v){
    	
    	path=edittext.getText().toString();
    	String filenameArray[]=path.split("\\.");
    	String extension = filenameArray[filenameArray.length-1];
    	if(!countall.equals("0")){
    		Toast.makeText(getApplicationContext(), "Please empty first the customer table.:)", Toast.LENGTH_SHORT).show();
    	}else if (""== path || path.trim().length() == 0 || path==null) {
    		Toast.makeText(getApplicationContext(), "Please browse a data to import", Toast.LENGTH_SHORT).show();
		}else if(!extension.equals("wbs")){
			Toast.makeText(getApplicationContext(), "This "+extension+" extension is not valid.", Toast.LENGTH_SHORT).show();
		}else {
			ImportDatabaseCSVTask task=new ImportDatabaseCSVTask();
			task.execute();
		}
    }
    
    private boolean check_date(String Date){
    	
		String mydatearray[]=Date.split("\\-");
		int import_month=Integer.parseInt(mydatearray[1]);
		int import_year=Integer.parseInt(mydatearray[0]);
		
		Date now = new Date();
	    String datenow = new SimpleDateFormat("yyyy-M-dd").format(now);//yyyy-dd-MM
	    String mydatearray1[]=datenow.split("\\-");
		int system_month=Integer.parseInt(mydatearray1[1]);
		int system_year=Integer.parseInt(mydatearray1[0]);
		
		boolean result;
		if((import_month==12 && system_month==1)||(import_month==12 && system_month==12)){
			if(((import_year+1)==system_year)|| import_year==system_year){
				result=true;
			}else{
				result=false;
			}
			
		}else if((import_month+1)==(system_month)|| import_month==system_month){
			if(import_year==system_year){
				result=true;
			}else{
				result=false;
			}
		}else{
			result=false;
		}
		
		return result;
    	
    	
    }
    
    private class ImportDatabaseCSVTask extends AsyncTask<String, Void, Boolean>{
	    private final ProgressDialog dialog = new ProgressDialog(ImportDB.this);
	    
	    @Override
	    protected void onPreExecute() {

	        this.dialog.setMessage("Importing database...");
	        this.dialog.show();

	    }
	    protected Boolean doInBackground(final String... args){

	    	
	    			  File file = new File(path);
					  try {
						CSVReader reader = new CSVReader(new FileReader(file));
						String [] nextLine;
						

						try {
						nextLine=reader.readNext();	
						if(!nextLine[0].equalsIgnoreCase(" idcustomer")|| !nextLine[1].equalsIgnoreCase("customer_info_id")) {
							//System.out.println(nextLine[0]+" "+nextLine[1]);						
							  error=0;
					    	  importsucc=false;
						
						}else{
							while ((nextLine = reader.readNext()) != null) {
								
															
								 String customerid=nextLine[0];
								 String customer_info_id=nextLine[1];
								 String fname=nextLine[2];
								 String mname=nextLine[3];
								 String lname=nextLine[4];
								 String corpname=nextLine[5];
								 String corptype=nextLine[6];
								 String barangay=nextLine[7];
								 String barangay_id=nextLine[8];
								 String meter_id=nextLine[9];
								 String meterno=nextLine[10];
								 String prev_amount=nextLine[11];
								 String pres_reading_date=nextLine[12];
								 String pres_reading_val=nextLine[13];
								 String curmeterbal=nextLine[14];
								 String penalty_amount=nextLine[15];
								 String reconnection=nextLine[16];
								 String purok_no=nextLine[17];
								
								 
							    if(customerid.equalsIgnoreCase("idcustomer"))
							    {
							    	
							    }else if(customerid.equalsIgnoreCase("payments_rate") && !check_date(nextLine[3])){
									   error=3;
									   importsucc=false;
									   break;
							    }else{
							    	
							    
								    	 if(customerid.equalsIgnoreCase("payments_rate")){
											 										 
											 values2=mydb.updatedue_datePenalty("1", customer_info_id, fname);
											 values3=mydb.updateWaterformulaValues("1", corptype, corpname, lname);
										 }
								    	 if(!customerid.equalsIgnoreCase("payments_rate")){
								    		 if(penalty_amount.equals("")){
								    			 penalty_amount="0";
								    		 }
								    		 value=mydb.insertCustomer(new Customer(customerid,customer_info_id,fname, mname, lname, corpname,
												 barangay,  corptype, meterno, meter_id,
												 Double.parseDouble(prev_amount) , pres_reading_date, Double.parseDouble(pres_reading_val), Double.parseDouble(curmeterbal),
												 Double.parseDouble(penalty_amount), Double.parseDouble(reconnection), Integer.parseInt(barangay_id),0,purok_no));
								    	 }
							    	
							    }
							    	
							  }   
							    
							}
						} catch (IOException e) {
							e.printStackTrace();
							error=1;
							importsucc=false;
						}
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						
						error=2;
						importsucc=false;
					}
					  if(value>=1 && values2==true && values3==true)
				    	{
				    		importsucc=true;
				    	}

	    	return importsucc;
	    }
	    
	    @Override
	    protected void onPostExecute(final Boolean success)	{

	        if (this.dialog.isShowing()){
	            this.dialog.dismiss();
	        }
	        if (success){
	        	
	        		
	        	
	        	Toast.makeText(getApplicationContext(), "Data successfully inserted to the database", Toast.LENGTH_SHORT).show();
	    		edittext.setText("");
	    		count_all_imported();
	    		file.writeLogs(sfname+" "+slname+" export data from the system on "+file.dateTime()+" "+
	         			   ": Number of rows:"+values[0]+" Penalty : "+values[1]+
	         			   " Previous Amount:"+values[2]+" Meter balance: "+values[3]+
	         			   " Reconnection: "+values[4]);
	        }
	        else {
	        	if(error==0){
	        		  Toast.makeText(getApplicationContext(), "This type of wbs file is not valid!!", Toast.LENGTH_SHORT).show();
			    	 
	        	}else if(error==2){
	        		   Toast.makeText(getApplicationContext(), "Sorry! File not found.", Toast.LENGTH_LONG).show();
	        		   edittext.setText("");
	        	}else if(error==3){
	        		 Toast.makeText(getApplicationContext(), "Please check the system date of wbs reader and wbs system.", Toast.LENGTH_SHORT).show();
		           
	        	}else{
	        		 Toast.makeText(ImportDB.this, "Something Wrong Import failed!", Toast.LENGTH_SHORT).show();
	        	}
	        
	        }
	    }
	}
}
