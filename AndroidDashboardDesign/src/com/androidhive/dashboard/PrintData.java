package com.androidhive.dashboard;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidhive.dashboard.R;

public class PrintData extends Activity{


	// android built in classes for bluetooth operations
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

     /*
	 * This will find a bluetooth printer device
	 */
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
					
					// MP300 is the name of the bluetooth printer device
					if (device.getName().equals("MP300")) {
						mmDevice = device;
						break;
					}
				}
			}
			Toast.makeText(getApplicationContext(), "Bluetooth Printer Found", Toast.LENGTH_SHORT).show();
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
			UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
			mmSocket.connect();
			mmOutputStream = mmSocket.getOutputStream();
			mmInputStream = mmSocket.getInputStream();

			beginListenForData();

			Toast.makeText(getApplicationContext(), "Bluetooth Printer is ready to use", Toast.LENGTH_SHORT).show();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
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
							
							int bytesAvailable = mmInputStream.available();
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
	public void sendData() throws IOException {
		try {
			//format text
			  // Bold
            /*format[2] = ((byte)(0x8 | arrayOfByte1[2]));

            // Height
            format[2] = ((byte)(0x10 | arrayOfByte1[2]));

            // Width
            format[2] = ((byte) (0x20 | arrayOfByte1[2]));

            // Underline
            format[2] = ((byte)(0x80 | arrayOfByte1[2]));

            // Small
            format[2] = ((byte)(0x1 | arrayOfByte1[2]));
            */
			// the text typed by the user
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.wbsprintlogo);
		        convertBitmap(bmp);
		        mmOutputStream.write(PrinterCommands.SET_LINE_SPACING_24);

		        int offset = 0;
		        while (offset < bmp.getHeight()) {
		        	mmOutputStream.write(PrinterCommands.SELECT_BIT_IMAGE_MODE);
		            for (int x = 0; x < bmp.getWidth(); ++x) {

		                for (int k = 0; k < 3; ++k) {

		                    byte slice = 0;
		                    for (int b = 0; b < 8; ++b) {
		                        int y = (((offset / 8) + k) * 8) + b;
		                        int i = (y * bmp.getWidth()) + x;
		                        boolean v = false;
		                        if (i < dots.length()) {
		                            v = dots.get(i);
		                        }
		                        slice |= (byte) ((v ? 1 : 0) << (7 - b));
		                    }
		                    mmOutputStream.write(slice);
		                }
		            }
		            offset += 24;
		            mmOutputStream.write(PrinterCommands.FEED_LINE);
		         
		        }
			byte[] arrayOfByte1 = { 27, 33, 0 };
			byte[] format = { 27, 33, 0 };
			String 
			   msg="______________________________\n\n";
			   msg+="        Service Information \n";
			   msg+="------------------------------\n";
			   msg+="Name: Rolly Tereso Jr. \n";
			   msg+="Address: IV Ambacon \n";
			   msg+="Type: Residential \n";
			   msg+="ACCT/MTR: 08140023/01004063 \n";
			   msg+="------------------------------\n";
			   msg+="Reading Date      Reading Val.\n\n";
			   msg+="Dec.02,2003 	       "+"12323\n";
			   msg+="Dec.30,2003 	       "+"12323\n";
			   msg+="Water Consump:     "+"20000\n";
			   msg+="______________________________\n\n";
			   msg+="        Billing Details \n";
			   msg+="------------------------------\n";
			   msg+="Particulars        Amount\n\n";
			   msg+="Cur.Charge	         "+"12323\n";
			   msg+="Penalty 	           "+"12323\n";
			   msg+="MTR Installment    "+"20000\n";
			   msg+="Prev.Amnt          "+"20000\n";
			   msg+="Recnction Fee      "+"20000\n";
			   msg+="------------------------------\n";
			   msg+="Amount Due	        "+"20000\n";
			   msg+="Due Date	          "+"Dec.30,2003\n";
			   msg+="After Due\n";
			   msg+="  Date Charge      "+"124333\n\n\n";
			   msg+="-------Nothing follows--------\n";
			   msg+="\n\n";
			   
			   mmOutputStream.write(msg.getBytes());
	
			
			
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Close the connection to bluetooth printer.
	 */
	void closeBT() throws IOException {
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
}
