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

public class FormulaSetting extends Activity{
	 /** Called when the activity is first created. */
	DBHelper mydb;
	String wid;
	String minval,vpc,addval;
	EditText minimumval,valuepercubic,additionalval;
	String minvals;
	String vpcs;
	String ads;
	Button b;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formula_setting);
        SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
	    String usertype=settings.getString("usertype", "").toString();
        ImageView icon=(ImageView)findViewById(R.id.systemicon);
        icon.setImageResource(R.drawable.formula);
        
	    mydb = new DBHelper(this);
	    minimumval = (EditText) findViewById(R.id.minimumvalue);
	    valuepercubic = (EditText) findViewById(R.id.valuepercubic);
	    additionalval = (EditText) findViewById(R.id.additionalamount);
	     b = (Button) findViewById(R.id.btnformulasetting);
		
	    
    	Cursor fomulavalues = mydb.viewWaterformulaValues();
    	int count=fomulavalues.getCount();
		if(count==1){
			fomulavalues.moveToFirst();
			wid = fomulavalues.getString(fomulavalues
					.getColumnIndex(DBHelper.W_COLUMN_WID));
			minval = fomulavalues.getString(fomulavalues
					.getColumnIndex(DBHelper.W_COLUMN_MINIMUM));
			vpc = fomulavalues.getString(fomulavalues
					.getColumnIndex(DBHelper.W_COLUMN_VPC));
			addval = fomulavalues.getString(fomulavalues
					.getColumnIndex(DBHelper.W_COLUMN_ADDITIONAL));
		
			
			if (!fomulavalues.isClosed()) {
				fomulavalues.close();
			}
		}
		minimumval.setText(minval);
		valuepercubic.setText(vpc);
		additionalval.setText(addval);
		
		if(!usertype.equals("Administrator")){
		
			
			minimumval.setFocusable(false);
			minimumval.setClickable(false);
			
			valuepercubic.setFocusable(false);
			valuepercubic.setClickable(false);
			
			additionalval.setFocusable(false);
			additionalval.setClickable(false);
		
			b.setVisibility(View.INVISIBLE);
		}
	
	
		
    }

	public void saveformula(View view){
		
				minvals=minimumval.getText().toString();
				vpcs=valuepercubic.getText().toString();
				ads=additionalval.getText().toString();
	
						
					
				if (null == minvals || minvals.trim().length() == 0) {
					minimumval.setError("Enter Minimum Value");
					minimumval.requestFocus();
				} else if (null == vpcs || vpcs.trim().length() == 0) {
					valuepercubic.setError("Enter Value per cubic");
					valuepercubic.requestFocus();
				} else if (null == ads || ads.trim().length() == 0) {
					additionalval.setError("Enter Additional Value");
					additionalval.requestFocus();
				
				}else { 
					
					AlertDialog alert = new AlertDialog.Builder(FormulaSetting.this)
					.create();
					alert.setTitle("Are you sure?");
					alert.setMessage("Are you sure want to edit its value?");

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
								Boolean insertRow=mydb.updateWaterformulaValues(wid, minvals, vpcs, ads);
								if(insertRow) {					
									
									Toast.makeText(getApplicationContext(),"Values successfully save!!!", Toast.LENGTH_SHORT).show();
									//Intent intent = new Intent(getApplicationContext(), Login.class);
									//startActivity(intent);
								}else{
									Toast.makeText(getApplicationContext(), "Error in updating the values", Toast.LENGTH_SHORT).show();
								}
								
							}
					});
					alert.show();
	
				}
	
	}

}
