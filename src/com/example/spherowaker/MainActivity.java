package com.example.spherowaker;

import java.util.Calendar;
import java.util.Date;

import orbotix.robot.app.StartupActivity;
import orbotix.robot.base.RGBLEDOutputCommand;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RobotProvider;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class MainActivity extends Activity {

	final Calendar c = Calendar.getInstance();
    int maxYear = c.get(Calendar.YEAR) - 20; // this year ( 2011 ) - 20 = 1991
    int maxMonth = c.get(Calendar.MONTH);
    int maxDay = c.get(Calendar.DAY_OF_MONTH);

    int minYear = 1960;
    int minMonth = 0; // january
    int minDay = 25;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    protected void onStart() {
    	super.onStart();
//    	Intent i = new Intent(this, StartupActivity.class);  
//    	startActivityForResult(i, 0);  

    	//Date Stuff 
    	Date now = new Date();
    	DatePicker dp = (DatePicker) findViewById(R.id.datePicker1);
    	

    	Log.d("MARK", ""+now.getYear() + now.getMonth() + now.getDate());
    	dp.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				if (year < minYear)
	                view.updateDate(minYear, minMonth, minDay);

	                if (monthOfYear < minMonth && year == minYear)
	                view.updateDate(minYear, minMonth, minDay);

	                if (dayOfMonth < minDay && year == minYear && monthOfYear == minMonth)
	                view.updateDate(minYear, minMonth, minDay);


	                if (year > maxYear)
	                view.updateDate(maxYear, maxMonth, maxDay);

	                if (monthOfYear > maxMonth && year == maxYear)
	                view.updateDate(maxYear, maxMonth, maxDay);

	                if (dayOfMonth > maxDay && year == maxYear && monthOfYear == maxMonth)
	                view.updateDate(maxYear, maxMonth, maxDay);
			}
    		
    	});
    	
    	
    	//time stuff
    	TimePicker tp = (TimePicker)findViewById(R.id.timePicker1);
    	tp.setIs24HourView(true);
    	tp.setCurrentHour(10);
    	tp.setCurrentMinute(10);
    	
    	final Context fContext = this;
    	try {
        	Log.d("MARK", "sleep");
    		Thread.sleep(10*1000);    		
        	Log.d("MARK", "calling wakey");
        	WakefulIntentService.sendWakefulWork(fContext, wakey.class);
    	} catch( Exception e) {
    		//nothing yet
    	}
    	
    }
    
    private final static int STARTUP_ACTIVITY = 0;
    Robot mRobot;
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == STARTUP_ACTIVITY && resultCode == RESULT_OK){
            //Get the connected Robot
            final String robot_id = data.getStringExtra(StartupActivity.EXTRA_ROBOT_ID);  // 1
            if(robot_id != null && !robot_id.equals("")){
                mRobot = RobotProvider.getDefaultProvider().findRobot(robot_id);          // 2
            }
            //Start blinking
            blink(false);                                                                 // 3
        }
     }

    int mBrightness = 0;
    
    private void blink(final boolean lit){

        if(mRobot != null){

            //If not lit, send command to show blue light, or else, send command to show no light
//            if(lit){
//                RGBLEDOutputCommand.sendCommand(mRobot, 0, 0, 0);        // 1
//            }else{
//                RGBLEDOutputCommand.sendCommand(mRobot, 0, 0, 25);      // 2
//            }
        	
        	RGBLEDOutputCommand.sendCommand(mRobot, 0, 0, mBrightness++); 

        	if(mBrightness == 255)
        		mBrightness = 0;
        	
            //Send delayed message on a handler to run blink again
            final Handler handler = new Handler();                       // 3
            handler.postDelayed(new Runnable() {
                public void run() {
                    blink(!lit);
                }
            }, 100);
        }
    }
    
    public class wakey extends WakefulIntentService {

		public wakey(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void doWakefulWork(Intent arg0) {
			// TODO Auto-generated method stub
			Log.d("MARK", "I woke up!");
		}
    	
    }
}
