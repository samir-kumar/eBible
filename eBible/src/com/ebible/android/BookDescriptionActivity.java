package com.ebible.android;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ebible.android.asyncthread.AssetsDataAsync;
import com.ebible.android.asyncthread.LocalDatabaseAsync;
import com.ebible.android.constants.EBibleConstants;
import com.ebible.android.dialogs.EBibleDialog;
import com.ebible.android.interfaces.OnAssetsProcess;
import com.ebible.android.interfaces.OnEBibleDialogProcess;
import com.ebible.android.interfaces.OnLocalDatabaseProcess;
import com.ebible.android.supportingclasses.HighlightDialog;
import com.ebible.android.supportingclasses.RetrieveValueFromDb;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView.BufferType;

/**
 * This class will show the description page of the books. 
 * From here user can also go for the chapter selection of 
 * this book and can also select respective verse.
 * This class also implements the Custom interface for different functionality
*/ 
public class BookDescriptionActivity extends Activity implements 
				OnClickListener, OnEBibleDialogProcess, OnAssetsProcess{//, OnLocalDatabaseProcess{
/**
 * Declare variables
*/
	TextView upperHeading, heading, chapterHeading;//, description;
	ImageView searchImg;
	Button next, previous;
	ListView descList;
	
	String[] bookNameArr = {
			"2Timothy", "Acts", "Amos", "Chronicles1", "Chronicles2", "Colossians", 
			"Corinthians1", "Corinthians2", "Daniel", "Deuteronomy",
			"Ecclesiastes", "Ephesians", "Esther", "Exodus", "Ezekiel", "Ezra", "Galatians",
			"Genesis", "Habakkuk", "Haggai", "Hebrews", "Hosea", "Isaiah", "James", "Jeremiah", 
			"Job", "Joel", "John", "John1", "John2", "John3", "Jonah", "Joshua", "Jude", "Judges", 
			"Kings1", "Kings2", "Lamentations", "Leviticus", "Luke", "Malachi", "Mark", "Matthew", 
			"Micah", "Nahum", "Nehemiah", "Numbers", "Obadiah", "Peter1", "Peter2", "Philemon", 
			"Philippians", "Proverbs", "Psalms", "Revelation", "Romans", "Ruth", "Samuel1", "Samuel2", 
			"Songofsolomon", "Thessalonians1", "Thessalonians2", "Timothy1", "Titus", "Zechariah", "Zephaniah"};
		
	String descriptionString = "", selectedChapterName = "", selectedBookNameForHeading = "",
			bookNameHeading = "", chapterNumberHeading = "", sectionTitleHeading = "";
			
	int selectedBookPosition = -1;
	boolean isChapterDisplaying;
	
	public static int versePositionToHighlighten = -1;
	public static String selectedBookName = "", selectedChapterPosition = "",
			verseHighlightedPositionToStore = "";
	
	ArrayList<String> searchBookNameArr, searchChapterNameArr, searchVerseNameArr;
	ArrayList<String> highlightedMatchedColorCodeArr, highlightedMatchedDescriptionArr,
						highlightedMatchedUnderlineArr, highlightedMatchedVerseArr;
	
	RetrieveValueFromDb rvFromDb;
	
	Dialog openedDialog;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);  
	
	    //hide the title bar
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    //set the main view for this activity
	    setContentView(R.layout.book_description);
	}

	@Override
	protected void onResume() {
	super.onResume();
	//check that this activity is backed from highlighted dialog
	if(EBibleConstants.isHighlightedDialogPassed && 
			!EBibleConstants.isHighLightedDone)
	{
		EBibleConstants.isHighlightedDialogPassed = false;
	}  
	//check that verse is highlighted by user
	else if(EBibleConstants.isHighLightedDone ||
			EBibleConstants.isHighlightedRemoved)
	{
		//reset the values
		EBibleConstants.isHighlightedRemoved = false;
		//EBibleConstants.isHighlightedDialogPassed = false;
		//set the book name and chapter name for first use
		selectedBookName = selectedBookName;
		selectedChapterPosition = selectedChapterPosition;
		//show the current verses with highlighted texts
		displayVerseText();
		/*else
		{*/
		//get the data from the assets
		//new AssetsDataAsync(this, EBibleConstants.CHAPTER_TABLE, EBibleConstants.lastOpenedFilePath, this).execute();
//		new AssetsDataAsync(this, EBibleConstants.DESCRIPTION_ACTIVITY, "genesis/1.json", this).execute();
	}
	else
	{
		//initialize the widgets and variables
		initialization();
		//store globally
		EBibleConstants.lastOpenedFilePath = "genesis/1.json";
		//set the book name and chapter name for first use
		selectedBookName = "Genesis";
		selectedChapterPosition = "1";
		//get the data from the assets
		new AssetsDataAsync(this, EBibleConstants.DESCRIPTION_ACTIVITY, "genesis/1.json", this).execute();

	}
}
	
