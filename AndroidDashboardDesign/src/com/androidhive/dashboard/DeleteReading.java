package com.androidhive.dashboard;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidhive.dashboard.R;


import com.database.adapter.CustomerAdapter;
import com.database.helper.DBHelper;
import com.database.model.Customer;

public class DeleteReading extends Activity implements OnItemClickListener,OnItemSelectedListener {
	 /** Called when the activity is first created. */
	ArrayList<Customer> unreadmeter = new ArrayList<Customer>();

	CustomerAdapter adapter;
	DBHelper db;
	String querydata="";
	TextView numrow,nonumrow;
	boolean color=false;
	String sfname,slname;
    ProgressBar loading;
    Customer custom;
    String cname,reading_amount,account_name,barangay,connection_type,
    	corporate,fname,lname, mname,
    	prev_reading_value,prev_reading_date,meter_no,
    	pres_reading_value,pres_reading_date,penalty_amount
    	,reconnection,prev_amount,cur_meter_bal,customer_info_id,wpenalty,wduedate;
    int water_consumption;
    double amount_due;
    String customerid;
    
    
  //Bluetooth variables
    Boolean result=false,ready=false;
    int bytesAvailable;
  	BluetoothAdapter mBluetoothAdapter;
  	BluetoothSocket mmSocket;
  	BluetoothDevice mmDevice;
  	
  	
  	OutputStream mmOutputStream;
  	InputStream mmInputStream;
  	Thread workerThread;
  	
  	byte[] readBuffer;
  	int readBufferPosition;
  	int counter;
  	volatile boolean stopWorker;

  	private int mWidth;
  	private int mHeight;
  	private String mStatus;
  	private BitSet dots;
  	//end of bluetooth variables
  
  	String searchBarangay="Select Barangay";
    String searchStandPipe="Select Stand Pipe";
    
    Spinner brgyDropDownSpinner, standPipeSpinner;
    ArrayAdapter<String> barangayDropDownAdapter;
	ArrayAdapter<String> standPipeDropDownAdapter;
 
