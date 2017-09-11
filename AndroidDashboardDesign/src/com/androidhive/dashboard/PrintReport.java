package com.androidhive.dashboard;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidhive.dashboard.R;

import com.database.adapter.CustomerAdapter;
import com.database.helper.DBHelper;
import com.database.model.Customer;

public class PrintReport extends Activity implements OnItemSelectedListener {
	 /** Called when the activity is first created. */
	ArrayList<Customer> readmeter = new ArrayList<Customer>();
	Date now;
	CustomerAdapter adapter;
	DBHelper db;
	String querydata="",barangaydropdown,barangay,account_name;
	String customer_info_id,pres_reading_value;
	String nowAsString;
	String sbarangay;
	Button printreport;
	
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
  	Spinner dropdownbarangay;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupbybarangay);
        db = new DBHelper(this);
        // Spinner element
      	dropdownbarangay = (Spinner) findViewById(R.id.barangaydrop);
      	printreport=(Button)findViewById(R.id.sortbarangay);
      	ImageView icon=(ImageView)findViewById(R.id.systemicon);
        icon.setImageResource(R.drawable.icon_print2);
          
          // Spinner click listener
      	dropdownbarangay.setOnItemSelectedListener(this);
          
      	query_barangay();	
      	now = new Date();
      	
      	nowAsString = new SimpleDateFormat("MMMMM dd, yyyy").format(now);//yyyy-dd-MM
    }
    
    public void query_barangay(){
    	
    	Cursor barangay = db.select_barangay("");
    	List<String> categories = new ArrayList<String>();
    	barangay.moveToFirst();
    	categories.add("All Barangay");
    	for(int x=1;x<=barangay.getCount();x++){

    		  barangaydropdown = barangay.getString(barangay
    					.getColumnIndex(DBHelper.CUS_COLUMN_BRGY));
    	      categories.add(barangaydropdown);
    	      barangay.moveToNext();
    			
    		
        }	 
        // Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// attaching data adapter to spinner
		dropdownbarangay.setAdapter(dataAdapter);
    }
    
    @SuppressWarnings("deprecation")
	public void printreport(View v){
    	AlertDialog alert = new AlertDialog.Builder(PrintReport.this)
		.create();
		alert.setTitle("Are you sure?");
		String message="";
		if(barangay.equals("all")){
			message=" all record of the Water Consumption";
		}else{
			message=" the Water Consumption Report of barangay "+barangay;
		}
		alert.setMessage("Are you sure want to print "+message+"? " );
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
					if(result==false){
						try {
							findBT();
							openBT();
							sendData();
						
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					

				}
		});
		alert.show();
    }

	private class PrintBill extends AsyncTask<String, Void, Boolean>{
	    private final ProgressDialog dialog = new ProgressDialog(PrintReport.this);
	    
	    @Override
	    protected void onPreExecute() {

	        this.dialog.setMessage("Printing...");
	        this.dialog.setCanceledOnTouchOutside(false);
	        this.dialog.show();

	    }
	    protected Boolean doInBackground(final String... args){
	    	
	    	try {
	    		
	    		
	    		 
	    		byte[] arrayOfByte1 = { 27, 33, 0 };
				byte[] format = { 27, 33, 0 };
				format[2] = ((byte)(0x10 | arrayOfByte1[2]));
				format[2] = ((byte)(0x20 | arrayOfByte1[2]));
			    mmOutputStream.write(format);
			   
	    		String msg0="   HINUNANGAN\n";
	    		mmOutputStream.write(msg0.getBytes());
	    		String msg1="             WATERWORKS SYSTEM \n";
	    			   msg1+="         Municipality of Hinunangan\n";
	    			   msg1+="         Hinunangan, Southern Leyte\n";
	    			   msg1+="         TIN: 000-044-819-000 VAT\n";
	    			
	    			   
	    		format[2] = ((byte)(0x1 | arrayOfByte1[2]));
			    mmOutputStream.write(format);
			    mmOutputStream.write(msg1.getBytes());
			    mmOutputStream.write(PrinterCommands.INIT);
	    		mmOutputStream.write(PrinterCommands.SET_LINE_SPACING_30);
	    		
	    		       	
				String msg3="______________________________\n";
				   msg3+="    Water Consumption Report \n";
				   msg3+="------------------------------\n";
				   msg3+="Date Printed: "+nowAsString+"\n";
				  
				   mmOutputStream.write(msg3.getBytes());
				   
				   
				   Cursor columns = db.customer_leftjoin_readingreport(barangay);
				   columns.moveToFirst();
				   sbarangay="";
				   for(int x=1;x<=columns.getCount();x++){
					   
					    customer_info_id = columns.getString(columns
								.getColumnIndex("customer_info_id"));
				    	String fname = columns.getString(columns
								.getColumnIndex("fname"));
				    	String lname = columns.getString(columns
								.getColumnIndex("lname"));
				    	String mname = columns.getString(columns
								.getColumnIndex("mname"));
				    	String corporate = columns.getString(columns
								.getColumnIndex("corporate"));
				    	
				    	
				    	if(fname.isEmpty()){
				    		account_name=corporate;
				    	}else{
				    		account_name=fname+" "+mname+" "+lname;
				    	}
				    	barangay = columns.getString(columns
								.getColumnIndex("barangay"));
				    	pres_reading_value = columns.getString(columns
								.getColumnIndex("pres_reading_value"));
				    
				 
		
				 if(!barangay.equals(sbarangay)){
					 
			         String  msg5=" ****** "+barangay+" ****** \n";
					 msg5+="------------------------------\n";
					 sbarangay=barangay;
					 mmOutputStream.write(msg5.getBytes());	
				 }
				  	  
				
				         String  msg ="Name: "+account_name+"\n";
						 msg+="ACCT #: "+customer_info_id+" \n";
						 msg+="Reading Val.: "+pres_reading_value+" \n";
						 msg+="------------------------------\n";
						
				    	
						   columns.moveToNext();
						   mmOutputStream.write(msg.getBytes());	
						 
				   }
				   String  msg6 ="\n\n";
				   		   msg6+="________________________________\n";
				   		   msg6+="  Admin.Name over Signature ";
				   		   msg6+="\n\n";
				   		   msg6+="________________________________\n";
				   		   msg6+=" Witness Name over Signature\n\n";
				   		 mmOutputStream.write(msg6.getBytes());	
					
				   
			
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	    	return true;
	    }
	   
	  
	    
	    @Override
	    protected void onPostExecute(final Boolean success)	{

	        if (this.dialog.isShowing()){
	            this.dialog.dismiss();
	        }
	        if (success){
	        	
	        	
	        	try {
	  
	    					Toast.makeText(getApplicationContext(), "Print Succesfully...", Toast.LENGTH_SHORT).show();
	    	        		closeBT();
	    	        		//finish();
	    
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }
	        else {
	        	Toast.makeText(getApplicationContext(), "Error in Printing..", Toast.LENGTH_SHORT).show();
	        
	        }
	    }
	}
	
	public void findBT() {
		
		try {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
			if (mBluetoothAdapter == null) {
				Toast.makeText(getApplicationContext(), "No Bluetooth Adapter is available", Toast.LENGTH_SHORT).show();
				
			}
			

			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBluetooth = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBluetooth, 0);
			}

			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
					.getBondedDevices();
			if (pairedDevices.size() > 0) {
				
					for (BluetoothDevice device : pairedDevices) {
					  //if(!device.getName().equals("MP300")){
							// MP300 is the name of the bluetooth printer device
							if (device.getName().equals("MP300")) {
								mmDevice = device;
								
								break;
							}
							//Toast.makeText(getApplicationContext(), "MP300 Printer not found", Toast.LENGTH_SHORT).show();
						 //break;
					  //}
				}
				
			}
			
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	/*
	 * Tries to open a connection to the bluetooth printer device
	 */
  public void openBT() throws IOException {
		try {
			// Standard SerialPortService ID  00001101-0000-1000-8000-00805f9b34fb
			UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
			mmSocket.connect();
			mmOutputStream = mmSocket.getOutputStream();
			mmInputStream = mmSocket.getInputStream();
			
			beginListenForData();
			Toast.makeText(getApplicationContext(), "Bluetooth Printer is ready to use", Toast.LENGTH_SHORT).show();
			result=true;
		
		} catch (NullPointerException e) {
			result=false;
			Toast.makeText(getApplicationContext(), "Printer not found", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			
		} catch (Exception e) {
			result=false;
			Toast.makeText(getApplicationContext(), "Unable to connect the printer", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	/*
	 * After opening a connection to bluetooth printer device, 
	 * we have to listen and check if a data were sent to be printed.
	 */
   public void beginListenForData() {
		try {
			final Handler handler = new Handler();
			
			// This is the ASCII code for a newline character
			final byte delimiter = 10;

			stopWorker = false;
			readBufferPosition = 0;
			readBuffer = new byte[1024];
			
			workerThread = new Thread(new Runnable() {
				public void run() {
					while (!Thread.currentThread().isInterrupted()
							&& !stopWorker) {
						
						try {
							
							bytesAvailable = mmInputStream.available();
							if (bytesAvailable > 0) {
								byte[] packetBytes = new byte[bytesAvailable];
								mmInputStream.read(packetBytes);
								for (int i = 0; i < bytesAvailable; i++) {
									byte b = packetBytes[i];
									if (b == delimiter) {
										byte[] encodedBytes = new byte[readBufferPosition];
										System.arraycopy(readBuffer, 0,
												encodedBytes, 0,
												encodedBytes.length);
										final String data = new String(
												encodedBytes, "US-ASCII");
										readBufferPosition = 0;

										handler.post(new Runnable() {
											public void run() {
												Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
											}
										});
									} else {
										readBuffer[readBufferPosition++] = b;
									}
								}
							}
							
						} catch (IOException ex) {
							stopWorker = true;
						}
						
					}
				}
			});

			workerThread.start();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
   
	/*
	 * This will send data to be printed by the bluetooth printer
	 */
	public void sendData() {
		
		if(result){
			PrintBill task=new PrintBill();
			task.execute();
		}else{
			//Toast.makeText(getApplicationContext(), "Connect first your device", Toast.LENGTH_SHORT).show();
		}
	
	}
	//	end find and open bluetooth below
public String convertBitmap(Bitmap inputBitmap) {

	    mWidth = inputBitmap.getWidth();
	    mHeight = inputBitmap.getHeight();

	    convertArgbToGrayscale(inputBitmap, mWidth, mHeight);
	    mStatus = "ok";
	    return mStatus;

	}

private void convertArgbToGrayscale(Bitmap bmpOriginal, int width,
	        int height) {
	    int pixel;
	    int k = 0;
	    int B = 0, G = 0, R = 0;
	    dots = new BitSet();
	    try {

	        for (int x = 0; x < height; x++) {
	            for (int y = 0; y < width; y++) {
	                // get one pixel color
	                pixel = bmpOriginal.getPixel(y, x);

	                // retrieve color of all channels
	                R = Color.red(pixel);
	                G = Color.green(pixel);
	                B = Color.blue(pixel);
	                // take conversion up to one single value by calculating
	                // pixel intensity.
	                R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
	                // set bit into bitset, by calculating the pixel's luma
	                if (R < 55) {                       
	                    dots.set(k);//this is the bitset that i'm printing
	                }
	                k++;

	            }


	        }


	    } catch (Exception e) {
	        // TODO: handle exception
	       Log.d("TAG", e.toString());
	    }
	}

/*
 * Close the connection to bluetooth printer.
 */
public void closeBT() throws IOException {
	try {
		stopWorker = true;
		mmOutputStream.close();
		mmInputStream.close();
		mmSocket.close();
		
		Toast.makeText(getApplicationContext(), "Bluetooth is close", Toast.LENGTH_SHORT).show();
	} catch (NullPointerException e) {
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	}
}

@Override
public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	// TODO Auto-generated method stub
	String item = arg0.getItemAtPosition(arg2).toString();
	
	// Showing selected spinner item
	if(item.equals("All Barangay")){
		barangay="all";
	}else{
		barangay=item;
	}
	
	 Cursor columns = db.customer_leftjoin_readingreport(barangay);
	 if(columns.getCount()==0){
		 Toast.makeText(getApplicationContext(), "Nothing to print in the barangay "+barangay+" Water Consumption Report", Toast.LENGTH_SHORT).show();
		 printreport.setClickable(false);
	 }else{
		 printreport.setClickable(true);
	 }


	
	
}

@Override
public void onNothingSelected(AdapterView<?> arg0) {
	// TODO Auto-generated method stub
	
}

}