/**
 * Click event for the different widgets
*/
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.book_desc_search_img:				//search image
			//clear the stored value for last view
			isChapterDisplaying = false;
			//clear the last stored book values
			selectedBookPosition = -1;
			selectedBookName = "";
			//start the dialog class to show 
			//the view of the dialog
			new EBibleDialog(BookDescriptionActivity.this, this, R.layout.book_list, null);
			break;
		case R.id.book_desc_upper_small_heading:	//upper small heading
			//start the dialog class to show 
			//the view of the dialog
			new EBibleDialog(BookDescriptionActivity.this, this, R.layout.book_list, null);
			break;
		case R.id.book_desc_next_btn:				//next verse page button
			EBibleConstants.pageNumber += 1;
			//display the text according to paging
			displayVerseText();
			break;
		case R.id.book_desc_prev_btn:				//previous verse page button
			EBibleConstants.pageNumber -= 1;
			//display the text according to paging
			displayVerseText();
			break;
		}
	}

/**
 * Initialize the widgets and variables
*/
	void initialization()
	{
		descList = (ListView)findViewById(R.id.book_desc_list);
		
		searchImg = (ImageView)findViewById(R.id.book_desc_search_img);
		
		upperHeading = (TextView)findViewById(R.id.book_desc_upper_small_heading);
		heading = (TextView)findViewById(R.id.book_desc_heading_txt);
		chapterHeading = (TextView)findViewById(R.id.book_desc_chapter_heading_txt);		
		
		next = (Button)findViewById(R.id.book_desc_next_btn);
		previous = (Button)findViewById(R.id.book_desc_prev_btn);
		
		//set the listener for the widgets
		searchImg.setOnClickListener(this);
		upperHeading.setOnClickListener(this);
		next.setOnClickListener(this);
		previous.setOnClickListener(this);
		
		//initialize the array list of searched books
		searchBookNameArr = new ArrayList<String>();
		searchChapterNameArr = new ArrayList<String>();
		searchVerseNameArr = new ArrayList<String>();	
		
		highlightedMatchedDescriptionArr = new ArrayList<String>();
		highlightedMatchedColorCodeArr = new ArrayList<String>();
		highlightedMatchedUnderlineArr = new ArrayList<String>();
		highlightedMatchedVerseArr = new ArrayList<String>();
		
		rvFromDb = new RetrieveValueFromDb(this);
	}

//interface functions to show the dialog and work with their widgets
	
@Override
public void onEBibleDialogShown(Dialog dialog, int layoutId, String nameOfList) {
	if(dialog != null)
	{
		Log.i(EBibleConstants.APPTAG,"Displaying the dialog");
		switch(layoutId)
		{
		case R.layout.book_list:			//if book list dialog is open
			//functionality to show book list and perform search 
			//and other functionality on this view
			bookDialogListDisplayedFunc(dialog);
			break;
		case R.layout.chapter_list_grid:	//if chapter grid list dialog
			if(nameOfList != null && nameOfList.equalsIgnoreCase("chapter"))
			{
				//functionality to show chapter list and perform search 
				//and other functionality on this view
				chapterDialogDisplayedFunc(dialog);
			}
			else if(nameOfList != null && nameOfList.equalsIgnoreCase("verses"))
			{
				//functionality to show verses list and perform search 
				//and other functionality on this view
				verseDialogDisplayedFunc(dialog);
			}
			else
			{
				//no selection is matched with chapter name/verse name
			}			
			break;
		case R.layout.verse_list_grid:
			//functionality to show verses list and perform search 
			//and other functionality on this view
			verseDialogDisplayedFunc(dialog);
			break;
		}
	}
	
}

@Override
public void onEBibleDialogError(String message) {
	Log.e(EBibleConstants.APPTAG,"BookDescActivity Dialog Error: "+message);
	
}

