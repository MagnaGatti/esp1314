package com.example.learntousefragments;

import android.util.Log;

public class Parameters {

	// ----- CAMPI PER I PARAMETRI -----
	public static final String X_ASIX = "xAsix";
	public static final String Y_ASIX = "yAsix";
	public static final String Z_ASIX = "zAsix";
	
	private static int size = 3;
	
	private boolean x;
	private boolean y;
	private boolean z;
	private byte[] byteParameters; 
	
	public Parameters(boolean xAsix, boolean yAsix, boolean zAsix){
		byteParameters = new byte[size]; // array tutto zero
		if(xAsix)
			byteParameters[0] = 1;
		if(yAsix)
			byteParameters[1] = Byte.MAX_VALUE;
		if(zAsix)
			byteParameters[2] = Byte.MAX_VALUE;
		for(int i=0; i<size; i++){
			Log.d("PARAMETERS BYTE[]", Byte.toString(byteParameters[i]));
		}		
		x = xAsix;
		y = yAsix;
		z = zAsix;		
	}
	
	public byte[] getByteArray(){
		byte[] result = new byte[size];
		for(int i=0; i<size; i++)
			result[i] = byteParameters[i];				
		return result;
	}

}