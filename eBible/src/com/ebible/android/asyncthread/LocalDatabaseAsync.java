package com.ebible.android.asyncthread;

import java.net.URLEncoder;

import com.ebible.android.BookDescriptionActivity;
import com.ebible.android.HighlightedTextViewActivity;
import com.ebible.android.constants.EBibleConstants;
import com.ebible.android.database.EBibleLocalDatabase;
import com.ebible.android.interfaces.OnLocalDatabaseProcess;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This class is used as a background thread to communicate 
 * with local database and provide database response or error 
 * to the related activity according to query.
*/
public class LocalDatabaseAsync extends AsyncTask<Void, Void, Void>{
	/**Declare variables*/
	Context context;
	
	int activityId, requestedQueryId;
	String tableName = "", where, orderBy;
	boolean isSuccess;
	
	String[] columns, dbInsertValueArr;
	
	ProgressDialog progress;
	
	EBibleLocalDatabase appDb ;
	SQLiteDatabase dwrite, dread;
	Cursor cursor;
	
	OnLocalDatabaseProcess listener;
	
	ContentValues value;
	/**
	 * Constructor Definition to work with different Activities
	 * @param activity context, activity number, where condition
	 * @param columns demanded, table name
	*/
	public LocalDatabaseAsync(Context cxt, int actId, OnLocalDatabaseProcess appDbInterfaceContext,
			int dBDMLId, String dbTableName, String[] coloms, String whereCondition, ContentValues values, 
			String inOrderBy, String[] dbInsertArr) 	
	
	{
		context = cxt;
		activityId = actId;
		listener = appDbInterfaceContext;
		tableName = dbTableName;
		requestedQueryId = dBDMLId;
		where = whereCondition;
		orderBy = inOrderBy;
		value = values;
		
		
		//initialize String array to store the values into array
		if(dbInsertArr != null)
		{
		dbInsertValueArr = new String[dbInsertArr.length];
		//copy the values into the local string array
		System.arraycopy(dbInsertArr, 0, dbInsertValueArr, 0, dbInsertArr.length);
		}
		
		//initialize String array to store the columns name
		if(coloms != null)
		{		
		columns = new String[coloms.length+1];
		//copy the columns name into the local string array
		System.arraycopy(coloms, 0, columns, 0, coloms.length);
		}
		
		//initialize progress dialog for current task
		progress = new ProgressDialog(context);
		progress.setMessage("Please wait.....");
		
		//Initialize the application database
		appDb = new EBibleLocalDatabase(context);
		dread = appDb.getReadableDatabase();
		dwrite = appDb.getWritableDatabase();
	}	
	//while database processing with data
	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
		//start the progress dialog that indicated 
		//that the work is on progress
		progress.show();
	}
	//while database finished the particular process
	@Override
	protected void onPostExecute(Void result) 
	{
		super.onPostExecute(result);
		//check the interface listener
		if(listener != null)
		{
			listener.onLocalDatabseResponse(activityId, isSuccess, cursor);
		}
		else
		{
			listener.onLocalDatabaseResponseError(activityId, "Listener not Attached or NULL for Application database");
		}		
		
		//close the cursor
		if(cursor != null)				//close the database
		{
			cursor.close();
			cursor = null;
		}
		//close the application database
		if(appDb != null)				//close the database
		{
			   appDb.close();
		}
		
		//close the progress dialog if running
		if(progress.isShowing())
		{
			progress.dismiss();
		}
	}
	//what have to be done with this async thread 
	//in the background through the application database
	@Override
	protected Void doInBackground(Void... params)           
	{
		if(requestedQueryId > 1000 && requestedQueryId < 1005)
		{
		switch(requestedQueryId)
		{
		case EBibleConstants.DATA_INSERT:					//insert records			
			((Activity) context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					try
					{
						ContentValues valuess;
						/*if((dbInsertValueArr != null) &&
								(dbInsertValueArr.length > 0))
						{
							for(int countText = 0; countText < dbInsertValueArr.length; countText++)
							{
								valuess = new ContentValues();
							    valuess.put("DESCRIPTION", dbInsertValueArr[countText]);
							    valuess.put("VERSENO", BookDescriptionActivity.verseHighlightedPositionToStore);
							    valuess.put("CHAPTERNO", BookDescriptionActivity.selectedChapterPosition);
							    valuess.put("BOOKNAME", BookDescriptionActivity.selectedBookName);
							    valuess.put("COLORCODE", URLEncoder.encode(""+EBibleConstants.selectedColorToHighlight));
							    valuess.put("ISHIGHLIGHTED", true);
							    valuess.put("ISUNDERLINE", EBibleConstants.isUnderline);
							    
								long inserted = dwrite.insert(tableName, null, valuess);
								
								Log.i("eBible local db","value inserted no = "+inserted);
							}
							{isSuccess = true;}
						}*/
						int inserted = (int) dwrite.insert(tableName, null, value);
						if(inserted > 0)
							{isSuccess = true;}
					}
					catch (Exception e) {
						Log.e("APPLICATION_DB_INSERT",""+e.toString());
					}
					
				}
			});
			
			
			break;
		case EBibleConstants.DATA_UPDATE:					//update records
			break;
		case EBibleConstants.DATA_RETRIEVE:				//fetch records
			try{
			cursor =  dread.query(tableName,columns ,where ,null, null,  null, orderBy);	
			if(cursor != null)
				{isSuccess = true;}
			}catch (Exception e) {
				Log.e("LOCAL_DB_RETRIEVE",""+e.toString());
			}
			break;
		case EBibleConstants.DATA_DELETE:					//delete records
			int deleted = dwrite.delete(tableName, where, null);
			if(deleted > 0)
			{isSuccess = true;}
			break;
		}
		}
		else
		{
			listener.onLocalDatabaseResponseError(activityId, "Undefined request query error");
		}
		return null;
	}
}