/**
 * Assets folder functionality
*/
@Override
public void onAssetsResponse(boolean isOk, String response, int activityId) {
	if(isOk)
	{       
        JSONObject json;
		try {
			json = new JSONObject(response);
			/*String scriptures = json.getString("scriptures");
			Log.i("","scripture = "+scriptures);*/
			
			JSONObject scriptObject = json.getJSONObject("scriptures");
			JSONArray script = scriptObject.getJSONArray("scriptures");
			Log.i("","script = "+script.length());
				
			//clear the last stored values for the description page headings
			sectionTitleHeading = ""; bookNameHeading = ""; chapterNumberHeading = "";
			
			switch(activityId)
			{
			case EBibleConstants.DESCRIPTION_ACTIVITY:			//description texts
				for(int countResult=0; countResult<script.length(); countResult++)
	            {
	            	String verseNumber = script.getJSONObject(countResult)
	        				.getString("verse").toString();
	            	sectionTitleHeading = script.getJSONObject(countResult)
	        				.getString("section_title").toString();
	            	String paragraphNumber = script.getJSONObject(countResult)
	        				.getString("paragraph").toString();
	                String content = script.getJSONObject(countResult)
	        				.getString("content").toString();
	                bookNameHeading = script.getJSONObject(countResult)
	        				.getString("book").toString();
	                chapterNumberHeading = script.getJSONObject(countResult)
	        				.getString("chapter").toString();
	                Log.i(EBibleConstants.APPTAG, "desc page = "+countResult+1+"\nverse = "+verseNumber+
	                		"\nsection = "+sectionTitleHeading+"\nparagraph = "+paragraphNumber+"\ncontents = "+content);
	            
	             //store the paragraph number
	                EBibleConstants.paragraphList.add(paragraphNumber);
	             //store the contents of the verse
	                EBibleConstants.contentList.add(content);
	             //store the verse number
	                EBibleConstants.verseList.add(verseNumber);
	            }						

				//set the heading text
				upperHeading.setText(bookNameHeading+" "+chapterNumberHeading);
				heading.setText(bookNameHeading);
			    //set the section title
			    chapterHeading.setText(sectionTitleHeading);
				//store the startting index of the paragraph
			    EBibleConstants.startParaNumber = EBibleConstants.pageNumber = Integer.parseInt(EBibleConstants.paragraphList.get(0));
			    //store the paragraph last value
			    EBibleConstants.lastParaValue = Integer.parseInt(EBibleConstants.paragraphList.get(EBibleConstants.paragraphList.size()-1));
				Log.i("","last value of paragraph = "+EBibleConstants.lastParaValue+"\nstart index of paragraph = "
												+EBibleConstants.pageNumber);
			    
				//display the verses texts according to paging
				displayVerseText();
	            
				break;
			case EBibleConstants.CHAPTER_TABLE:			//verse lists
				//clear the arrayLists
                EBibleConstants.paragraphList.clear();
                EBibleConstants.contentList.clear();
                EBibleConstants.verseList.clear();
                //clear chapter values
                isChapterDisplaying = false;
				for(int countResult=0; countResult<script.length(); countResult++)
	            {
	            	String verseNumber = script.getJSONObject(countResult)
	        				.getString("verse").toString();
	            	sectionTitleHeading = script.getJSONObject(countResult)
	        				.getString("section_title").toString();
	            	String paragraphNumber = script.getJSONObject(countResult)
	        				.getString("paragraph").toString();
	                String content = script.getJSONObject(countResult)
	        				.getString("content").toString();
	                bookNameHeading = script.getJSONObject(countResult)
	        				.getString("book").toString();
	                chapterNumberHeading = script.getJSONObject(countResult)
	        				.getString("chapter").toString();
	                Log.i(EBibleConstants.APPTAG, "desc page = "+countResult+1+"\nverse = "+verseNumber+
	                		"\nsection = "+sectionTitleHeading+"\nparagraph = "+paragraphNumber+"\ncontents = "+content);
	            
	             //store the paragraph number
	                EBibleConstants.paragraphList.add(paragraphNumber);
	             //store the contents of the verse
	                EBibleConstants.contentList.add(content);
	             //store the verse number
	                EBibleConstants.verseList.add(verseNumber);
	            }
				
				
				new EBibleDialog(BookDescriptionActivity.this, 
						BookDescriptionActivity.this, R.layout.verse_list_grid, null);
				
				break;
			}
			
            
            
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}

@Override
public void onAssetsError(int activityId, String message) {
	// TODO Auto-generated method stub
	
}

/**
 * Functionality to show the text of the verse to related chapter and book
*/
void displayVerseText()
{    
	//clear the highlighted array list
	EBibleConstants.verseListToHighlight.clear();
	EBibleConstants.paragraphListToHighlight.clear();
	EBibleConstants.contentListToHighlight.clear();
	
	highlightedMatchedDescriptionArr.clear();
	highlightedMatchedColorCodeArr.clear();
	highlightedMatchedUnderlineArr.clear();
	highlightedMatchedVerseArr.clear();
	
	//clear the description string
	descriptionString = "";   
	//clear the position value
	versePositionToHighlighten = -1;
	verseHighlightedPositionToStore = "";
	int pagenumber;
	if(EBibleConstants.isHighlightedDialogPassed)
	{
		EBibleConstants.isHighlightedDialogPassed = false;
		pagenumber = EBibleConstants.lastOpenendPagenumber;
	}
	else
	{	
		//set the text description content with paging
		pagenumber = EBibleConstants.lastOpenendPagenumber = EBibleConstants.pageNumber;
	}
    Log.i("","page number = "+pagenumber);
    
    for(int countParagraph = 0; countParagraph<EBibleConstants.paragraphList.size(); countParagraph++)
    {	            	
    	if(EBibleConstants.paragraphList.get(countParagraph).equalsIgnoreCase(""+pagenumber))
    	{
    		String contentTextAtPosition = EBibleConstants.contentList.get(countParagraph);
    		if(contentTextAtPosition.contains("<"))
    		{
    			contentTextAtPosition = Html.fromHtml(contentTextAtPosition).toString();
    		}
    		//descriptionString += EBibleConstants.verseList.get(countParagraph)+") "+contentTextAtPosition;
    		//versePositionToHighlighten = countParagraph;
    		
    		//find if that verse has any highlighted text stored into db
    		Cursor cursor = rvFromDb.retrieveValues(EBibleConstants.verseList.get(countParagraph), 
    				selectedChapterPosition, selectedBookName);
    		if(cursor.getCount() > 0)
    		{
    			Log.i("","cursor value = "+cursor.getCount());
    			cursor.moveToFirst();
    			for(int curCount=0; curCount<cursor.getCount(); curCount++)
    			{
    				String desc = cursor.getString(1);
    				Log.i("cursor matched","values = "+desc+"\n"+
    						cursor.getString(2)+"\n"+cursor.getString(3)+"\n"+
    						cursor.getString(4)+"\n"+cursor.getString(5)+"\n"+
    						cursor.getString(6)+"\n"+cursor.getString(7)+"\n");
    				
    				/*if(!highlightedMatchedVerseArr.contains(cursor.getString(2)))
    				{*/
    					highlightedMatchedVerseArr.add(cursor.getString(2));
    				//}    				
    					highlightedMatchedColorCodeArr.add(URLDecoder.decode(cursor.getString(5)));    				
    					highlightedMatchedUnderlineArr.add(cursor.getString(7));
    				
    				highlightedMatchedDescriptionArr.add(cursor.getString(1));

    				cursor.moveToNext();
    			}
    		}
    		Log.i("db desc array",""+highlightedMatchedDescriptionArr+"\n"
    						+highlightedMatchedColorCodeArr+"\n"+highlightedMatchedUnderlineArr);
    		//close the cursor    			
    			if(cursor != null)
    			{
    				cursor.close();
    				cursor = null;
    			}    			
    		
    		//store into array list to highlight on highlighted page
    		EBibleConstants.verseListToHighlight.add(EBibleConstants.verseList.get(countParagraph));
    		EBibleConstants.paragraphListToHighlight.add(EBibleConstants.paragraphList.get(countParagraph));
    		EBibleConstants.contentListToHighlight.add(contentTextAtPosition);
    		    		
    	}	            	
    }
    Log.i("","description = \n"+descriptionString);
    Log.i("","list content to highlight "+EBibleConstants.verseListToHighlight
    		+"\npara= "+EBibleConstants.paragraphListToHighlight+"\ncontent = "+
    		EBibleConstants.contentListToHighlight); 
    //set the description texts
   // description.setText(descriptionString);
    descList.setAdapter(new VersesListAdapter());
    
	Log.i("","page no = "+EBibleConstants.pageNumber
			+"\nlast para value = "+EBibleConstants.lastParaValue);
	
	/*//find the values from the local database for the perticular verses
	String[] coloms = {"DESCRIPTION, VERSENO, CHAPTERNO, BOOKNAME, PARAGRAPHNO, " +
	"SECTIONID, CHARPOSITION, ISHIGHLIGHTED, ISUNDERLINE"};
	
	String whereCondition = "VERSENO="+verseno+" AND CHAPTERNO="+chapterno+" " +
			"AND BOOKNAME="+bookname+" AND ISHIGHLIGHTED=true";
	new LocalDatabaseAsync(BookDescriptionActivity.this, EBibleConstants.DESCRIPTION_ACTIVITY, 
							this, EBibleConstants.DATA_RETRIEVE, "TEXTHIGHLIGHT", coloms, 
							whereCondition, null, null, null);*/
	/*if(EBibleConstants.pageNumber < (EBibleConstants.lastParaValue))
	{
		next.setEnabled(true);
	}
	else
	{
		next.setEnabled(false);
	}
	if(EBibleConstants.pageNumber == (EBibleConstants.startParaNumber) )
	{
		previous.setEnabled(false);
	}
	else
	{
	    previous.setEnabled(true);
	}
	
	description.setOnLongClickListener(new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			Log.i("","selected chapter "+selectedChapterName);
			//start the highlight view dialog
			new HighlightDialog(BookDescriptionActivity.this, selectedBookName, 
					selectedChapterPosition, versePositionToHighlighten);
			return false;
		}
	});*/
}

@Override
protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
	/*if(rvFromDb != null)
	rvFromDb.closeDb();*/
}

@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	if(rvFromDb != null)
	rvFromDb.closeDb();
}

