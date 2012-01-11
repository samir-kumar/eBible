package com.ebible.android.constants;

import java.util.ArrayList;

/**
 * This class store all the constant value of the eBible app
*/
public class EBibleConstants {

	//Application name tag/name space
	public static final String APPTAG = "eBible";
	
	//Database DML queries constants
	public static final int DATA_INSERT = 1001;
	public static final int DATA_UPDATE = 1002;
	public static final int DATA_RETRIEVE = 1003;
	public static final int DATA_DELETE = 1004;
	
	//Activity or data files ids
	public static final int DESCRIPTION_ACTIVITY = 1;
	public static final int HIGHLIGHTEDTEXTVIEW_ACTIVITY = 2;
	
	public static final int CHAPTER_TABLE = 101;
	
	//page id to display verses according to the pages
	public static int pageNumber;
	
	//store the paragraph last value
	public static int lastParaValue;
	
	//store the paragraph starting index value
	public static int startParaNumber;
	
	//array list for the book description
	public static ArrayList<String> paragraphList, contentList, verseList;
	
	//array list to show on the highlighted text page
	public static ArrayList<String> verseListToHighlight, contentListToHighlight, 
										paragraphListToHighlight;
	
	//Device height and width
	public static int DEVICE_HEIGHT;
	public static int DEVICE_WIDTH;
	
	//find the the highlighted dialog has been opened
	public static boolean isHighlightedDialogPassed;
	
	//condition to check that highlighted has been done
	public static boolean isHighLightedDone;
	
	//condition to check that highlighted is removed/not
	public static boolean isHighlightedRemoved;
	
	//store the selected color value by default yellow
	public static int selectedColorToHighlight = 0xFFFFFF00; 
	
	//store the value to underline texts
	public static boolean isUnderline;
	
	//store the last opened file path
	public static String lastOpenedFilePath = "";
	public static int lastOpenendPagenumber;
}
