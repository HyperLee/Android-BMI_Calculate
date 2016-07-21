package com.demo.android.bmi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

@SuppressLint("NewApi")
public class ReportActivity extends Activity {
	private static final String TAG = ReportActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		initViews();
        showResults();
        setListensers();
	}
	
	private Button button_back;
    private TextView show_result;
    private TextView show_suggest;

    private void initViews() {
    	if(Debug.On) Log.d(TAG, "init Views, Peter ReportActivity");
        button_back = (Button) findViewById(R.id.report_back);
        show_result = (TextView) findViewById(R.id.result);
        show_suggest = (TextView) findViewById(R.id.suggest);
    }
    
    private double BMI;
    private void showResults() {
        DecimalFormat nf = new DecimalFormat("0.00");

    try{
        	Bundle bunde = this.getIntent().getExtras();
        	double height = Double.parseDouble(bunde.getString("KEY_HEIGHT")) / 100;
        	double weight = Double.parseDouble(bunde.getString("KEY_WEIGHT"));
        	BMI = weight / (height * height);
        	show_result.setText(getString(R.string.bmi_result) + nf.format(BMI));

        	//Give health advice
        	if (BMI > 25) 
        	{
        		showNotification(BMI);
        		show_suggest.setText(R.string.advice_heavy);
        		show_suggest.setTextColor(Color.RED);//set color red//
                Toast.makeText(ReportActivity.this,"死胖子",Toast.LENGTH_SHORT).show();
        	} 
        	else if (BMI < 20) 
        	{
        		show_suggest.setText(R.string.advice_light);
        		show_suggest.setTextColor(Color.GREEN);//set color green//
                Toast.makeText(ReportActivity.this,"瘦皮侯",Toast.LENGTH_SHORT).show();
        	} 
        	else 
        	{
        		show_suggest.setText(R.string.advice_average);
        		show_suggest.setTextColor(Color.BLUE);//set color blue//
                Toast.makeText(ReportActivity.this,"看起來還可以喔!",Toast.LENGTH_SHORT).show();
        	}
        }
        
    catch (Exception err){
        	show_suggest.setText(R.string.input_error);
        	if (Debug.On) Log.e(TAG, "Peter, error: " + err.toString());
        	Toast.makeText(ReportActivity.this, R.string.input_error, Toast.LENGTH_SHORT).show();
        }
    }

    protected void showNotification(double BMI)
    {
       NotificationManager barManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//     Notification barMsg = new Notification(R.drawable.ic_launcher, "你屎定了 胖子！",System.currentTimeMillis());
       PendingIntent contentIntent = PendingIntent.getActivity(this,0,new Intent(this, MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
       Notification.Builder barMsg = new Notification.Builder(this)
       .setTicker("你屎定了 胖子！")
       .setContentTitle("你太肥拉~~~!")
       .setContentText("要被賣掉囉哈哈")
//     .setSmallIcon(android.R.drawable.stat_sys_warning)
       .setSmallIcon(android.R.drawable.btn_star)
       .setContentIntent(contentIntent);
       barManager.notify(0, barMsg.build());
    }

    //Listen for button clicks
    private void setListensers() {
    	if (Debug.On) Log.d(TAG, "set Listensers, Peter ReportActivity");
        button_back.setOnClickListener(backMain);
    }

    private Button.OnClickListener backMain = new Button.OnClickListener() {
        public void onClick(View v) {
        	DecimalFormat nf = new DecimalFormat("0.00");
        	Bundle bundle = new Bundle(); 
            bundle.putString("BMI", nf.format(BMI));  
            Intent intent = new Intent();  
            intent.putExtras(bundle); 
        	setResult(RESULT_OK, intent);
            // Close this Activity
            ReportActivity.this.finish();
        }
    };
}