/**
 * Class that will extend BaseAdapter class to show custom list of books
 */
class BookListAdapter extends BaseAdapter 
{
/**Declare variables*/
	String[] arrayListOfItems;
	
	Dialog opendDialog;
/**
 * Constructor definition
 * @param array list
*/
	BookListAdapter(String[] arrayList, Dialog dialog)
	{
		arrayListOfItems = new String[arrayList.length];
		System.arraycopy(arrayList, 0, arrayListOfItems, 0, arrayList.length);
		
		opendDialog = dialog;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayListOfItems.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(BookDescriptionActivity.this).
				inflate(R.layout.book_list_data, null);
		
		//initialize the widgets of inflated view
		TextView bookName = (TextView)view.findViewById(R.id.book_list_name_txt);
		
		//set the name of the book for 
		//the all positions of no of books that
		//associates with bookName Array
		bookName.setText(arrayListOfItems[position]);
		
		//click event for the list item
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if((arrayListOfItems[position].equalsIgnoreCase("Genesis")) || 
						(arrayListOfItems[position].equalsIgnoreCase("John"))
						||(arrayListOfItems[position].equalsIgnoreCase("John1")) 
						||(arrayListOfItems[position].equalsIgnoreCase("John2"))
						||(arrayListOfItems[position].equalsIgnoreCase("John3")))
				{
					selectedBookPosition = position;
					selectedBookNameForHeading = arrayListOfItems[position];
					//close the current opened dialog if showing
					if(opendDialog != null && 
							opendDialog.isShowing())
					{
						opendDialog.dismiss();   
					}
					isChapterDisplaying = true;
					new EBibleDialog(BookDescriptionActivity.this, 
							BookDescriptionActivity.this, R.layout.chapter_list_grid, "chapter");
				}
				else
				{
					//nothing to do because we don't have 
					//related file in assets folder of the app
					Toast.makeText(BookDescriptionActivity.this, "No detail is available for this Book", Toast.LENGTH_SHORT).show();
				}
			
			}
		});
		
		return view;
	}
	
}

