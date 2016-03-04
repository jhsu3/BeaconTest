package com.example.beacontest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	BluetoothAdapter bluetoothAdapter;
	BluetoothLeScanner bls;
	ScanCallback scanCallback;
	private static final long SCAN_PERIOD = 10000;
	
    private boolean mScanning;
    private Handler mHandler;
    
    TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textView = (TextView) findViewById(R.id.textView);
		mHandler = new Handler();
		
		// Initializes Bluetooth adapter.
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		bls = bluetoothAdapter.getBluetoothLeScanner();
		scanCallback = new ScanCallback()
		{
			@Override
			public void onScanResult(int callbackType, ScanResult result)
			{
				super.onScanResult(callbackType, result);
				textView.setText(result.toString());
				Log.d("onScanResult", result.toString());
			}
		};
		
		// Ensures Bluetooth is available on the device and it is enabled. If not,
		// displays a dialog requesting user permission to enable Bluetooth.
		if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) 
		{
			Log.e("Bluetooth error", "Enable bluetooth in settings");
		    //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		scanLeDevice(true);
		
	}
	
	private void scanLeDevice(final boolean enable) {
        if (enable) 
        {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() 
            {
                @Override
                public void run() 
                {
                    mScanning = false;
                    //bluetoothAdapter.stopLeScan(mLeScanCallback);
                    bls.stopScan(scanCallback);
                    textView.setText("Timeout");
                }
            }, SCAN_PERIOD);

            mScanning = true;
            //bluetoothAdapter.startLeScan(scanCallback);
            bls.startScan(scanCallback);
        }
        else 
        {
            mScanning = false;}
            //bluetoothAdapter.stopLeScan(mLeScanCallback);
        	bls.stopScan(scanCallback);
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
}
