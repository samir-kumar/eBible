package com.ebible.android;

import java.util.ArrayList;

import com.ebible.android.constants.EBibleConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/**
 * This class will show the banner of the eBible Android app.
 * This is the first screen that opens for 3 seconds at 
 * the startup and show the First main screen of the app.
*/ 
public class SplashActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //hide the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //show the full screen view for this activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //set the main view of this activity
        setContentView(R.layout.splash);
        
        //initialize the constants variables
        initializationOfConstatnts();
        
        //get the screen dimensions and 
        //save temporary in the constant class
        Display display = getWindowManager().getDefaultDisplay();
        //store height of the device
        EBibleConstants.DEVICE_HEIGHT = display.getHeight();
        //store width of the device
        EBibleConstants.DEVICE_WIDTH = display.getWidth();
        
        //start the timer for 3 seconds to disappear of this screen
        CountDownTimer();
    }
  //-----------------functionality to delay time and start with login view
    
  	 void CountDownTimer()
  	{
  		new CountDownTimer(3000, 3000) {
  			
  			@Override
  			public void onTick(long millisUntilFinished) {
  				// TODO Auto-generated method stub
  		
  			}
  			
  			@Override
  			public void onFinish() {
  			//start the Book Description page
  			//and close this activity
  				startActivity(new Intent(SplashActivity.this, BookDescriptionActivity.class));	 
  				finish();
  			}
  		}.start();
  	}
  	 
 /**
  * Functionality to initialize the constants variables 
  * like array list to use in the application for different activities.
 */
  	void initializationOfConstatnts()
  	 {
  		
 		//initialize the array lists
 		EBibleConstants.paragraphList = new ArrayList<String>();
 		EBibleConstants.contentList = new ArrayList<String>();
 		EBibleConstants.verseList = new ArrayList<String>();
 		
 		//initiate the highlighted array lists
 		EBibleConstants.verseListToHighlight = new ArrayList<String>();
 		EBibleConstants.paragraphListToHighlight = new ArrayList<String>();
 		EBibleConstants.contentListToHighlight = new ArrayList<String>();
  	 }
}