/**
 * Class that will extend BaseAdapter class to show custom list of chapters
 */
class ChapterListAdapter extends BaseAdapter 
{
/**Declare variables*/
	String[] arrayListOfItems;
	
	Dialog opendDialog;
/**
 * Constructor definition
 * @param array list
*/
	ChapterListAdapter(String[] arrayList, Dialog dialog)
	{
		opendDialog = dialog;
		
		arrayListOfItems = new String[arrayList.length];
		System.arraycopy(arrayList, 0, arrayListOfItems, 0, arrayList.length);
		
		//clear the last selected chapter name
		selectedChapterName = "";
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayListOfItems.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(BookDescriptionActivity.this).
				inflate(R.layout.chapter_list_grid_data, null);
		
		//initialize the widgets of inflated view
		TextView chapterName = (TextView)view.findViewById(R.id.chapter_list_txt);
		
		//set the name of the book for 
		//the all positions of no of books that
		//associates with bookName Array
		chapterName.setText(arrayListOfItems[position]);
		
		//click event for the list item
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				
				if(isChapterDisplaying)
				{
					//store the selected chapter name
					selectedChapterName = "Chapter"+arrayListOfItems[position];
					selectedChapterPosition = arrayListOfItems[position];
					//show the dialog of verses for the selected chapter
					//get the data from the assets of related chapter
					String fileName = selectedBookName+"/"+arrayListOfItems[position]+".json";
					//store globally
					EBibleConstants.lastOpenedFilePath = fileName;
					Log.i(EBibleConstants.APPTAG,"selected chapter "+fileName);
					//close the current opened dialog if showing
					if(opendDialog != null && 
							opendDialog.isShowing())
					{
						opendDialog.dismiss();   
					}
					new AssetsDataAsync(BookDescriptionActivity.this, EBibleConstants.CHAPTER_TABLE, fileName, BookDescriptionActivity.this).execute();

				}
				else
				{
					//close the current opened dialog if showing
					if(opendDialog != null && 
							opendDialog.isShowing())
					{
						opendDialog.dismiss();   
					}
					//set the heading text
					upperHeading.setText(bookNameHeading+" "+chapterNumberHeading);
					heading.setText(bookNameHeading);
				    //set the section title
				    chapterHeading.setText(sectionTitleHeading);
					
				    //store the startting index of the paragraph
				    EBibleConstants.startParaNumber = EBibleConstants.pageNumber = Integer.parseInt(EBibleConstants.paragraphList.get(0));
				    //store the paragraph last value
				    EBibleConstants.lastParaValue = Integer.parseInt(EBibleConstants.paragraphList.get(EBibleConstants.paragraphList.size()-1));
					Log.i("","last value of paragraph = "+EBibleConstants.lastParaValue+"\nstart index of paragraph = "
													+EBibleConstants.pageNumber);
				    
					//display the verses texts according to paging
					displayVerseText();
				}
			}   
		});  
		
		return view;
	}
	
}

