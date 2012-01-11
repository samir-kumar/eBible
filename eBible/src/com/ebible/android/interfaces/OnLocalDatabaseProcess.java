package com.ebible.android.interfaces;

import android.database.Cursor;

/**
 * This interface works with local application 
 * database communication.
 * LocalDatabaseAsync thread is used to call 
 * this interface for the related activity.
*/
public interface OnLocalDatabaseProcess {

	void onLocalDatabseResponse(int activityId, boolean isOk, Cursor cursor);
	void onLocalDatabaseResponseError(int activityId, String message);
}
