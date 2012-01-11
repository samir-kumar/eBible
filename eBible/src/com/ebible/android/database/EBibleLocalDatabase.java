package com.ebible.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This activity used to create the local database if not exist,
 * basically to highlight the text. Initially this will store the 
 * highlighted texts according to asset file.
*/
public class EBibleLocalDatabase extends SQLiteOpenHelper {
/**
 * Declare class variables
*/
	private static String dbName = "eBibleLocalDB";
	private static int dbVerion = 1;
	
	public EBibleLocalDatabase(Context context) {
		super(context, dbName, null, dbVerion);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Create highlight text table 
		//this table store the highlighted texts of verses 
		//and show to the user
		String createHighlightTextTable = "CREATE TABLE TEXTHIGHLIGHT (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
				"DESCRIPTION TEXT, VERSENO INTEGER, CHAPTERNO INTEGER, BOOKNAME TEXT, " +
				"COLORCODE TEXT, ISHIGHLIGHTED BOOLEAN, ISUNDERLINE BOOLEAN)";
		
		db.execSQL(createHighlightTextTable);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
