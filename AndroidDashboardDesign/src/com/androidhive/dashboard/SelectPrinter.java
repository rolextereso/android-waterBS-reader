package com.androidhive.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.database.helper.DBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import androidhive.dashboard.R;

public class SelectPrinter extends Activity{
	 /** Called when the activity is first created. */
	DBHelper mydb;
	String printerSelect, printerSelectID;
	String getPrinterName;
	EditText printer_Name;
	String minvals;
	String vpcs;
	String ads;
	Button button;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printer_select);
        SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
	    String usertype=settings.getString("usertype", "").toString();
        ImageView icon=(ImageView)findViewById(R.id.systemicon);
        icon.setImageResource(R.drawable.printer_select);
        
	    mydb = new DBHelper(this);
	    printer_Name = (EditText) findViewById(R.id.printer_name);
	    button = (Button) findViewById(R.id.savePrinter);
		
	    
    	Cursor printerName = mydb.viewSelectPrinter();
    	int count=printerName.getCount();
		if(count==1){
			printerName.moveToFirst();
			printerSelect = printerName.getString(printerName
					.getColumnIndex(DBHelper.PRINTER_COLUMN_NAME));
			printerSelectID = printerName.getString(printerName
					.getColumnIndex(DBHelper.PRINTER_COLUMN_ID));
			if (!printerName.isClosed()) {
				printerName.close();
			}
		}
		printer_Name.setText(printerSelect);
		
		
		if(!usertype.equals("Administrator")){
		
			
			printer_Name.setFocusable(false);
			printer_Name.setClickable(false);
		
			button.setVisibility(View.INVISIBLE);
		}
    }

	public void saveformula(View view){
		
				getPrinterName=printer_Name.getText().toString();
			
					AlertDialog alert = new AlertDialog.Builder(SelectPrinter.this)
					.create();
					alert.setTitle("Are you sure?");
					alert.setMessage("Are you sure want to save changes?");

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
								Boolean updateRow=mydb.updateSelectPrinter(printerSelectID,getPrinterName);
								if(updateRow) {					
									
									Toast.makeText(getApplicationContext(),"Connection Name successfully save!!!", Toast.LENGTH_SHORT).show();
								
								}else{
									Toast.makeText(getApplicationContext(), "Error in updating the values", Toast.LENGTH_SHORT).show();
								}
								
							}
					});
					alert.show();
	
				
	
	}

}
