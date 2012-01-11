package com.ebible.android.supportingclasses;

import com.ebible.android.constants.EBibleConstants;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * This class used to open assets file and convert 
 * this json file into text strings to be displayed.
*/
public class FindAssetsValues {
/**Declare class variables*/
	Context context;
/**
 * Constructor definition
 * @param Activity context, 
 * @param Asset's file path
*/
	public FindAssetsValues(Context cxt, String assetFileName) {
		context = cxt;
		AssetManager manager= this.context.getAssets();
		try
		{
			manager.open(assetFileName);
			
			Log.i(EBibleConstants.APPTAG,"asset file = /n");
			
		}
		catch(Exception ex){
			Log.e(EBibleConstants.APPTAG,"Error in asset file selection");
		}

	}
}
