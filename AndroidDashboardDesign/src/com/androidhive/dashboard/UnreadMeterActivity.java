package com.androidhive.dashboard;

import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import com.database.model.Barangay;
import com.database.model.Customer;
import com.navigation.drawer.activity.BaseActivity;

public class UnreadMeterActivity extends BaseActivity implements
		OnItemClickListener, OnItemSelectedListener {

	ArrayList<Customer> unreadmeter = new ArrayList<Customer>();
	ArrayList<Barangay> brgy = new ArrayList();
	// SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);

	ArrayAdapter<String> barangayDropDownAdapter;
	ArrayAdapter<String> standPipeDropDownAdapter;

	CustomerAdapter adapter;
	DBHelper db;
	String querydata = "";
	TextView numrow, nonumrow;
	boolean color = false;

	ProgressBar loading;
	Customer custom;

	String searchBarangay = "Select Barangay";
	String searchStandPipe = "Select Stand Pipe";

	Spinner brgyDropDownSpinner, standPipeSpinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.unreadmeter_layout, frameLayout);

		/**
		 * Setting title and itemChecked
		 */
		mDrawerList.setItemChecked(1, true);
		setTitle(listArray[1]);
		// setContentView(R.layout.unreadmeter_layout);

		db = new DBHelper(this);
		numrow = (TextView) findViewById(R.id.result_found);
		nonumrow = (TextView) findViewById(R.id.no_result_found);
		loading = (ProgressBar) findViewById(R.id.progressBar1);

		brgyDropDownSpinner = (Spinner) findViewById(R.id.barangay_dropdown);
		standPipeSpinner = (Spinner) findViewById(R.id.standpipe);

		// Spinner click listener
		brgyDropDownSpinner.setOnItemSelectedListener(this);
		standPipeSpinner.setOnItemSelectedListener(this);

		querysqlite();
		dropdownBarangay();

		standPipeSpinner.setSelection(standPipeDropDownAdapter
				.getPosition(SaveReading.searchStandPipe));
		brgyDropDownSpinner.setSelection(barangayDropDownAdapter
				.getPosition(SaveReading.searchBarangay));

	}

	public void querysqlite() {
		List<Customer> customer = db.select_all_customer();
		for (Customer c : customer) {
			unreadmeter.add(c);

		}

		if (unreadmeter.size() <= 0) {
			nonumrow.setVisibility(View.VISIBLE);
			nonumrow.setText("No Data Found!!!");
		} else {
			nonumrow.setVisibility(View.GONE);
		}
		// nonumrow.setText("Searching...");
		loading.setVisibility(View.GONE);
		adapter = new CustomerAdapter(this, R.layout.unreadmeter_row,
				unreadmeter);
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

		SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				standPipeSpinner.setSelection(standPipeDropDownAdapter
						.getPosition("Select Stand Pipe"));
				brgyDropDownSpinner.setSelection(barangayDropDownAdapter
						.getPosition("Select Barangay"));
				searchListView(newText);
				System.out.println("query on search box " + newText);
				return true;

			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				// this is your adapter that will be filtered
				adapter.getFilter().filter(query);
				System.out.println("on query submit: " + query);

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
		case R.id.action_search:
			// search action
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		custom = adapter.getItem(position);
		String cname = custom.getCorporate() + custom.getFname() + " "
				+ custom.getLname();

		String id_To_Search = custom.getCustomerid();

		String meternum = custom.getMeter_no();
		String connection_type = custom.getConnection_type();
		Bundle dataBundle = new Bundle();
		dataBundle.putString("id", id_To_Search);
		dataBundle.putString("cname", cname);
		dataBundle.putString("meternum", meternum);
		dataBundle.putString("connectiontype", connection_type);

		dataBundle.putString("searchBarangay", searchBarangay);
		dataBundle.putString("searchStandPipe", searchStandPipe);
		Intent intent = new Intent(getApplicationContext(), SaveReading.class);
		intent.putExtras(dataBundle);
		startActivity(intent);

	}

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void dropdownBarangay() {

		// Spinner Drop down elements
		List<String> categories = new ArrayList<String>();
		categories.add("Select Barangay");
		Cursor rs = db.select_all_barangay();
		while (rs.moveToNext()) {
			categories.add(rs.getString(rs.getColumnIndex("brgy")));
		}

		List<String> categories2 = new ArrayList<String>();
		categories2.add("Select Stand Pipe");
		for (int x = 1; x <= 60; x++) {
			categories2.add("T" + x + "-");
			// categories2.add("Automobile");
		}

		// Creating adapter for spinner
		barangayDropDownAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, categories);

		// Drop down layout style - list view with radio button
		barangayDropDownAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		brgyDropDownSpinner.setAdapter(barangayDropDownAdapter);

		// Creating adapter for spinner
		standPipeDropDownAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, categories2);

		// Drop down layout style - list view with radio button
		standPipeDropDownAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		standPipeSpinner.setAdapter(standPipeDropDownAdapter);
		// spinner2.setSelection(standPipeDropDownAdapter.getPosition("T-49"),
		// true);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		String newText = parent.getItemAtPosition(position).toString();

		switch (parent.getId()) {
		case R.id.barangay_dropdown:
			// Do stuff for barangay_dropdown
			searchBarangay = newText;
			break;
		case R.id.standpipe:
			// Do stuff for stand pipe
			searchStandPipe = newText;
			break;
		}
		if ((searchBarangay == "Select Barangay")
				&& (searchStandPipe == "Select Stand Pipe")) {
			searchListView("");
		} else if (!(searchBarangay == "Select Barangay")
				&& (searchStandPipe == "Select Stand Pipe")) {
			searchListView(searchBarangay + "|-");
		} else if ((searchBarangay == "Select Barangay")
				&& !(searchStandPipe == "Select Stand Pipe")) {
			searchListView(searchStandPipe + "|-");
		} else if (!(searchBarangay == "") && !(searchStandPipe == "")) {
			searchListView(searchBarangay + "|" + searchStandPipe + "|" + "-");
		} else {
			searchListView("");
		}

	}

	private void searchListView(String newText) {
		adapter.getFilter().filter(newText);
		System.out.println("on text chnge text: " + newText);
		loading.setVisibility(View.VISIBLE);
		nonumrow.setVisibility(View.VISIBLE);
		nonumrow.setText("Searching...");

		adapter.getFilter().filter(newText, new Filter.FilterListener() {
			public void onFilterComplete(int count) {
				numrow.setText("Result found: " + Integer.toString(count));

				if (adapter.isEmpty()) {
					nonumrow.setVisibility(View.VISIBLE);
					nonumrow.setText("No Result Found!!!");
				} else {
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
