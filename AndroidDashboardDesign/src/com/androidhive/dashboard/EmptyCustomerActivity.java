package com.androidhive.dashboard;

import java.text.NumberFormat;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidhive.dashboard.R;

import com.database.adapter.importexportAdapter;
import com.database.helper.DBHelper;
import com.navigation.drawer.activity.BaseActivity;

public class EmptyCustomerActivity extends BaseActivity  {
	 /** Called when the activity is first created. */
	DBHelper mydb;
	TextView numrow;
	int count;
	Button button;
	 ListView list;
	 String[] name={
			 "No. of rows",
			 "Total Penalty",
			 "Total Previous Amount Unpaid",
			 "Meter Balance",
			 "Reconnection fee"
	 };
	 String[] descrip={
			 "Total number of rows in the database",
			 "Total Amount of penalty in the database",
			 "Total previous unpaid amount in the database",
			 "Total meter amortization balance in the databse",
			 "Total reconnection fee in the databse",
	 };
	 
	 Integer[] imageId={
			 R.drawable.numrows,
			 R.drawable.penalty,
			 R.drawable.water_consumption,
			 R.drawable.meter_bal,
			 R.drawable.t_reconnection
	 };
	 importexportAdapter importdb;
	 String countall="0",penalty="0",prev_amount="0", meter_bal="0",reconnection="0";
	 String[] values={"0","0","0","0","0"};
	 
	 //String sfname, slname;
	 //FileOperations file=new FileOperations();
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.emtycustomertable_layout, frameLayout);
		
		/**
		 * Setting title and itemChecked  
		 */
		mDrawerList.setItemChecked(3, true);
		setTitle(listArray[3]);
       // setContentView(R.layout.emtycustomertable_layout);
        button=(Button)findViewById(R.id.emptytable);
               
        mydb=new DBHelper(this);
        count_all_imported();
        
       
        if(countall.equals("0")){
        	button.setClickable(false);
        }
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
			   
			     
	        }else{
	        	penalty="0";
	        	prev_amount="0";
	        	meter_bal="0";
	        	reconnection="0";
	        	
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
			 list=(ListView)findViewById(R.id.emptydbs);
			 list.setAdapter(importdb);
	}
    
    public void emptycustomertable(View v){
    	
    	AlertDialog alert = new AlertDialog.Builder(EmptyCustomerActivity.this)
		.create();
		alert.setTitle("Are you sure?");
		alert.setMessage("Are you sure you want to empty customer table?");

		alert.setButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
		});
		alert.setButton2("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					DeleteAllCustomer task=new DeleteAllCustomer();
					task.execute();
					
				}
		});
		alert.show();
    }
    	
    
    private class DeleteAllCustomer extends AsyncTask<String, Void, Boolean>{
	    private final ProgressDialog dialog = new ProgressDialog(EmptyCustomerActivity.this);
	    
	    @Override
	    protected void onPreExecute() {

	        this.dialog.setMessage("Deleting customer rows...");
	        this.dialog.show();

	    }
	    protected Boolean doInBackground(final String... args){
            Boolean numrowdel;
	    	int deletedrow=mydb.drop_all_customer();
	    	if(deletedrow>=1){
	    		numrowdel=true;
	    	}else{
	    		numrowdel=false;
	    	}
	    		
	    		
	    	return numrowdel;
	    }
	    
	    @Override
	    protected void onPostExecute(final Boolean success)	{

	        if (this.dialog.isShowing()){
	            this.dialog.dismiss();
	        }
	        if (success){

        		file.writeLogs(sfname+" "+slname+" empty record data of the system on "+file.dateTime()+" ");
	        	
	        	Toast.makeText(getApplicationContext(), "Customer table is now empty", Toast.LENGTH_SHORT).show();
	        	count_all_imported();
	        }
	        else {
	        
	        		
		            Toast.makeText(getApplicationContext(), "Something wrong!", Toast.LENGTH_SHORT).show();
	        	}
	        
	        }
	    }
}

