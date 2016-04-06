package com.example.beacontest;

import com.example.beacontest.model.RawScanRecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	BluetoothAdapter bluetoothAdapter;
	BluetoothLeScanner bls;
	ScanCallback scanCallback;
	BluetoothAdapter.LeScanCallback leScanCallback;
	
	private static final long SCAN_PERIOD = 10000;
	private static final int REQUEST_ENABLE_BT = 1;
	
    private Handler mHandler;
    
    TextView textView;
    ListView listView;
    Button scanButton;
    
    ArrayAdapter<RawScanRecord> aa;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textView = (TextView) findViewById(R.id.textView);
		listView = (ListView) findViewById(R.id.listView);
		scanButton = (Button) findViewById(R.id.button);
		mHandler = new Handler();
		
		// Use this check to determine whether BLE is supported on the device. Then
		// you can selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
		    Toast.makeText(this, "Bluetooth LE not supported", Toast.LENGTH_SHORT).show();
		    finish();
		}
		
		scanCallback = new ScanCallback()	//Callback for API 21+
		{
			@Override
			public void onScanResult(int callbackType, ScanResult result)
			{
				for(int i=0; i<aa.getCount(); i++)
				{
					if(result.getDevice().getAddress().equals(aa.getItem(i).deviceAddress))	//Check for duplicates using BluetoothDevice address? //String
					{
						//Duplicate
						//Log.d("onScanResult duplicate", result.getDevice().getAddress());
						return;
					}
				}
				byte[] raw = result.getScanRecord().getBytes();
				RawScanRecord rawRecord = new RawScanRecord(result.getDevice().getAddress(),
						getBytes(raw,0,1),
						getBytes(raw,1,2),
						getBytes(raw,2,3),
						getBytes(raw,3,4),
						getBytes(raw,4,5),
						getBytes(raw,5,9),
						getBytes(raw,9,25),
						getBytes(raw,25,27),
						getBytes(raw,27,29),
						getBytes(raw,29,30));
				aa.add(rawRecord);	//Add to adapter
			}
			
			@Override
			public void onScanFailed(int errorCode)
			{
				Log.d("onScanFailed", "errorCode "+errorCode);
				//super.onScanFailed(errorCode);
				textView.setText("onScanFailed "+errorCode);
			}
		};
		
		leScanCallback = new BluetoothAdapter.LeScanCallback()	//Callback for API 18-20
		{

			@Override
			public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) 
			{
				//Log.d("onLeScan", device.getAddress());
				for(int i=0; i<aa.getCount(); i++)
				{
					if(device.getAddress().equals(aa.getItem(i).deviceAddress))	//Check for duplicates using BluetoothDevice address? //String
					{
						//Duplicate
						//Log.d("onScanResult duplicate", result.getDevice().getAddress());
						return;
					}
				}
				RawScanRecord rawRecord = new RawScanRecord(device.getAddress(),
						getBytes(scanRecord,0,1),
						getBytes(scanRecord,1,2),
						getBytes(scanRecord,2,3),
						getBytes(scanRecord,3,4),
						getBytes(scanRecord,4,5),
						getBytes(scanRecord,5,9),
						getBytes(scanRecord,9,25),
						getBytes(scanRecord,25,27),
						getBytes(scanRecord,27,29),
						getBytes(scanRecord,29,30));
				aa.add(rawRecord);	//Add to adapter
			}
		};
		
		aa = new ScanListAdapter(this, android.R.layout.simple_list_item_1);
		listView.setAdapter(aa);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{	
				AlertDialog alertBuilder = new AlertDialog.Builder(MainActivity.this)
				.setTitle("Bluetooth Payload")
				//.setMessage(aa.getItem(position).getScanRecord().toString()).show();
				.setMessage(aa.getItem(position).toString()).show();
			}
		});
		
		scanButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				//Log.d("onClick", "clicked");
				textView.setText("Scanning...");
				aa.clear();
				scanLeDevice(true);
			}
		});
	}
	
	public byte[] getBytes(byte[] orig, int start, int end)
	{
		byte[] result = java.util.Arrays.copyOfRange(orig, start, end);	//START IS INCLUSIVE, END IS EXCLUSIVE
		//Log.d("getBytes", "length "+result.length);
		return result;
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// Ensures Bluetooth is available on the device and it is enabled. If not,
		// displays a dialog requesting user permission to enable Bluetooth.
		if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) 
		{
			Log.e("Bluetooth error onCreate", "Enable bluetooth in settings");
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		// Initializes Bluetooth adapter.
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		//bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		bls = bluetoothAdapter.getBluetoothLeScanner();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
	}
	
	private void scanLeDevice(final boolean enable) 
	{
        if (enable)
        {
        	Log.d("scanLeDevice", "Scanning");
            // Stops scanning after a pre-defined scan period.	//10s
            mHandler.postDelayed(new Runnable() 
            {
                @Override
                public void run() 
                {
                	Log.d("scanLeDevice", "Stop scan");
                	if(Build.VERSION.SDK_INT<21) bluetoothAdapter.stopLeScan(leScanCallback);
                	else bls.stopScan(scanCallback);
                    if(textView.getText().equals("Scanning..."))
                    {
                    	textView.setText("Timeout");
                    }
                }
            }, SCAN_PERIOD);

            if(Build.VERSION.SDK_INT<21) bluetoothAdapter.startLeScan(leScanCallback);
            else bls.startScan(scanCallback);
        }
        else //enable false	//Use to stop scanning
        {
        	if(Build.VERSION.SDK_INT<21) bluetoothAdapter.stopLeScan(leScanCallback);
        	else bls.stopScan(scanCallback);
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class ScanListAdapter extends ArrayAdapter<RawScanRecord>
	{
		Context context;
		int resource;
		TextView textView;
		public ScanListAdapter(Context context, int resource) 
		{
			super(context, resource);
			this.context = context;
			this.resource = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			textView = (TextView) super.getView(position, convertView, parent);
			textView.setText(getItem(position).deviceAddress);
			return textView;
		}
	}
}
