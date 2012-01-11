package com.ebible.android.dialogs;

import com.ebible.android.constants.EBibleConstants;
import com.ebible.android.interfaces.OnEBibleDialogProcess;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

/**
 * This class used to display the dialogs that 
 * inflate the xml layouts and functions accordingly.
 * This class works with OnEBibleDialogProcessListener 
 * and call back the activity to allow mutual functionality 
 * with dialog widgets.
*/
public class EBibleDialog {
/**
 * Declare class variables
*/
	Context context;
	int layoutToDisplay;
	String nameOfList;
	OnEBibleDialogProcess dialogListener;
	
/**
 * Constructor definition
 * @param activity context
 * @param layout id that has to be displayed
*/
	public EBibleDialog(Context cxt, OnEBibleDialogProcess listenerObject, 
			int layoutId, String listName) {
		context = cxt;
		layoutToDisplay = layoutId;
		dialogListener = listenerObject;
		nameOfList = listName;
		//start the dialog class to display a dialog
		dialogShow();
	}
/**
 * This function shows the dialog and callback to activity 
 * respect to interface listener.
*/ 
	void dialogShow()
	{
		//initialize the Dialog
		Dialog dialog = new Dialog(context);
		//hide the title bar
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//set the main view of this dialog
		dialog.setContentView(layoutToDisplay);
		//set the dimention of the dialog
		dialog.getWindow().setLayout(EBibleConstants.DEVICE_WIDTH-30, LayoutParams.WRAP_CONTENT);
		//set the gravity of the dialog
		dialog.getWindow().getAttributes().gravity = Gravity.LEFT;
		//show the dialog
		dialog.show();
		
		if(dialog.isShowing())	
		{
			//if listener attached proper then call back to activity
			if(dialogListener != null)
			{
				dialogListener.onEBibleDialogShown(dialog, layoutToDisplay, nameOfList);
			}
			else	//listener not attached proper
			{
				//error on dialog attached
				dialogListener.onEBibleDialogError("Dialog listener not attached");
				//close the dialog
				dialog.dismiss();
			}
			
		}
		
	}
}
