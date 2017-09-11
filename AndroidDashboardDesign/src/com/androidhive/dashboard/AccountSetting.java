package com.androidhive.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.database.helper.DBHelper;
import com.database.model.Users;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import androidhive.dashboard.R;

public class AccountSetting extends Activity implements OnItemSelectedListener {
	 /** Called when the activity is first created. */
	EditText firstname,lastname,username,rpassword,password,oldpassword;
	String uid,usertype,fname,lname,uname,rupass,upass,idu,usertypeu,fnameu,lnameu,unameu,upassu,oldpass,userstatu;
	Spinner spinner;
	DBHelper mydb;
	int count_user;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting);
        
        ImageView icon=(ImageView)findViewById(R.id.systemicon);
        icon.setImageResource(R.drawable.user_count);
        firstname = (EditText) findViewById(R.id.FirstName);
	    lastname = (EditText) findViewById(R.id.LastName);
        username = (EditText) findViewById(R.id.uname);
        oldpassword = (EditText) findViewById(R.id.oldupass);
        password = (EditText) findViewById(R.id.upass);
	    rpassword = (EditText) findViewById(R.id.rupass);
	    SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
	    uid=settings.getString("id", "").toString();
	    mydb = new DBHelper(this);
	    
    	Cursor users = mydb.select_all_user(uid);//test if administrator account exist
    	int count=users.getCount();
		if(count==1){
			users.moveToFirst();
			idu = users.getString(users
					.getColumnIndex(DBHelper.USERS_COLUMN_ID));
			fnameu = users.getString(users
					.getColumnIndex(DBHelper.USERS_COLUMN_FNAME));
			lnameu = users.getString(users
					.getColumnIndex(DBHelper.USERS_COLUMN_LNAME));
			unameu = users.getString(users
					.getColumnIndex(DBHelper.USERS_COLUMN_USERNAME));
			usertypeu = users.getString(users
					.getColumnIndex(DBHelper.USERS_COLUMN_UTYPE));
			upassu = users.getString(users
					.getColumnIndex(DBHelper.USERS_COLUMN_PASSWORD));
			userstatu = users.getString(users
					.getColumnIndex(DBHelper.USERS_COLUMN_USTAT));
			
			if (!users.isClosed()) {
				users.close();
			}
		}
		firstname.setText(fnameu);
		lastname.setText(lnameu);
		username.setText(unameu);
		
		
		
     // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner);
        
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add(usertypeu);
		
        
       
      
        
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
	
	public void editaccount(View view){
	
				fname = firstname.getText().toString();
				lname = lastname.getText().toString();
				uname = username.getText().toString();
				oldpass = oldpassword.getText().toString();
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
				}else if (null == oldpass || oldpass.trim().length() == 0 || !oldpass.equals(upassu)) {
					oldpassword.setError("Wrong Old Password");
					oldpassword.requestFocus();
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
					
					AlertDialog alert = new AlertDialog.Builder(AccountSetting.this)
					.create();
					alert.setTitle("Are you sure?");
					alert.setMessage("Are you sure want to edit "+ fname +" "+ lname+" account?");

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
								Boolean insertRow=mydb.updateUser(new Users(Integer.parseInt(uid),fname, lname, uname, rupass, usertype,userstatu));
								if(insertRow) {					
									
									Toast.makeText(getApplicationContext(), fname+" "+lname+" account successfully save!!!", Toast.LENGTH_SHORT).show();
									//Intent intent = new Intent(getApplicationContext(), Login.class);
									//startActivity(intent);
								}else{
									Toast.makeText(getApplicationContext(), "Error in updating the user", Toast.LENGTH_SHORT).show();
								}
								
							}
					});
					alert.show();
	
				}
	
			}


}