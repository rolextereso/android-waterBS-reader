package com.androidhive.dashboard;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidhive.dashboard.R;

import com.database.helper.DBHelper;
import com.database.model.Reading;

public class SaveReading extends Activity  {
	 /** Called when the activity is first created. */
	TextView cname,connection_type,meternum,readingdate,prevreadingvalue;
	DBHelper mydb;
	int count_user;
	TextView readingvalue;
	String value,id, nowAsString,meter_reading_date,sfname,slname,reg_date;
	String reading_amount,prev_reading_value,prev_reading_date;
	Button save,proceedprint;
	Calendar calendar = Calendar.getInstance();
	Date now;
	LinearLayout systemdateinvalid;

    int bytesAvailable;
	
	//Bluetooth variables
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
	
	public static String searchBarangay="Select Barangay";
	public static String searchStandPipe= "Select Stand Pipe";
	
	String customername;
	String connectiontype;
	String meternumber;
	
	FileOperations file=new FileOperations();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savereadmeter);
        SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
	    sfname=settings.getString("fname", "").toString();
	    slname=settings.getString("lname", "").toString();
	     
        mydb=new DBHelper(this);
    
        cname=(TextView)findViewById(R.id.customer_name);
        connection_type=(TextView)findViewById(R.id.customer_connection_type);
        meternum=(TextView)findViewById(R.id.customer_meter_num);
        readingdate=(TextView)findViewById(R.id.reading_date);
        readingvalue=(TextView)findViewById(R.id.waterconsumptionread);
        prevreadingvalue=(TextView)findViewById(R.id.PrevReadingValue);
        systemdateinvalid=(LinearLayout)findViewById(R.id.systemdateinvalid);
        
        save=(Button)findViewById(R.id.btnreadsave);
        proceedprint=(Button)findViewById(R.id.btngotoprint);
        save.setVisibility(View.VISIBLE);
        systemdateinvalid.setVisibility(View.GONE);
        proceedprint.setVisibility(View.GONE);
        
        now = new Date();
        //Date alsoNow = Calendar.getInstance().getTime();
        nowAsString = new SimpleDateFormat("MMMMM dd, yyyy").format(now);//yyyy-dd-MM
        meter_reading_date = new SimpleDateFormat("yyyy-MM-dd").format(now);
        reg_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(now);
        
        readingdate.setText("Reading Date: "+nowAsString);
        
    	Bundle extras = getIntent().getExtras();
		if (extras != null) {
			id = extras.getString("id");
			customername= extras.getString("cname");
			connectiontype= extras.getString("connectiontype");
			meternumber= extras.getString("meternum");
			
			searchBarangay= extras.getString("searchBarangay");
			searchStandPipe= extras.getString("searchStandPipe");
			
			
			if(id.length()!=0 || !id.isEmpty()){
			   
			    cname.setText(customername);
			    connection_type.setText(connectiontype);
			    meternum.setText("Meter Number: "+meternumber);
	
			}

		}
		getdata();
		prevreadingvalue.setText(prev_reading_value);
		
		String mydatearray[]=prev_reading_date.split("\\-");
		Boolean valid_sys_date=validate_date_of_pres(Integer.parseInt(mydatearray[0]),Integer.parseInt(mydatearray[1])-1, Integer.parseInt(mydatearray[2]),now);
		if(valid_sys_date==false){
			save.setVisibility(View.GONE);
			readingvalue.setClickable(false);
			readingvalue.setFocusable(false);
			systemdateinvalid.setVisibility(View.VISIBLE);
		}
	
    }
    
    public void getdata(){
    	Cursor columns = mydb.customer_leftjoin_reading(Integer.parseInt(id));//Integer.parseInt(id)
    	columns.moveToFirst();
    	prev_reading_value = columns.getString(columns
				.getColumnIndex("prev_reading_value"));
    	prev_reading_date = columns.getString(columns
				.getColumnIndex("prev_reading_date"));
		   
		if (!columns.isClosed()) {
			columns.close();
	    }
		
		
    }
	
	@SuppressWarnings("deprecation")
	public void save(View view){
	            
				getdata();
	                   
				value = readingvalue.getText().toString();     
					
				if (null == value || value.trim().length() == 0) {
					readingvalue.setError("Must be filled");
					readingvalue.requestFocus();
				
				}else if(Double.parseDouble(prev_reading_value)> Double.parseDouble(value) && Double.parseDouble(prev_reading_value)< 99000.00){
					Toast.makeText(getApplicationContext(), "It must be greater than "+prev_reading_value, Toast.LENGTH_SHORT).show();
				}else{
					int waterconsumption=0;
					//if(Integer.parseInt(prev_reading_value)>=9600 && (Double.parseDouble(value)>=1 && Double.parseDouble(value)<=200)){
					if(Integer.parseInt(prev_reading_value)>=99000 && (Double.parseDouble(value)>=1 && Double.parseDouble(value)<=1000)){
						//waterconsumption=(9999-Integer.parseInt(prev_reading_value))+ Integer.parseInt(value);
						waterconsumption=(100000-Integer.parseInt(prev_reading_value))+ Integer.parseInt(value);
					}else{
						waterconsumption=(Integer.parseInt(value)-Integer.parseInt(prev_reading_value));
					}
					if(waterconsumption<0){
						AlertDialog alert = new AlertDialog.Builder(SaveReading.this)
						.create();
						alert.setTitle("Attention!!!");
						alert.setMessage("Please check your reading value because water consumption is in negative numbers." );
						

						alert.setButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
						});
					
						alert.show();
					}else{
						Cursor reading = mydb.compute_water_amount(waterconsumption);
						reading.moveToFirst();
					    reading_amount = reading.getString(reading
								.getColumnIndex("reading_value"));
					    
					    if (!reading.isClosed()) {
							reading.close();
					    }
					    
					    AlertDialog alert = new AlertDialog.Builder(SaveReading.this)
						.create();
						alert.setTitle("Are you sure?");
						alert.setMessage("Are you sure this is the right reading?\nReading Value: "+ value+"\n" +
								"Water Consumed.: "+waterconsumption );
						

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
									
									System.out.println(reg_date);
									int success=mydb.insertReading(new Reading(id,meter_reading_date,value,Double.parseDouble(reading_amount),sfname+" "+slname,reg_date));									
									
									if(success>=1){
										
										
										int result=mydb.updateStat(Integer.parseInt(id), 1);
															
										if(result==1){
											file.writeLogs(sfname+" "+slname+" save reading of cust. name: "+customername+" " +
													" connection type: "+connectiontype+" " +
													"mtr no: "+meternumber+" " +
													" prev reading: "+prev_reading_value+" "+
													" pres reading:"+ value+" on "+file.dateTime());
											
											save.setVisibility(View.GONE);
											proceedprint.setVisibility(View.VISIBLE);
											readingvalue.setFocusable(false);
											Toast.makeText(getApplicationContext(),"Successfully save!!!", Toast.LENGTH_SHORT).show();
										}

									}else{
										Toast.makeText(getApplicationContext(), "Error in saving the data", Toast.LENGTH_SHORT).show();
									}
									
								}
						});
						alert.show();
					}

				}
	
			}
	
		@SuppressWarnings("deprecation")
		public boolean validate_date_of_pres(int year, int month, int day,Date now){
			//now=calendar.getTime();		
		    calendar.set(year, month, day);
			Date setDate=calendar.getTime();
			
			
			
			if(now.after(setDate)){
				//if(now.getMonth()==setDate.getMonth() && now.getYear()!=setDate.getYear()){
					return true;
				//}else if(now.getMonth()!=setDate.getMonth()){
				//	return true;
					
			    //}else{
				//	return false;
				//}
				
			}else{
				return false;
			}
			
		}
		
		public void ProceedRead(View v){
			Intent intent = new Intent(getApplicationContext(),ReadMeterActivity.class);
	    	startActivity(intent);
		}
		
		@Override
	    public void onBackPressed() {
			
			
			Intent intent = new Intent(getApplicationContext(), UnreadMeterActivity.class);
			startActivity(intent);
		}
	
	
	
}
