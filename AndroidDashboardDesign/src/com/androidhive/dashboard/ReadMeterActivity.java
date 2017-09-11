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
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
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


public class ReadMeterActivity extends Activity implements OnItemClickListener,
		OnItemSelectedListener, MultiChoiceModeListener {
	/** Called when the activity is first created. */
	ArrayList<Customer> unreadmeter = new ArrayList<Customer>();

	CustomerAdapter adapter;
	DBHelper db;
	String querydata = "";
	TextView numrow, nonumrow;
	boolean color = false;
	String sfname, slname;
	ProgressBar loading;
	Customer custom;
	String reading_amount, account_name, barangay, connection_type, corporate,
			fname, lname, mname, prev_reading_value, prev_reading_date,
			meter_no, pres_reading_value, pres_reading_date, penalty_amount,
			reconnection, prev_amount, cur_meter_bal, customer_info_id,
			wpenalty, wduedate;
	int water_consumption;
	double amount_due;
	ListView dataList;

	// Bluetooth variables
	Boolean result = false, ready = false;
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
	// end of bluetooth variables

	String searchBarangay = "Select Barangay";
	String searchStandPipe = "Select Stand Pipe";

	Spinner brgyDropDownSpinner, standPipeSpinner;
	ArrayAdapter<String> barangayDropDownAdapter;
	ArrayAdapter<String> standPipeDropDownAdapter;

	FileOperations file = new FileOperations();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.readmeter_layout);

		SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
		sfname = settings.getString("fname", "").toString();
		slname = settings.getString("lname", "").toString();

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

	}

	public void querysqlite() {
		List<Customer> customer = db.select_all_readorunreadcustomer();
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
		dataList = (ListView) findViewById(R.id.unreadmeterlist);
		dataList.setOnItemClickListener(this);

		dataList.setAdapter(adapter);
		dataList.setTextFilterEnabled(true);
		dataList.setMultiChoiceModeListener(this);
		dataList.setChoiceMode(dataList.CHOICE_MODE_MULTIPLE_MODAL);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_menu, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				// this is your adapter that will be filtered
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
		/*case R.id.print:
			Intent intent = new Intent(getApplicationContext(),
					PrintReport.class);
			startActivity(intent);
			return true;
		case R.id.deleteReading:
			Intent intent2 = new Intent(getApplicationContext(),
					DeleteReading.class);
			startActivity(intent2);
			return true;*/
		case R.id.action_search:
			// search action
			return true;
			/*
			 * case android.R.id.home:
			 * 
			 * onBackPressed(); return true;
			 */

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

		AlertDialog alert = new AlertDialog.Builder(ReadMeterActivity.this)
				.create();
		alert.setTitle("Are you sure?");
		alert.setMessage("Are you sure want to print the bill of " + cname
				+ " ? ");
		// alert.setContentView(layoutResID)

		alert.setButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// Toast.makeText(getApplicationContext(), sfname+" "+slname,
				// Toast.LENGTH_SHORT).show();
			}
		});
		alert.setButton2("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				getdata(custom.getCustomerid());
				if (result == false) {

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

	public void getdata(String id) {

		Cursor columns = db.customer_leftjoin_reading(Integer.parseInt(id));
		columns.moveToFirst();
		customer_info_id = columns.getString(columns
				.getColumnIndex("customer_info_id"));
		fname = columns.getString(columns.getColumnIndex("fname"));
		lname = columns.getString(columns.getColumnIndex("lname"));
		mname = columns.getString(columns.getColumnIndex("mname"));
		corporate = columns.getString(columns.getColumnIndex("corporate"));

		if (fname.isEmpty()) {
			account_name = corporate;
		} else {
			account_name = fname + " " + mname + " " + lname;
		}
		barangay = columns.getString(columns.getColumnIndex("barangay"));
		connection_type = columns.getString(columns
				.getColumnIndex("connection_type"));
		meter_no = columns.getString(columns.getColumnIndex("meter_no"));
		prev_reading_value = columns.getString(columns
				.getColumnIndex("prev_reading_value"));
		prev_reading_date = columns.getString(columns
				.getColumnIndex("prev_reading_date"));
		pres_reading_value = columns.getString(columns
				.getColumnIndex("pres_reading_value"));
		pres_reading_date = columns.getString(columns
				.getColumnIndex("pres_reading_date"));

		penalty_amount = columns.getString(columns
				.getColumnIndex("penalty_amount"));
		reconnection = columns
				.getString(columns.getColumnIndex("reconnection"));
		prev_amount = columns.getString(columns.getColumnIndex("prev_amount"));
		cur_meter_bal = columns.getString(columns
				.getColumnIndex("cur_meter_bal"));

		if (!columns.isClosed()) {
			columns.close();
		}

		int waterconsumption = 0;
		
		//if(Integer.parseInt(prev_reading_value)>=9600 && (Double.parseDouble(pres_reading_value)>=1 && Double.parseDouble(pres_reading_value)<=200)){
		if(Integer.parseInt(prev_reading_value)>=99000 && (Double.parseDouble(pres_reading_value)>=1 && Double.parseDouble(pres_reading_value)<=1000)){
			//waterconsumption=(9999-Integer.parseInt(prev_reading_value))+ Integer.parseInt(pres_reading_value);
			  waterconsumption=(100000-Integer.parseInt(prev_reading_value))+ Integer.parseInt(pres_reading_value);
		}else{
			waterconsumption=(Integer.parseInt(pres_reading_value)-Integer.parseInt(prev_reading_value));
		}

		water_consumption = waterconsumption;

		// Calculating the water consumption for current charges
		Cursor reading = db.compute_water_amount(waterconsumption);
		reading.moveToFirst();
		reading_amount = reading.getString(reading
				.getColumnIndex("reading_value"));

		if (!reading.isClosed()) {
			reading.close();
		}

		amount_due = (Double.parseDouble(reading_amount)
				+
				// Double.parseDouble(penalty_amount)+
				Double.parseDouble(reconnection)
				+ Double.parseDouble(prev_amount) + Double
				.parseDouble(cur_meter_bal));

		// This is for the water formula values
		Cursor fomulavalues = db.viewWaterformulaValues();
		int count = fomulavalues.getCount();
		if (count == 1) {
			fomulavalues.moveToFirst();

			wpenalty = fomulavalues.getString(fomulavalues
					.getColumnIndex(DBHelper.W_COLUMN_PENALTY));
			wduedate = fomulavalues.getString(fomulavalues
					.getColumnIndex(DBHelper.W_COLUMN_DUE_DATE));

			if (!fomulavalues.isClosed()) {
				fomulavalues.close();
			}
		}
		// end

	}

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private class PrintBill extends AsyncTask<String, Void, Boolean> {
		private final ProgressDialog dialog = new ProgressDialog(
				ReadMeterActivity.this);

		@Override
		protected void onPreExecute() {

			this.dialog.setMessage("Printing...");
			this.dialog.setCanceledOnTouchOutside(false);
			this.dialog.show();

		}

		protected Boolean doInBackground(final String... args) {

			try {

				/*
				 * Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				 * R.drawable.wbsprintlogo); convertBitmap(bmp);
				 * mmOutputStream.write(PrinterCommands.SET_LINE_SPACING_24);
				 * 
				 * int offset = 0; while (offset < bmp.getHeight()) {
				 * mmOutputStream.write(PrinterCommands.SELECT_BIT_IMAGE_MODE);
				 * for (int x = 0; x < bmp.getWidth(); ++x) {
				 * 
				 * for (int k = 0; k < 3; ++k) {
				 * 
				 * byte slice = 0; for (int b = 0; b < 8; ++b) { int y =
				 * (((offset / 8) + k) * 8) + b; int i = (y * bmp.getWidth()) +
				 * x; boolean v = false; if (i < dots.length()) { v =
				 * dots.get(i); } slice |= (byte) ((v ? 1 : 0) << (7 - b)); }
				 * mmOutputStream.write(slice); } } offset += 24;
				 * //mmOutputStream.write(PrinterCommands.FEED_LINE);
				 * 
				 * }
				 */
				byte[] arrayOfByte1 = { 27, 33, 0 };
				byte[] format = { 27, 33, 0 };
				format[2] = ((byte) (0x10 | arrayOfByte1[2]));
				format[2] = ((byte) (0x20 | arrayOfByte1[2]));
				mmOutputStream.write(format);
				String msg0 = "\n";
				msg0 += "    HINUNANGAN\n";
				mmOutputStream.write(msg0.getBytes());
				String msg1 = "             WATERWORKS SYSTEM \n";
				msg1 += "         Municipality of Hinunangan\n";
				msg1 += "          Hinunangan, Southern Leyte\n";
				//msg1 += "         TIN: xxx-xxx-xxx-xxx VAT\n";

				format[2] = ((byte) (0x1 | arrayOfByte1[2]));
				mmOutputStream.write(format);
				mmOutputStream.write(msg1.getBytes());
				mmOutputStream.write(PrinterCommands.INIT);
				mmOutputStream.write(PrinterCommands.SET_LINE_SPACING_30);

				String msg = "______________________________\n";
				msg += "        Service Information \n";
				msg += "------------------------------\n";
				msg += "Name: " + account_name + "\n";
				msg += "Address: " + barangay + "\n";
				msg += "Type: " + connection_type + "\n";
				msg += "ACCT #: " + customer_info_id + " \n";
				msg += "MTR #: " + meter_no + " \n";
				msg += "------------------------------\n";
				msg += "Reading Date      Reading Val.\n\n";
				msg += dateFormat(prev_reading_date) + " 	       "
						+ prev_reading_value + "\n";
				msg += dateFormat(pres_reading_date) + " 	       "
						+ pres_reading_value + "\n";
				msg += "Water Consump:       " + water_consumption + "\n";
				msg += "______________________________\n";
				msg += "        Billing Details \n";
				msg += "------------------------------\n";
				msg += "Particulars        Amount\n\n";
				msg += "Cur.Charge	         "
						+ numberFormat(Double.parseDouble(reading_amount))
						+ "\n";
				// msg+="Penalty 	           "+numberFormat(Double.parseDouble(penalty_amount))+"\n";
				msg += "MTR Installment    "
						+ numberFormat(Double.parseDouble(cur_meter_bal))
						+ "\n";
				msg += "Prev.Amnt          "
						+ numberFormat(Double.parseDouble(prev_amount)) + "\n";
				msg += "Recnction Fee      "
						+ numberFormat(Double.parseDouble(reconnection)) + "\n";
				msg += "------------------------------\n";

				msg += "Amount Due	        " + numberFormat(amount_due) + "\n";

				msg += "Due Date	          " + due_date(pres_reading_date)
						+ "\n";
				msg += "After Due\n";
				msg += "  Date Charge     " + numberFormat(penalty_addition())
						+ "\n\n\n";
				msg += "Reader's Name: " + sfname + " " + slname + "\n";
				msg += "Reading date: \n";
				msg += "      " + ReadingDate(pres_reading_date) + "\n";
				msg += "+------------------------------+\n";
				msg += "| Please pay your bill before  |\n";
				msg += "| deadline to avoid penalty    |\n";
				msg += "| and disconnection.           |\n";
				msg += "+------------------------------+\n";
				msg += "| Meter Readers are not allowed|\n";
				msg += "| to accept payment..          |\n";
				msg += "+------------------------------+";
				msg += "\n";
				msg += "********Nothing follows*******\n\n\n";

				mmOutputStream.write(msg.getBytes());

				db.updateStat(Integer.parseInt(custom.getCustomerid()), 2);

			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return true;
		}

		private String numberFormat(Double x) {

			String number_format = NumberFormat.getNumberInstance(Locale.US)
					.format(x);
			return number_format;
		}

		private double penalty_addition() {
			double penalty_amount = amount_due
					* (Double.parseDouble(wpenalty) / 100);
			double total = (amount_due + penalty_amount);
			return total;
		}

		private String dateFormat(String date) {
			Calendar calendar = Calendar.getInstance();

			String mydatearray[] = date.split("\\-");
			int year = Integer.parseInt(mydatearray[0]);
			int month = Integer.parseInt(mydatearray[1]) - 1;
			int day = Integer.parseInt(mydatearray[2]);

			calendar.set(year, month, day);
			Date setDate = calendar.getTime();

			DateFormat dateFormat = new SimpleDateFormat("MMM. dd, yyyy");
			String setDateset = dateFormat.format(setDate);

			return setDateset;
		}

		private String ReadingDate(String date) {
			Calendar calendar = Calendar.getInstance();

			String mydatearray[] = date.split("\\-");
			int year = Integer.parseInt(mydatearray[0]);
			int month = Integer.parseInt(mydatearray[1]) - 1;
			int day = Integer.parseInt(mydatearray[2]);

			calendar.set(year, month, day);
			Date setDate = calendar.getTime();

			DateFormat dateFormat = new SimpleDateFormat(
					"MMM. dd, yyyy hh:mm:ss a");
			String setDateset = dateFormat.format(setDate);

			return setDateset;
		}

		private String due_date(String date) {
			Calendar calendar = Calendar.getInstance();

			String mydatearray[] = date.split("\\-");
			int year = Integer.parseInt(mydatearray[0]);
			int month = Integer.parseInt(mydatearray[1]) - 1;
			int day = Integer.parseInt(mydatearray[2]);

			calendar.set(year, month, day);
			Date setDate = calendar.getTime();
			calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(wduedate));
			Date next_date = calendar.getTime();

			DateFormat dateFormat = new SimpleDateFormat("MMM. dd, yyyy");
			String setDateset = dateFormat.format(next_date);

			return setDateset;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			if (success) {

				try {

					int successfullyread = db.updateStat(
							Integer.parseInt(custom.getCustomerid()), 2);

					if (successfullyread >= 1) {
						file.writeLogs(sfname + " " + slname
								+ " print the bill of cust. name: "
								+ account_name + " " + " connection type: "
								+ connection_type + " " + "mtr no: " + meter_no
								+ " " + " on " + file.dateTime());

						Toast.makeText(getApplicationContext(),
								"Print Succesfully...", Toast.LENGTH_SHORT)
								.show();

						Intent intent = new Intent(getApplicationContext(),
								ReadMeterActivity.class);
						       startActivity(intent);
						//searchListView("");
						
						closeBT();
						//finish();

					} else {
						Toast.makeText(getApplicationContext(),
								"Error in printing:(", Toast.LENGTH_SHORT)
								.show();
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Toast.makeText(getApplicationContext(), "Error in Printing..",
						Toast.LENGTH_SHORT).show();

			}
		}
	}

	public void findBT() {

		try {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

			if (mBluetoothAdapter == null) {
				Toast.makeText(getApplicationContext(),
						"No Bluetooth Adapter is available", Toast.LENGTH_SHORT)
						.show();

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
					// if(!device.getName().equals("MP300")){
					// MP300 is the name of the bluetooth printer device
					if (device.getName().equals(getPrinterName())) {
						mmDevice = device;

						break;
					}
					// Toast.makeText(getApplicationContext(),
					// "MP300 Printer not found", Toast.LENGTH_SHORT).show();
					// break;
					// }
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
			// Standard SerialPortService ID
			// 00001101-0000-1000-8000-00805f9b34fb
			UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
			mmSocket.connect();
			mmOutputStream = mmSocket.getOutputStream();
			mmInputStream = mmSocket.getInputStream();

			beginListenForData();
			Toast.makeText(getApplicationContext(),
					"Bluetooth Printer is ready to use", Toast.LENGTH_SHORT)
					.show();
			result = true;

		} catch (NullPointerException e) {
			result = false;
			Toast.makeText(
					getApplicationContext(),
					getPrinterName()
							+ " Printer not found. Be sure the "
							+ "printer connection is correctly set in the setting printer's name",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();

		} catch (Exception e) {
			result = false;
			Toast.makeText(getApplicationContext(),
					"Unable to connect the printer", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	/*
	 * After opening a connection to bluetooth printer device, we have to listen
	 * and check if a data were sent to be printed.
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
												Toast.makeText(
														getApplicationContext(),
														data,
														Toast.LENGTH_SHORT)
														.show();
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

		if (result) {
			PrintBill task = new PrintBill();
			task.execute();
		} else {
			// Toast.makeText(getApplicationContext(),
			// "Connect first your device", Toast.LENGTH_SHORT).show();
		}

	}

	// end find and open bluetooth below
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
						dots.set(k);// this is the bitset that i'm printing
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

			Toast.makeText(getApplicationContext(), "Bluetooth is close",
					Toast.LENGTH_SHORT).show();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(getApplicationContext(),
				AndroidDashboardDesignActivity.class);
		startActivity(intent);
	}

	public String getPrinterName() {
		String printerSelect = "";

		Cursor printerName = db.viewSelectPrinter();
		int count = printerName.getCount();
		if (count == 1) {
			printerName.moveToFirst();
			printerSelect = printerName.getString(printerName
					.getColumnIndex(DBHelper.PRINTER_COLUMN_NAME));

			if (!printerName.isClosed()) {
				printerName.close();
			}
		}
		return printerSelect;
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
			categories2.add("T" + x+"-");
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
	
	@Override
	public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
		switch (arg1.getItemId()) {
		case R.id.delete:
			
			SparseBooleanArray selected = adapter.getSelectedIds();
			for (int i = (selected.size() - 1); i >= 0; i--) {
				if (selected.valueAt(i)) {
					custom = adapter.getItem(selected.keyAt(i));
					confirmationRollback(custom);
				}
			}
			// Close CAB
			arg0.finish();
			return true;
			default:
				return false;
		}	
	  }

	private void confirmationRollback(final Customer custom) {
		
		final String customerid=custom.getCustomerid();
		final String cname = custom.getCorporate()+custom.getFname()+" "+custom.getLname(); 
		
		AlertDialog alert = new AlertDialog.Builder(this)
		.create();
		alert.setTitle("Are you sure?");
		alert.setMessage("Are you sure want to ROLLBACK the READING of "+cname+" ? " );
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
							file.writeLogs(sfname+" "+slname+" rollback the reading saved of cust. name: "+cname+" " +
									" connection type: "+custom.getConnection_type()+" " +
									"mtr no: "+custom.getMeter_no()+" " +
									" on "+file.dateTime());
							
							Toast.makeText(getApplicationContext(),"Reading Rollback Successfully!!!", Toast.LENGTH_SHORT).show();
							searchListView("");
						}else{
							Toast.makeText(getApplicationContext(),"System error on rollbacking reading!!!", Toast.LENGTH_SHORT).show();
						}
						
					}else{
						Toast.makeText(getApplicationContext(),"System error!!!", Toast.LENGTH_SHORT).show();
					}
					adapter.remove(custom);
				}
		});
		alert.show();
	}
	@Override
	public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
		arg0.getMenuInflater().inflate(R.menu.main, arg1);
		return true;
		
	}
	@Override
	public void onDestroyActionMode(ActionMode arg0) {
		adapter.removeSelection();
	}
	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		return false;
	}
	@Override
	public void onItemCheckedStateChanged(ActionMode arg0, int arg1, long arg2, boolean arg3) {
		
		final int checkedCount = dataList.getCheckedItemCount();
		arg0.setTitle(checkedCount + " Selected");
		adapter.toggleSelection(arg1);
		
	}

}
