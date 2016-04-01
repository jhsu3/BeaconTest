package com.example.beacontest.model;

public class RawScanRecord 
{
	//iBeacon prefix
	//02 01 06 1A FF 4C 00 02 15
	byte[] dataLength0 = new byte[1];				//x02
	byte[] dataTypeFlags = new byte[1];				//x01
	byte[] LEflag = new byte[1];					//x06 (LE general discoverable mode)
	byte[] dataLength1 = new byte[1];				//x1A 26 bytes
	byte[] dataTypeManufacturer = new byte[1];		//xFF Custom payload (iBeacon)
	byte[] manufacturerData = new byte[4];			//Fixed from here up	0x4C000215	//Apple identifier x004C	//02 proximity beacon ADV_NONCONN_IND //15 remaining length 21 bytes
	byte[] UUID = new byte[16];						//128 bit UUID
	byte[] major = new byte[2];						//2 byte major
	byte[] minor = new byte[2];						//2 byte minor
	byte[] signalPower = new byte[1];				//Two's complement of measured power
	
	public RawScanRecord(byte[] dataLength0, byte[] dataTypeFlags, byte[] LEflag, 
			byte[] dataLength1, byte[] dataTypeManufacturer, byte[] manufacturerData, byte[] UUID, 
			byte[] major, byte[] minor, byte[] signalPower)
	{
		this.dataLength0 = dataLength0;
		this.dataTypeFlags = dataTypeFlags;
		this.LEflag = LEflag;
		this.dataLength1 = dataLength1;
		this.dataTypeManufacturer = dataTypeManufacturer;
		this.manufacturerData = manufacturerData;
		this.UUID = UUID;
		this.major = major;
		this.minor = minor;
		this.signalPower = signalPower;
	}
	
	public String toString()
	{
		String result = "AD0 length "+formatByteArray(dataLength0)+"\n"
				+"AD0 type "+formatByteArray(dataTypeFlags)+"\n"
				+"Payload (Advertising type flags) "+formatByteArray(LEflag)+"\n"
				+"AD1 length "+formatByteArray(dataLength1)+"\n"
				+"AD1 type "+formatByteArray(dataTypeManufacturer)+"\n"
				+"Manufacturer data "+formatByteArray(manufacturerData)+"\n"
				+"UUID "+formatByteArray(UUID)+"\n"
				+"Major "+formatByteArray(major)+"\n"
				+"Minor "+formatByteArray(minor)+"\n"
				+"Signal power (calibrated RSSI) "+formatByteArray(signalPower)+"\n";
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
