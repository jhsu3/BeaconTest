package com.example.beacontest.model;

import android.util.Log;

public class RawScanRecord 
{
	//iBeacon prefix
	//02 01 06 1A FF 4C 00 02 15
	byte dataLength;
	byte dataTypeFlags;
	byte LEflag;
	byte dataLength2;
	byte dataTypeManufacturer;
	byte[] manufacturerData = new byte[4];	//Fixed from here up
	byte[] UUID = new byte[16];
	byte[] major = new byte[2];
	byte[] minor = new byte[2];
	byte signalPower;						//Two's complement of measured power
	
	public RawScanRecord(byte dataLength, byte dataTypeFlags, byte LEflag, 
			byte dataLength2, byte dataTypeManufacturer, byte[] manufacturerData, byte[] UUID, 
			byte[] major, byte[] minor, byte signalPower)
	{
		this.dataLength = dataLength;
		this.dataTypeFlags = dataTypeFlags;
		this.LEflag = LEflag;
		this.dataLength2 = dataLength2;
		this.dataTypeManufacturer = dataTypeManufacturer;
		this.manufacturerData = manufacturerData;
		this.UUID = UUID;
		this.major = major;
		this.minor = minor;
		this.signalPower = signalPower;
	}
	
	public String toString()
	{
		String result = "Data Length "+String.format("%02X", dataLength)+"\n"
				+"Data type – flags "+String.format("%02X", dataTypeFlags)+"\n"
				+"LE and BR/EDR flag "+String.format("%02X", LEflag)+"\n"
				+"Data length – 26 bytes "+String.format("%02X", dataLength2)+"\n"
				+"Data type - manufacturer specific data "+String.format("%02X", dataTypeManufacturer)+"\n"
				+"Manufacturer data "+formatByteArray(manufacturerData)+"\n"
				+"UUID "+formatByteArray(UUID)+"\n"
				+"Major "+formatByteArray(major)+"\n"
				+"Minor "+formatByteArray(minor)+"\n"
				+"Signal power (calibrated RSSI) "+String.format("%02X", signalPower)+"\n";
		return result;
	}
	
	public String formatByteArray(byte[] in)
	{
		String result = new String();
		for(int i=0; i<in.length; i++)
		{
			result = result.concat(String.format("%02X", in[i]));
		}
		return result;
	}
}
