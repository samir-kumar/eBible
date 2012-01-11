package com.ebible.android.interfaces;

import android.app.Dialog;

/**
 * This interface used to implement the functionality 
 * according to the xml layout of a dialog is displayed 
 * on the screen.
*/
public interface OnEBibleDialogProcess {

	void onEBibleDialogShown(Dialog dialog, int layoutId, String listName);
	void onEBibleDialogError(String message);
}
