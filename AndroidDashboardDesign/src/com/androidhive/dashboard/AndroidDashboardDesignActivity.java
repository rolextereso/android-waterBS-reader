package com.androidhive.dashboard;

import com.navigation.drawer.activity.BaseActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidhive.dashboard.R;

public class AndroidDashboardDesignActivity extends BaseActivity {

	// DatabaseHelper helper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getLayoutInflater().inflate(R.layout.dashboard_layout, frameLayout);

		/**
		 * Setting title and itemChecked
		 */
		mDrawerList.setItemChecked(0, true);
		setTitle(listArray[0]);

		TextView session_user = (TextView) findViewById(R.id.usernames);
		TextView susertype = (TextView) findViewById(R.id.usertypes);

		session_user.setText(sfname + " " + slname);
		susertype.setText(usertype);

		/**
		 * Creating all buttons instances
		 * */
		// Dashboard News feed button
		Button btn_newsfeed = (Button) findViewById(R.id.btn_news_feed);

		// Dashboard Friends button
		Button btn_friends = (Button) findViewById(R.id.btn_friends);

		// Dashboard Messages button
		Button btn_messages = (Button) findViewById(R.id.btn_messages);

		// Dashboard Places button
		Button btn_places = (Button) findViewById(R.id.btn_places);

		// Dashboard Events button
		Button btn_events = (Button) findViewById(R.id.btn_events);

		// Dashboard Photos button
		Button btn_photos = (Button) findViewById(R.id.btn_photos);

		/**
		 * Handling all button click events
		 * */

		// Listening to News Feed button click
		btn_newsfeed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				Intent i = new Intent(getApplicationContext(),
						UnreadMeterActivity.class);
				startActivity(i);
			}
		});

		// Listening Friends button click
		btn_friends.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				Intent i = new Intent(getApplicationContext(),
						ReadMeterActivity.class);
				startActivity(i);
			}
		});

		// Listening Messages button click
		btn_messages.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				if (usertype.equals("Administrator")) {
					Intent i = new Intent(getApplicationContext(),
							EmptyCustomerActivity.class);
					startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(),
							"You have no permission to open this module!",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		// Listening to Places button click
		btn_places.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				Intent i = new Intent(getApplicationContext(), Settings.class);
				startActivity(i);
			}
		});

		// Listening to Events button click
		btn_events.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				if (usertype.equals("Administrator")) {
					Intent i = new Intent(getApplicationContext(),
							ImportDB.class);
					startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(),
							"You have no permission to open this module!",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		// Listening to Photos button click
		btn_photos.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				if (usertype.equals("Administrator")) {
					Intent i = new Intent(getApplicationContext(),
							ExportDB.class);
					startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(),
							"You have no permission to open this module!",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater Inflater = getMenuInflater();
		Inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.logout) {
			logout_app();
		} else if (item.getItemId() == R.id.about) {
			about_app();
		}
		return super.onOptionsItemSelected(item);
	}

}