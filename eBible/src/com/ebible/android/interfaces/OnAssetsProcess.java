package com.ebible.android.interfaces;

/**
 * This interface works with Assets folder data of the 
 * application also need a thread named AssetsDataAsync
 * to access to Assets folder.
*/
public interface OnAssetsProcess {

	void onAssetsResponse(boolean isOk, String response, int activityId);
	void onAssetsError(int activityId, String message);
}
