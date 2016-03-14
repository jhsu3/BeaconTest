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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	BluetoothAdapter bluetoothAdapter;
	BluetoothLeScanner bls;
	ScanCallback scanCallback;
	private static final long SCAN_PERIOD = 10000;
	
    private boolean mScanning;
    private Handler mHandler;
    
    TextView textView;
    Button scanButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textView = (TextView) findViewById(R.id.textView);
		scanButton = (Button) findViewById(R.id.button);
		mHandler = new Handler();
		
		// Use this check to determine whether BLE is supported on the device. Then
		// you can selectively disable BLE-related features.
		/*if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
		    Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
		    finish();
		}*/
		
		// Initializes Bluetooth adapter.
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		//bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		bls = bluetoothAdapter.getBluetoothLeScanner();
		scanCallback = new ScanCallback()
		{
			@Override
			public void onScanResult(int callbackType, ScanResult result)
			{
				//super.onScanResult(callbackType, result);
				textView.setText(result.toString());
				Log.d("onScanResult", result.toString());
			}
			
			@Override
			public void onScanFailed(int errorCode)
			{
				Log.d("onScanFailed", "errorCode "+errorCode);
				//super.onScanFailed(errorCode);
				textView.setText("onScanFailed "+errorCode);
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
		
		scanButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				//Log.d("onClick", "clicked");
				textView.setText("Scanning...");
				scanLeDevice(true);
			}
		});
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

            //mScanning = true;
            //bluetoothAdapter.startLeScan(scanCallback);
            bls.startScan(scanCallback);
        }
        else 
        {
            mScanning = false;
        }
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
