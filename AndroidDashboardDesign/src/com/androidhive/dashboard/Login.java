package com.androidhive.dashboard;


import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidhive.dashboard.R;

import com.database.helper.DBHelper;
import com.database.model.Users;

public class Login extends Activity {

	public static final String PREFS_NAME = "LoginPrefs";
	EditText username, password;
	CheckBox checkpass;
	Boolean login=false;
	String strName,strPass,fname="",lname="",usertype="",id="";
	DBHelper mydb;
	int count,count_user,userid;
	View view;
	FileOperations file=new FileOperations();
	SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		mydb = new DBHelper(this);
		
		username = (EditText) findViewById(R.id.username);
	    password = (EditText) findViewById(R.id.pass);
	    
	   // mydb.insertCustomer(new Customer("111", "Sample", "Sample", " ", "Residential", "102933"));
	   
	    
	    checkpass=(CheckBox)findViewById(R.id.hidepass);
	  
	    TextView registerScreen = (TextView) findViewById(R.id.link_to_registers);
	     // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
				// Switching to Register screen
        		Intent intent = new Intent(getApplicationContext(), Register.class);
    			startActivity(intent);
			}
		});
	    
    	Cursor users_count = mydb.select_all_user("Administrator");//test if administrator account exist
		count_user=users_count.getCount();
		
		if(count_user==1){
			registerScreen.setVisibility(View.INVISIBLE);
			
		}
		if(count_user!=1){
			 mydb.insertWaterformulaValues(10, (float) 3.5, 50);
			 mydb.insertSelectPrinter("MP300");
			 
		}
		
		
	    //if checkbox check it hide the password of the textbox
	    checkpass.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkpass.isChecked()){
					password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
										
				}else{
					password.setInputType(InputType.TYPE_CLASS_TEXT);
									
				}
			}
		});
  
        
   
	    
		 /*
         * Check if we successfully logged in before. 
         * If we did, redirect to home page
         */
         settings = getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("logged", "").toString().equals("logged")) {
			Intent intent = new Intent(Login.this, AndroidDashboardDesignActivity.class);
			startActivity(intent);
		}
		
		
        Button b = (Button) findViewById(R.id.btnLogin);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				strName = username.getText().toString();
				strPass = password.getText().toString();
				
				
					
					if (null == strName || strName.trim().length() == 0) {
						username.setError("Enter Your Username");
						username.requestFocus();
					} else if (null == strPass || strPass.trim().length() == 0) {
						password.setError("Enter Your Password");
						password.requestFocus();
					} else { 
						List<Users> users = mydb.validate_user_all(strName, strPass);
						if(users.size()>0){
							fname=users.get(0).getFname();
							lname=users.get(0).getLname();
							userid=users.get(0).getId();
							id=Integer.toString(userid);
							usertype=users.get(0).getUser_type();
						}
						
		
						
						if(!fname.isEmpty() && !lname.isEmpty()) {					
							//make SharedPreferences object
							settings = getSharedPreferences(PREFS_NAME, 0);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString("logged", "logged");
							editor.putString("fname", fname);
							editor.putString("id", id);
							editor.putString("lname", lname);
							editor.putString("usertype", usertype);
							editor.commit();
							
							login=true;
							
							Toast.makeText(getApplicationContext(), " Welcome "+fname+" "+lname, Toast.LENGTH_SHORT).show();
							file.writeLogs(fname+" "+lname+" log-in to the system on "+file.dateTime())	;//write logs
							Intent intent = new Intent(Login.this, AndroidDashboardDesignActivity.class);
							startActivity(intent);
						}else{
							Toast.makeText(getApplicationContext(), "Wrong Password/Username", Toast.LENGTH_SHORT).show();
						}
						
					
					}
				
			
				
				
			}
		});
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(login==true){
			finish();
		}
		
	}
	
	@Override
	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), "Please Login your account", Toast.LENGTH_SHORT).show();
	}
	
	

}
