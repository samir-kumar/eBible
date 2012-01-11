package com.ebible.android.asyncthread;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ebible.android.constants.EBibleConstants;
import com.ebible.android.interfaces.OnAssetsProcess;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This class used to access the asset data 
 * according to the query by the user.
 * Works as a separate thread to read assets 
 * data and provide response or error to the activity.
*/
public class AssetsDataAsync extends AsyncTask<Void, Void, Void>
{
/**Declare variables*/
	Context context;
	
	int activityId;
	String filename = "", assetResult = "";
	
	ProgressDialog progress;
	
	OnAssetsProcess listener;

/**
 * Constructor definition
 * @param Activity context, asset file path,
 * @param Activity id, listener object
*/
	public AssetsDataAsync(Context cxt, int activityNo, String assetsFilename,
			OnAssetsProcess assetListenerObject) 
	{
		context = cxt;
		activityId = activityNo;
		filename = assetsFilename;
		listener = assetListenerObject; 
		
		//initialize the progress dialog
		progress = new ProgressDialog(context);
		progress.setMessage("Please wait.....");
	}
	
/**
 * AsyncTask functionality
*/
	//at the time of processing
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//start the progress dialog that indicated 
		//that the work is on progress
		progress.show();
	}

	//after the fetching of the data
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		//check if listener not null then
		//provide response to the activity 
		//other wise error
		if(listener != null)
		{
			listener.onAssetsResponse(true, assetResult, activityId);
		}
		else	//asset data response error
		{
			listener.onAssetsError(activityId, "Error accessing Assets data");
		}
		//close the progress dialog if running
		if(progress.isShowing())
		{
			progress.dismiss();
		}
	}

	//in the background
	@Override
	protected Void doInBackground(Void... params) 
	{
		AssetManager manager= context.getAssets();
		try
		{
			 InputStream is = manager.open(filename);
			
			Log.i(EBibleConstants.APPTAG,"asset file = /n");
			
		     // We guarantee that the available method returns the total 
			 // size of the asset...  of course, this does mean that single 
			 // asset can't be more than 2 gigs. 
			            int size = is.available(); 
			            // Read the entire asset into a local byte buffer. 
			            byte[] buffer = new byte[size]; 
			            is.read(buffer); 
			            is.close(); 
			            // Convert the buffer into a string. 
			            assetResult = new String(buffer); 
			            Log.i(EBibleConstants.APPTAG,"asset data string = \n"+assetResult);
			           			           
		}
		catch(Exception ex){
			Log.e(EBibleConstants.APPTAG,"Error in asset file selection");
		}
		return null;
	}

}