/**
 * Functionality of chapter dialog box to show the list of chapters for the selected book.
*/
void chapterDialogDisplayedFunc(final Dialog dialog)
{
	//initialize the widgets of this view
			final AutoCompleteTextView searchBoxChapters = (AutoCompleteTextView)dialog.findViewById(R.id.chapter_list_search_box);
			final GridView chapterList = (GridView)dialog.findViewById(R.id.chapter_list);
			TextView bookName = (TextView)dialog.findViewById(R.id.chapter_list_bookname_txt);
			
	int numberOfChapters;
	//find the selected book
	if(selectedBookNameForHeading.equalsIgnoreCase("Genesis"))
	{
		numberOfChapters = 50;
		bookName.setText("Genesis");
		selectedBookName = "genesis";
	}
	else if(selectedBookNameForHeading.equalsIgnoreCase("John"))
	{
		numberOfChapters = 21;
		bookName.setText("John");
		selectedBookName = "john";
	}
	else if(selectedBookNameForHeading.equalsIgnoreCase("John1"))
	{
		numberOfChapters = 5;
		bookName.setText("John1");
		selectedBookName = "1john";
	}
	else if(selectedBookNameForHeading.equalsIgnoreCase("John2"))
	{
		numberOfChapters = 1;
		bookName.setText("John2");
		selectedBookName = "2john";
	}
	else
	{
		numberOfChapters = 1;
		bookName.setText("John3");
		selectedBookName = "3john";
	}
	//store the chapters counting into the array
	final String[] numberOfChaptersList = new String[numberOfChapters];
	for(int count = 0; count<numberOfChapters; count++)
	{
		int chapterNumber = count+1;
		numberOfChaptersList[count] = ""+chapterNumber;
		
		Log.i("","chapter list = "+numberOfChaptersList[count]);
	}
					
		//set the adapter class to show the list of Books
		chapterList.setAdapter(new ChapterListAdapter(numberOfChaptersList, dialog));
		
		//set the text that enters to search
		searchBoxChapters.setThreshold(1);
		//set the list of the books to show by search
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
			(this,android.R.layout.simple_dropdown_item_1line,numberOfChaptersList);
		//search functionality to get book name of the list
		//that will works with dynamically text entered by user
		searchBoxChapters.addTextChangedListener(new TextWatcher() {
			   
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub					
				searchChapterNameArr.clear();
				
				int searchTextlength=searchBoxChapters.getText().length();
				Log.i("","chapter list size = "+numberOfChaptersList.length);
				if(searchTextlength > 0)
				{
					for(int j=0;j<numberOfChaptersList.length;j++)
					{
						Log.i("","i = "+j);
						if(searchTextlength<=numberOfChaptersList[j].length())
						{
							if(searchBoxChapters.getText().toString().equalsIgnoreCase((String)numberOfChaptersList[j]
									.subSequence(0, searchTextlength)))
							{
								searchChapterNameArr.add(numberOfChaptersList[j]);
								Log.e(""," searched chapter= "+searchChapterNameArr);
							}
						}
					}
				}
				//if search text is entered then
				//show the search items list
				if(searchChapterNameArr.size() > 0)
				{
					String[] searchBookNameStringArr = searchChapterNameArr.toArray(new String[0]);
					Log.i("","searched chapter string array"+searchBookNameStringArr.length);
					chapterList.setAdapter(new ChapterListAdapter(searchBookNameStringArr, dialog));
				}
				else		//show the whole list of books
				{
					chapterList.setAdapter(new ChapterListAdapter(numberOfChaptersList, dialog));	
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
							
		});
}

/**
 * Functionality of verses dialog box to show the list of verses for the selected chapter.
*/
void verseDialogDisplayedFunc(final Dialog dialog)
{
	//initialize the widgets of this view
		final AutoCompleteTextView searchBox = (AutoCompleteTextView)dialog.findViewById(R.id.verse_list_search_box);
		final GridView verseList = (GridView)dialog.findViewById(R.id.verse_list);
		TextView bookNameText = (TextView)dialog.findViewById(R.id.verse_list_bookname_txt);
		TextView chapterNameText = (TextView)dialog.findViewById(R.id.verse_list_chaptername_heading_txt);
		
		//set the heading text with chapter name and book name
		bookNameText.setText(selectedBookNameForHeading);
		chapterNameText.setText(selectedChapterName);
		
		//convert array list into string array of the verse list
		final String[] verseListArr = EBibleConstants.verseList.toArray(new String[0]);
		//set the adapter class to show the list of verses
		verseList.setAdapter(new ChapterListAdapter(verseListArr, dialog));
		
		//set the text that enters to search
		searchBox.setThreshold(1);
		//set the list of the verse to show by search
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
			(this,android.R.layout.simple_dropdown_item_1line,verseListArr);
		//search functionality to get verse name of the list
		//that will works with dynamically text entered by user
		searchBox.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub					
				searchVerseNameArr.clear();
				
				int searchTextlength=searchBox.getText().length();
				
				if(searchTextlength > 0)
				{
					for(int i=0;i<verseListArr.length;i++)
					{
						if(searchTextlength<=verseListArr[i].length())
						{
							if(searchBox.getText().toString().equalsIgnoreCase((String)verseListArr[i]
									.subSequence(0, searchTextlength)))
							{
								searchVerseNameArr.add(verseListArr[i]);
								Log.e(""," searched friend data "+searchVerseNameArr);
							}
						}
					}
				}
				//if search text is entered then
				//show the search items list
				if(searchVerseNameArr.size() > 0)
				{
					String[] searchVerseNameStringArr = searchVerseNameArr.toArray(new String[0]);
					Log.i("","searched verse string array"+searchVerseNameStringArr.length);
					verseList.setAdapter(new ChapterListAdapter(searchVerseNameStringArr, dialog));
				}
				else		//show the whole list of verse
				{
					verseList.setAdapter(new ChapterListAdapter(verseListArr, dialog));	
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
							
		});
}