	FileOperations file=new FileOperations();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deletereading_layout);
        
        SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
	     sfname=settings.getString("fname", "").toString();
	     slname=settings.getString("lname", "").toString();
        
            db = new DBHelper(this);
            numrow=(TextView)findViewById(R.id.result_found);
            nonumrow=(TextView)findViewById(R.id.no_result_found);
            loading=(ProgressBar)findViewById(R.id.progressBar1);

            brgyDropDownSpinner = (Spinner) findViewById(R.id.barangay_dropdown);
            standPipeSpinner = (Spinner) findViewById(R.id.standpipe);
             
             // Spinner click listener
            brgyDropDownSpinner.setOnItemSelectedListener(this);
            standPipeSpinner.setOnItemSelectedListener(this);
        
            querysqlite();
      		dropdownBarangay();
     		
     		
    }

	public void querysqlite() {
		List<Customer> customer = db.select_all_readorunreadcustomer();
		for (Customer c : customer) {
			unreadmeter.add(c);

		}
		if(unreadmeter.size()<=0){
			nonumrow.setVisibility(View.VISIBLE);
			nonumrow.setText("No Data Found!!!");
		}else{
			nonumrow.setVisibility(View.GONE);
		}
		
		//nonumrow.setText("Searching...");
		loading.setVisibility(View.GONE);
		adapter = new CustomerAdapter(this, R.layout.unreadmeter_row,
				unreadmeter);
		
		adapter.setact("delete");// this will tell that the data must list as delete
		ListView dataList = (ListView) findViewById(R.id.unreadmeterlist);
		dataList.setOnItemClickListener(this);
	
		dataList.setAdapter(adapter);
		dataList.setTextFilterEnabled(true);
		
	
		
		
	}
      	 
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		
		
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() 
        {
        	
            @Override
            public boolean onQueryTextChange(String newText) 
            {
                // this is your adapter that will be filtered
            	
            	standPipeSpinner.setSelection(standPipeDropDownAdapter.getPosition("Select Stand Pipe"));
            	brgyDropDownSpinner.setSelection(barangayDropDownAdapter.getPosition("Select Barangay"));
            	searchListView(newText);
            	System.out.println("query on search box "+newText);
                return true;
                
            }
            @Override
            public boolean onQueryTextSubmit(String query) 
            {
                // this is your adapter that will be filtered
                adapter.getFilter().filter(query);
                System.out.println("on query submit: "+query);
             	
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		/*case R.id.print:
			Intent intent = new Intent(getApplicationContext(),PrintReport.class);
        	startActivity(intent);
			return true;
		case R.id.deleteReading:
			Intent intent2 = new Intent(getApplicationContext(),PrintReport.class);
        	startActivity(intent2);
			return true;*/
		case R.id.action_search:
			// search action
			return true;
		/*case android.R.id.home:
			
			onBackPressed();
			return true;*/
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		custom=adapter.getItem(position);
		cname = custom.getCorporate()+custom.getFname()+" "+custom.getLname();
		customerid=custom.getCustomerid();

			AlertDialog alert = new AlertDialog.Builder(DeleteReading.this)
			.create();
			alert.setTitle("Are you sure?");
			alert.setMessage("Are you sure want to delete the record of "+cname+" ? " );
			//alert.setContentView(layoutResID)
	

			alert.setButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						//Toast.makeText(getApplicationContext(), sfname+" "+slname, Toast.LENGTH_SHORT).show();
					}
			});
			alert.setButton2("Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
						int result=db.updateStat(Integer.parseInt(customerid), 0);
						
						if(result==1){
							int deletereading=db.deleteReading(customerid);
							if(deletereading==1){
								file.writeLogs(sfname+" "+slname+" delete the reading saved of cust. name: "+cname+" " +
										" connection type: "+custom.getConnection_type()+" " +
										"mtr no: "+custom.getMeter_no()+" " +
										" on "+file.dateTime());
								
								Toast.makeText(getApplicationContext(),"Delete Successfully!!!", Toast.LENGTH_SHORT).show();
								Intent i = new Intent(getApplicationContext(), DeleteReading.class);
								startActivity(i);
							}else{
								Toast.makeText(getApplicationContext(),"System error on deleting reading!!!", Toast.LENGTH_SHORT).show();
							}
							
						}else{
							Toast.makeText(getApplicationContext(),"System error!!!", Toast.LENGTH_SHORT).show();
						}
						
						
					}
			});
			alert.show();
		
	
	}
	
	@Override
    public void onBackPressed() {
		Intent i = new Intent(getApplicationContext(), ReadMeterActivity.class);
		startActivity(i);
	}
	
	private void dropdownBarangay(){
		
		   //Spinner Drop down elements
		        List<String> categories = new ArrayList<String>();
		        categories.add("Select Barangay");
		        Cursor rs = db.select_all_barangay();
		        while(rs.moveToNext()){
		        	categories.add(rs.getString(rs
										.getColumnIndex("brgy")));
		        }
		        

				List<String> categories2 = new ArrayList<String>();
				categories2.add("Select Stand Pipe");
		        for(int x=1;x<=60;x++){
		        	categories2.add("T-"+x);
		        	//categories2.add("Automobile");
		        }
		        
		        // Creating adapter for spinner
		        barangayDropDownAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
				
				// Drop down layout style - list view with radio button
		        barangayDropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				// attaching data adapter to spinner
		        brgyDropDownSpinner.setAdapter(barangayDropDownAdapter);
				
				 // Creating adapter for spinner
				standPipeDropDownAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories2);
				
				// Drop down layout style - list view with radio button
				standPipeDropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				// attaching data adapter to spinner
				standPipeSpinner.setAdapter(standPipeDropDownAdapter);
				//spinner2.setSelection(standPipeDropDownAdapter.getPosition("T-49"), true);
			}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		
		String newText = parent.getItemAtPosition(position).toString();
		
		switch(parent.getId()) {
     case R.id.barangay_dropdown:
         // Do stuff for barangay_dropdown
     	searchBarangay=newText;
         break;
     case R.id.standpipe:
         // Do stuff for stand pipe
     	searchStandPipe=newText;
     	break;
     }
		if((searchBarangay=="Select Barangay") && (searchStandPipe=="Select Stand Pipe")){
			searchListView("");
		}else if(!(searchBarangay=="Select Barangay") && (searchStandPipe=="Select Stand Pipe")){
			searchListView(searchBarangay+"|-");
		}else if((searchBarangay=="Select Barangay") && !(searchStandPipe=="Select Stand Pipe")){
			searchListView(searchStandPipe+"|-");
		}
		else if(!(searchBarangay=="") && !(searchStandPipe=="")){
			searchListView(searchBarangay+"|"+searchStandPipe+"|"+"-");
		}else {
			searchListView("");
		}
		
		
	}

	private void searchListView(String newText) {
		 adapter.getFilter().filter(newText);
      System.out.println("on text chnge text: "+newText);
      loading.setVisibility(View.VISIBLE);
      nonumrow.setVisibility(View.VISIBLE);
      nonumrow.setText("Searching...");
    
      
      adapter.getFilter().filter(newText, new Filter.FilterListener() {
		    public void onFilterComplete(int count) {
		         numrow.setText("Result found: "+ Integer.toString(count));
		       
		         if(adapter.isEmpty()){
		 			nonumrow.setVisibility(View.VISIBLE);
		 			nonumrow.setText("No Result Found!!!");
		 		}else{
		 			nonumrow.setVisibility(View.GONE);
		 		}
		         loading.setVisibility(View.GONE);
		    }
		});
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}



}
