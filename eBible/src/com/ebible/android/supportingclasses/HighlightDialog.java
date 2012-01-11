package com.ebible.android.supportingclasses;

import com.ebible.android.BookDescriptionActivity;
import com.ebible.android.HighlightedTextViewActivity;
import com.ebible.android.R;
import com.ebible.android.constants.EBibleConstants;
import com.ebible.android.database.EBibleLocalDatabase;
import com.ebible.android.dialogs.EBibleDialog;
import com.ebible.android.interfaces.OnEBibleDialogProcess;
import com.ebible.android.interfaces.OnLocalDatabaseProcess;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
/**
 * This class shows the dialog for highlighted text 
 * and perform different events according to attached event handler 
 * to the widgets of the dialog view.
*/
public class HighlightDialog implements OnClickListener, 
				OnEBibleDialogProcess, OnCheckedChangeListener{
/**Declare variables*/
	Context context;
	
	String bookName = "", chapterName = "";
	int versePosition = -1;
	
	TextView title, viewHighlight, closeText, removeHighlighted;
	ImageView redColorImg, pinkColorImg, yellowColorImg;
	RadioGroup underlineRG;
	RadioButton yesRD, noRD;
	
	Dialog dialogLocal;
/**
 * Constructor definition
 * @param activity context
 * @param book name, chapter name, verse number
*/
	public HighlightDialog(Context cxt, String book, 
			String chapter, int verseNo) 
	{
		context = cxt;
		bookName = book;
		chapterName = chapter;
		versePosition = verseNo;
		
		//reset the value to underline texts
		EBibleConstants.isUnderline = false;
		
		//start the dialog of options to highlight texts
		new EBibleDialog(context, this, R.layout.highlight_box, null);
	}
	
/**Functionality after showing the dialog to highlight*/
	@Override
	public void onEBibleDialogShown(Dialog dialog, int layoutId, String listName) {
		if(dialog != null)
		{				
			dialogLocal = dialog;
			//initialize the variables/widgets for this dialog view
			title = (TextView)dialog.findViewById(R.id.highlight_title_txt);
			viewHighlight = (TextView)dialog.findViewById(R.id.highlight_viewhigh_txt);
			closeText = (TextView)dialog.findViewById(R.id.highlight_close_txt);
			removeHighlighted = (TextView)dialog.findViewById(R.id.highlight_removehigh_txt);
			redColorImg = (ImageView)dialog.findViewById(R.id.highlight_redcolor_img);
			pinkColorImg = (ImageView)dialog.findViewById(R.id.highlight_pinkcolor_img);
			yellowColorImg = (ImageView)dialog.findViewById(R.id.highlight_yellowcolor_img);
			underlineRG = (RadioGroup)dialog.findViewById(R.id.highlight_underline_radiogrp);
			yesRD = (RadioButton)dialog.findViewById(R.id.highlight_yes_radio);
			noRD = (RadioButton)dialog.findViewById(R.id.highlight_no_radio);
			
			//set click events for the dialog view's widgets
			viewHighlight.setOnClickListener(this);
			removeHighlighted.setOnClickListener(this);
			redColorImg.setOnClickListener(this);
			pinkColorImg.setOnClickListener(this);
			yellowColorImg.setOnClickListener(this);
			closeText.setOnClickListener(this);
			
			yesRD.setOnCheckedChangeListener(this);
			noRD.setOnCheckedChangeListener(this);
			
			//show the book name into upper case
			bookName = bookName.toUpperCase();
			//set the title of the highlight box
			title.setText(bookName+" "+chapterName+":"+
					EBibleConstants.verseListToHighlight.get(versePosition));
			
			//by default no option is selected for the underline
			noRD.setChecked(true);
		}
		else
		{
			onEBibleDialogError("Highlight view Error: Dialog is null");
		}
		
	}
	
/**If Dialog showing have errors*/
	@Override
	public void onEBibleDialogError(String message) {
		Log.e(EBibleConstants.APPTAG,"Highlight view dialog error "+message);
		
	}
	
/**Click events for the widgets*/
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.highlight_viewhigh_txt:		//view highlighted text
			//open the activity to show the highlighted texts
			context.startActivity(new Intent(context, HighlightedTextViewActivity.class));
			break;
		case R.id.highlight_redcolor_img:		//red color image
			EBibleConstants.selectedColorToHighlight = 0xFFFF0000;
			break;
		case R.id.highlight_pinkcolor_img:		//pink color image
			EBibleConstants.selectedColorToHighlight = 0xFFFF3366;
			break;
		case R.id.highlight_yellowcolor_img:	//yellow color image
			EBibleConstants.selectedColorToHighlight = 0xFFFFFF00;
			break;
		case R.id.highlight_close_txt:			//close text
			if(dialogLocal != null &&
				dialogLocal.isShowing())
			{
				dialogLocal.dismiss();
			}
			break;
		case R.id.highlight_removehigh_txt:		//remove highlighted
			removeHighlightButtonClicked();
			break;
		}
		
	}

/**Functionality to select radio buttons*/
@Override
public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	if(yesRD.isChecked())
	{
		EBibleConstants.isUnderline = true;
	}
	else 
	{
		EBibleConstants.isUnderline = false;
	}
	
}

/**
 * Functionality of remove highlight button
*/
void removeHighlightButtonClicked()
{
	EBibleLocalDatabase database = new EBibleLocalDatabase(context);
	SQLiteDatabase dWrite = database.getWritableDatabase();
	
	String whereCondition = "VERSENO="+BookDescriptionActivity.verseHighlightedPositionToStore+
			" AND CHAPTERNO="+BookDescriptionActivity.selectedChapterPosition+
			" AND BOOKNAME='"+BookDescriptionActivity.selectedBookName+"'";
	ContentValues values = new ContentValues();
	values.put("ISHIGHLIGHTED", 0);
	//update query for highlight
	//dWrite.update("", values, whereCondition, null);
	//delete the highlight entry from db for the selected verse number
	dWrite.delete("TEXTHIGHLIGHT", whereCondition, null);
	
	EBibleConstants.isHighlightedRemoved = true;
	
	//close the openend dialog box
	if(dialogLocal != null &&
	dialogLocal.isShowing())
	{
	dialogLocal.dismiss();
	}
	//close the opened db
	if(database != null)
	{
		database.close();
	}
	
	//close the activity and start again
	/*((BookDescriptionActivity) this.context).onResume();*//*
	context.startActivity(new Intent(context, BookDescriptionActivity.class));*/
}
}