/**
 * Functionality of book dialog box to show list of 66 books.
*/
void bookDialogListDisplayedFunc(final Dialog dialog)
{	
	//initialize the widgets of this view
	final AutoCompleteTextView searchBox = (AutoCompleteTextView)dialog.findViewById(R.id.book_list_search_box);
	final ListView bookList = (ListView)dialog.findViewById(R.id.book_list);
			
	//set the adapter class to show the list of Books
	bookList.setAdapter(new BookListAdapter(bookNameArr, dialog));
	
	//set the text that enters to search
	searchBox.setThreshold(1);
	//set the list of the books to show by search
	ArrayAdapter<String> adapter = new ArrayAdapter<String>
		(this,android.R.layout.simple_dropdown_item_1line,bookNameArr);
	//search functionality to get book name of the list
	//that will works with dynamically text entered by user
	searchBox.addTextChangedListener(new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub					
			searchBookNameArr.clear();
			
			int searchTextlength=searchBox.getText().length();
			
			if(searchTextlength > 0)
			{
				for(int i=0;i<bookNameArr.length;i++)
				{
					if(searchTextlength<=bookNameArr[i].length())
					{
						if(searchBox.getText().toString().equalsIgnoreCase((String)bookNameArr[i]
								.subSequence(0, searchTextlength)))
						{
							searchBookNameArr.add(bookNameArr[i]);
							Log.e(""," searched friend data "+searchBookNameArr);
						}
					}
				}
			}
			//if search text is entered then
			//show the search items list
			if(searchBookNameArr.size() > 0)
			{
				String[] searchBookNameStringArr = searchBookNameArr.toArray(new String[0]);
				Log.i("","searched book string array"+searchBookNameStringArr.length);
				bookList.setAdapter(new BookListAdapter(searchBookNameStringArr, dialog));
			}
			else		//show the whole list of books
			{
				bookList.setAdapter(new BookListAdapter(bookNameArr, dialog));	
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
						
	});
}

