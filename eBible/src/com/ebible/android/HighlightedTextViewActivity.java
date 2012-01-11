package com.ebible.android;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import com.ebible.android.asyncthread.LocalDatabaseAsync;
import com.ebible.android.constants.EBibleConstants;
import com.ebible.android.interfaces.OnLocalDatabaseProcess;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.TextView.BufferType;

/**
 * This activity shows the highlighted texts into the text box 
 * and save into the local db to show text as highlighted.
*/
public class HighlightedTextViewActivity extends Activity implements OnClickListener,
				OnLocalDatabaseProcess{
/**
 * Declare variables
*/
    //private MyAdapter myAdapter;

	
	//ListView highlightedVerseListView;
	Button back, done;
	EditText textBox;
	TextView title;
	
	String displayedEditBoxText = "";
	int displayedEditTextPosition = -1;
	
	ArrayList<String> highlightedParagraphList, 
		highlightedContentList, highlightedVerseList;
	String[] textToHighlightArr, verseToHighlightArr;
	HashMap<Integer, String> changedTextArr;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    //hide the title bar
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //set the main view to display
	    setContentView(R.layout.highlighted_view);
	    //setContentView(R.layout.textviewexample);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//initiate widgets
		initialization();
		
		//set the text spannable
		textBox.setText(EBibleConstants.contentListToHighlight.get(BookDescriptionActivity.
							versePositionToHighlighten), BufferType.SPANNABLE);
		
		Spannable textToSpan = textBox.getText();
		
		if(EBibleConstants.isUnderline)
		{
			textToSpan.setSpan(new UnderlineSpan(), 0, 
								textToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		textToSpan.setSpan(new BackgroundColorSpan(EBibleConstants.selectedColorToHighlight), 
								0, textToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		
	}

/**
 * Functionality to initialize the widgets and variables and perform different events
*/
	void initialization()
	{
		//set the constant value true as highlighted dialog is shown
		EBibleConstants.isHighlightedDialogPassed = true;
		
		back = (Button)findViewById(R.id.highlighted_view_back_btn);
		done = (Button)findViewById(R.id.highlighted_view_ok_btn);
		
		textBox = (EditText)findViewById(R.id.highlighted_view_editbox);
		
		title = (TextView)findViewById(R.id.highlighted_view_heading_txt);
		//highlightedVerseListView = (ListView)findViewById(R.id.highlighted_view_list);
		
		//enable list to get on focus
		//highlightedVerseListView.setItemsCanFocus(true);
		
		//initiate array list
		highlightedContentList = new ArrayList<String>();
		highlightedParagraphList = new ArrayList<String>();
		highlightedVerseList = new ArrayList<String>();
		textToHighlightArr = new String[EBibleConstants.contentListToHighlight.size()];
		verseToHighlightArr= new String[EBibleConstants.contentListToHighlight.size()];
		changedTextArr = new HashMap<Integer, String>();
		
		//set the event listener for the widgets
		back.setOnClickListener(this);
		done.setOnClickListener(this);
				
		//show the list of selected verses that has to be highlighted
		//highlightedVerseListView.setAdapter(new VerseListAdapter());
		
		/*highlightedVerseListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.i("","scroll view"+scrollState);
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Log.i("","on scroll - \n first visible"+firstVisibleItem+
						"\ntotal item = "+totalItemCount+"\nvisible item = "+visibleItemCount);
				if(textBox != null && !displayedEditBoxText.equalsIgnoreCase(""))
				{
				Log.i("","value of text box = "+textBox.getText().toString());
				
				textBox.setText(displayedEditBoxText);
				//changedTextArr.put(displayedEditTextPosition, displayedEditBoxText);
				//Log.i("","id in scroll = "+view.getId());
				//Log.i("","string of changed = "+displayedEditTextPosition+" "+displayedEditBoxText);
				}
			}
		});*/
		
		/*highlightedVerseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i("","position of selected item "+highlightedVerseListView.getSelectedView());
				Log.i("","position of = "+position+" id= "+id);
			}
		});*/
		/*myAdapter = new MyAdapter();
		
		highlightedVerseListView.setAdapter(myAdapter);*/

	}
	
/**
 * Class to show the verses into the text boxes
*/
	/*class VerseListAdapter extends BaseAdapter
	{
*//**Declare variables*//*
		
*//**Default Constructor definition*//*
		public VerseListAdapter() {
					
			notifyDataSetChanged();
		}
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
		final View view = LayoutInflater.from(HighlightedTextViewActivity.this)
					.inflate(R.layout.highlighted_list_data, null);
		//initialize widgets
		textBox = (EditText)view.findViewById(R.id.highlighted_view_box);
		TextView text = (TextView)view.findViewById(R.id.highlighted_view_txt);
		
		//set the values to display on widgets
		text.setText(EBibleConstants.verseListToHighlight.get(position));
		//textBox.setText(EBibleConstants.contentListToHighlight.get(position));
		
		//set the background color of the text
		textBox.setText(EBibleConstants.contentListToHighlight.get(position), BufferType.SPANNABLE);
		
		Spannable textToSpan = textBox.getText();
		
		if(EBibleConstants.isUnderline)
		{
			textToSpan.setSpan(new UnderlineSpan(), 0, 
								textToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		textToSpan.setSpan(new BackgroundColorSpan(EBibleConstants.selectedColorToHighlight), 
								0, textToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		if(textBox.isFocused())
		{
			Log.i("focused","position of focused = "+position);
		}
		textBox.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				for(int countPos=0; countPos<EBibleConstants.contentListToHighlight.size(); countPos++)
				{
				switch(countPos)		
				{
				case 0:
				textBox.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if(!hasFocus)
						{
						if(!changedTextArr.containsKey(position))
							
						Log.i("focused","position of focused = "+position);
						
						int searchTextlength=textBox.getText().length();
						//textToHighlightArr[position] = "";
						if(searchTextlength > 0)
						{
							//textToHighlightArr[position] = textBox.getText().toString();
							
							displayedEditBoxText = textBox.getText().toString();
							displayedEditTextPosition = position;
							
							changedTextArr.put(position, displayedEditBoxText);
							Log.i("","string of changed = "+displayedEditTextPosition+" "+changedTextArr);
							Log.i("focused","value = "+textBox.getText().toString());
						}
						//}
				}
					
				//}
				//Log.i("","position = "+countPos+"changed texts = "+textToHighlightArr[countPos]);
				});
				//}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("click","position of clicked = "+position);
				
			}
		});
		return view;
	}
		
	}*/

/**
 * Click events for the widgets
 */
@Override
public void onClick(View v) {
	switch(v.getId())
	{
	case R.id.highlighted_view_back_btn:		//back button
		//close this activity
		finish();
		break;
	case R.id.highlighted_view_ok_btn:			//done button
		EBibleConstants.isHighLightedDone = true;
		//store the highlighted values into variables
		highlightedTextStoreFunction();		
		break;
	}
	
}
/**
 * Functionality to store highlighted texts in to arrays
*/
void highlightedTextStoreFunction()
{
	displayedEditBoxText = textBox.getText().toString().trim();	
	this.runOnUiThread(new Runnable() {
		
		@Override
		public void run() {
			
			String displayVerseTexts = EBibleConstants.contentListToHighlight.
					get(BookDescriptionActivity.versePositionToHighlighten);
					int index = displayVerseTexts.indexOf(displayedEditBoxText);
					Log.i("stored data index",""+index);
			
			ContentValues valuess = new ContentValues();
		    valuess.put("DESCRIPTION", displayedEditBoxText);
		    valuess.put("VERSENO", BookDescriptionActivity.verseHighlightedPositionToStore);
		    valuess.put("CHAPTERNO", BookDescriptionActivity.selectedChapterPosition);
		    valuess.put("BOOKNAME", BookDescriptionActivity.selectedBookName);
		    valuess.put("COLORCODE", URLEncoder.encode(""+EBibleConstants.selectedColorToHighlight));
		    valuess.put("ISHIGHLIGHTED", true);
		    valuess.put("ISUNDERLINE", EBibleConstants.isUnderline);
			new LocalDatabaseAsync(HighlightedTextViewActivity.this, EBibleConstants.HIGHLIGHTEDTEXTVIEW_ACTIVITY,
					HighlightedTextViewActivity.this, EBibleConstants.DATA_INSERT, "TEXTHIGHLIGHT", null, null, valuess, null, null).execute();
		
			
			/*if(displayedEditBoxText.contains(" "))
			{
				String displayVerseTexts = EBibleConstants.contentListToHighlight.
											get(BookDescriptionActivity.versePositionToHighlighten);
				int index = displayVerseTexts.indexOf(displayedEditBoxText);
				Log.i("stored data index",""+index);
				String[] valueStoreToHighlightArr = displayedEditBoxText.split(" ");
				
				valueStoreToHighlightArr = displayedEditBoxText.split(" ");
				if((valueStoreToHighlightArr != null) && 
						(valueStoreToHighlightArr.length>0))
				{		
					//new StoreValueIntoDb(HighlightedTextViewActivity.this, valueStoreToHighlightArr);
					new LocalDatabaseAsync(HighlightedTextViewActivity.this, EBibleConstants.HIGHLIGHTEDTEXTVIEW_ACTIVITY,
							HighlightedTextViewActivity.this, EBibleConstants.DATA_INSERT, "TEXTHIGHLIGHT", null, null, null, null, valueStoreToHighlightArr).execute();
				
					}
			}  
			else
			{
				String[] valueStoreToHighlightArr = new String[1];
				valueStoreToHighlightArr[0] = displayedEditBoxText;
				
				valueStoreToHighlightArr = displayedEditBoxText.split(" ");
				if((valueStoreToHighlightArr != null) && 
						(valueStoreToHighlightArr.length>0))
				{		
					//new StoreValueIntoDb(HighlightedTextViewActivity.this, valueStoreToHighlightArr);
					new LocalDatabaseAsync(HighlightedTextViewActivity.this, EBibleConstants.HIGHLIGHTEDTEXTVIEW_ACTIVITY,
							HighlightedTextViewActivity.this, EBibleConstants.DATA_INSERT, "TEXTHIGHLIGHT", null, null, null, null, valueStoreToHighlightArr).execute();
				}
			}*/
			
			
		}
	});
	
	
}

/**
 * Retrieve the response from local database async thread after processing
*/
@Override
public void onLocalDatabseResponse(int activityId, boolean isOk, Cursor cursor) {
	if(isOk)
	{
		Log.i(EBibleConstants.APPTAG,"data inserted successfully");
	}
	else
	{
		onLocalDatabaseResponseError(EBibleConstants.HIGHLIGHTEDTEXTVIEW_ACTIVITY,"insertion error");
	}
	//close this activity
	finish();
}

@Override
public void onLocalDatabaseResponseError(int activityId, String message) {
	Log.e(EBibleConstants.APPTAG, "Local db insert response error: "+message);
	
}

	/*public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public ArrayList myItems = new ArrayList();

        public MyAdapter() {

            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (int i = 0; i < 20; i++) {

                ListItem listItem = new ListItem();

                listItem.caption = "Caption" + i;

                myItems.add(listItem);

            }

            notifyDataSetChanged();

        }

        public int getCount() {

            return myItems.size();

        }

        public Object getItem(int position) {

            return position;

        }

        public long getItemId(int position) {

            return position;

        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.highlighted_list_data, null);

                holder.caption = (EditText) convertView.findViewById(R.id.highlighted_view_box);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();

            }

            //Fill EditText with the value you have in data source

           // holder.caption.setText(myItems.get(position).caption);

            holder.caption.setId(position);

            //we need to update adapter once we finish with editing

            holder.caption.setOnFocusChangeListener(new OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus){

                        final int position = v.getId();

                        final EditText Caption = (EditText) v;

                        myItems.get(position).caption= Caption.getText().toString();

                    }

                }

            });

            return convertView;

        }

    }

  public class ViewHolder {

        public EditText caption;

    }

  public class ListItem {

      public String caption;

    }*/

}
