package com.ebible.android.supportingclasses;

import com.ebible.android.database.EBibleLocalDatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * this class used to get values from local database that has to be highlighted
*/
public class RetrieveValueFromDb 
{
/**Declare class variables*/
	EBibleLocalDatabase database;
	SQLiteDatabase dRead;
	Cursor cursor;
/**
 * Constructor definition
 * @param activity context
 * @param verse number, chapter number
 * @param book name
*/
	public RetrieveValueFromDb(Context context) 
	{
		//initiate and open db
		database = new EBibleLocalDatabase(context);
		dRead = database.getReadableDatabase();
		
		
	}  
	
	//Functionality to retrieve data from the database
	public Cursor retrieveValues(String verseno, 
			String chapterno, String bookname)   
	{
		String[] coloms = {"DISTINCT _id, DESCRIPTION, VERSENO, CHAPTERNO, BOOKNAME, " +
		"COLORCODE, ISHIGHLIGHTED, ISUNDERLINE"};
		
		String whereCondition = "VERSENO="+verseno+" AND CHAPTERNO="+chapterno+" " +
				"AND BOOKNAME='"+bookname+"' AND ISHIGHLIGHTED=1";
		
		cursor = dRead.query("TEXTHIGHLIGHT", coloms, whereCondition, null, null, null, null);
		int count = cursor.getCount();
		Log.i("","count = "+count);   
		return cursor;
	}
	
	//close the cursor and database
	public void closeDb()
	{
		if(cursor != null)
		{
			cursor.close();
			cursor = null;
		}
		if(database != null)
		{
			database.close();
		}
	}
}
