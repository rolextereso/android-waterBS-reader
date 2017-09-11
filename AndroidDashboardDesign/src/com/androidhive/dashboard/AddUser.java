package com.androidhive.dashboard;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import androidhive.dashboard.R;

import com.database.helper.DBHelper;
import com.database.model.Users;

public class AddUser extends Activity implements OnItemSelectedListener {
	 /** Called when the activity is first created. */
		EditText firstname,lastname,username,rpassword,password;
		String usertype,fname,lname,uname,rupass,upass;
		Spinner spinner;
		DBHelper mydb;
		int count_user;
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.register_user);
	        
	        firstname = (EditText) findViewById(R.id.FirstName);
		    lastname = (EditText) findViewById(R.id.LastName);
	        username = (EditText) findViewById(R.id.uname);
	        password = (EditText) findViewById(R.id.upass);
		    rpassword = (EditText) findViewById(R.id.rupass);
		    ImageView icon=(ImageView)findViewById(R.id.systemicon);
	        icon.setImageResource(R.drawable.adduser);
		    mydb = new DBHelper(this);
		    
	    	Cursor users_count = mydb.select_all_user("Administrator");//test if administrator account exist
			count_user=users_count.getCount();
			
	     // Spinner element
	        spinner = (Spinner) findViewById(R.id.spinner);
	        
	        // Spinner click listener
	        spinner.setOnItemSelectedListener(this);
	        
	        // Spinner Drop down elements
	        List<String> categories = new ArrayList<String>();
	        if(count_user==1){
	        	categories.add("Field Officer");
			}else{
				categories.add("Administrator");
			}
	        
	       
	      
	        
	        // Creating adapter for spinner
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
			
			// Drop down layout style - list view with radio button
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			// attaching data adapter to spinner
			spinner.setAdapter(dataAdapter);
			
	    }
	    
	    @Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// On selecting a spinner item
			String item = parent.getItemAtPosition(position).toString();
			
			// Showing selected spinner item
			usertype=item;

		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
		public void register(View view){
		
					fname = firstname.getText().toString();
					lname = lastname.getText().toString();
					uname = username.getText().toString();
					upass = password.getText().toString();
					rupass = rpassword.getText().toString();
					
						
					if (null == fname || fname.trim().length() == 0) {
						firstname.setError("Enter Your First Name");
						firstname.requestFocus();
					} else if (null == lname || lname.trim().length() == 0) {
						lastname.setError("Enter Your Last Name");
						lastname.requestFocus();
					} else if (null == uname || uname.trim().length() == 0) {
						username.setError("Enter Your Username");
						username.requestFocus();
					} else if (null == upass || upass.trim().length() == 0) {
						password.setError("Enter Your Password");
						password.requestFocus();
					}else if (null == rupass || rupass.trim().length() == 0) {
						rpassword.setError("Re-type your Password");
						rpassword.requestFocus();
					} else if (!rupass.equals(upass) || rupass.trim().length() != upass.trim().length()) {
						rpassword.setError("Password Mismatch");
						rpassword.requestFocus();
					}else { 
						
						AlertDialog alert = new AlertDialog.Builder(AddUser.this)
						.create();
						alert.setTitle("Are you sure?");
						alert.setMessage("Are you sure want to add "+ fname +" "+ lname+"?");

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
									Boolean insertRow=mydb.insertUser(new Users(fname, lname, uname, rupass, usertype,"Active"));
									if(insertRow) {					
										
										Toast.makeText(getApplicationContext(), fname+" "+lname+" successfully save!!!", Toast.LENGTH_SHORT).show();
										//Intent intent = new Intent(getApplicationContext(), Login.class);
										//startActivity(intent);
									}else{
										Toast.makeText(getApplicationContext(), "Error in inserting new user", Toast.LENGTH_SHORT).show();
									}
									
								}
						});
						alert.show();
		
					}
		
				}

	}