/**
 * Class that extends the BaseAdapter class to show the list of fetched Verses
*/
class VersesListAdapter extends BaseAdapter
{

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return EBibleConstants.contentListToHighlight.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(BookDescriptionActivity.this)
						.inflate(R.layout.book_desc_data, null);
		//initiate widgets 
		TextView description = (TextView)view.findViewById(R.id.book_desc_txt);
		TextView verseNo = (TextView)view.findViewById(R.id.book_desc_verseno_txt);
		//set the verse string to show
		verseNo.setText(EBibleConstants.verseListToHighlight.get(position));
		description.setText(EBibleConstants.contentListToHighlight.get(position), BufferType.SPANNABLE);
		  
		/*Log.i("vesre contains in adapter",""+highlightedMatchedVerseArr.contains(EBibleConstants.verseListToHighlight.get(position)));
		Log.i("db saved verse at position",""+highlightedMatchedVerseArr.get(position));
		Log.i("saved verse at position",""+EBibleConstants.verseListToHighlight.get(position));
		Log.i("verse position matched",""+(highlightedMatchedVerseArr.get(position).equalsIgnoreCase((EBibleConstants.verseListToHighlight.get(position)))));*/
		//highlight functionality if found
		Spannable textToSpan = (Spannable) description.getText();
		if((highlightedMatchedDescriptionArr.size() > 0))
		{  
		for(int count=0; count<highlightedMatchedDescriptionArr.size(); count++)
		{  			
			if(highlightedMatchedDescriptionArr.get(count) != null  /*&&
					(highlightedMatchedVerseArr.get(position).equalsIgnoreCase(
							(EBibleConstants.verseListToHighlight.get(position))))*/)
			{
				/*Log.i("verse matched desc in loop",""+highlightedMatchedDescriptionArr.get(count).equalsIgnoreCase
						(highlightedMatchedVerseArr.get(position)));
			if(highlightedMatchedDescriptionArr.get(count).equalsIgnoreCase
					(highlightedMatchedVerseArr.get(position)))
			{*/
			int index = EBibleConstants.contentListToHighlight.get(position)
					.indexOf(highlightedMatchedDescriptionArr.get(count), 0);
							//.indexOf(highlightedMatchedDescriptionArr.get(count));
			Log.i("index",""+index);
			if(index > -1 && (highlightedMatchedVerseArr.get(count)
					.equalsIgnoreCase(EBibleConstants.verseListToHighlight.get(position))))
			{
			textToSpan.setSpan(new BackgroundColorSpan(Integer.parseInt(highlightedMatchedColorCodeArr.get(count))),
									index, (index+highlightedMatchedDescriptionArr.get(count).length()), 
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			//check for underline
			if(highlightedMatchedUnderlineArr.get(count).equalsIgnoreCase("1"))
			{
				textToSpan.setSpan(new UnderlineSpan(), index, (index+highlightedMatchedDescriptionArr.get(count).length()), 
										Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			}
			}
			//}
		}	
		description.setText(textToSpan);
		}
		
		
		//set the buttons state as enable or disable respect to data fetched from file
		if(EBibleConstants.pageNumber < (EBibleConstants.lastParaValue))
		{
			next.setEnabled(true);
		}
		else
		{
			next.setEnabled(false);
		}
		if(EBibleConstants.pageNumber == (EBibleConstants.startParaNumber) )
		{
			previous.setEnabled(false);
		}
		else
		{
		    previous.setEnabled(true);
		}
		
		description.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Log.i("","selected chapter "+selectedChapterName);
				versePositionToHighlighten = position;
				verseHighlightedPositionToStore = EBibleConstants.verseListToHighlight.get(position);
				//start the highlight view dialog
				new HighlightDialog(BookDescriptionActivity.this, selectedBookName, 
						selectedChapterPosition, position);
				return false;
			}
		});
		
		return view;
	}
	
}

/**
 * Functionality to find the value from local db that have to be highlighted
*/
/*@Override
public void onLocalDatabseResponse(int activityId, boolean isOk, Cursor cursor) {
	if(isOk)
	{
		if(cursor.getCount() > 0)
		{
			
		}
		descList.setAdapter(new VersesListAdapter());
	}
	else
	{
		onLocalDatabaseResponseError(EBibleConstants.DESCRIPTION_ACTIVITY, "Local db value retrieve error");
	}
}

@Override
public void onLocalDatabaseResponseError(int activityId, String message) {
	// TODO Auto-generated method stub
	
}*/
}
