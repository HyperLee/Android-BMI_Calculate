package com.demo.android.bmi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int ACTIVITY_REPORT = 0001;
//	public static final String PREF = "BMI_PREF";
//	public static final String PREF_HEIGHT = "BMI_HEIGHT";
	private  DB mDbHelper;
    private Cursor mCursor;

	String fileName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate, Peter MainActivity");

		Resources res = getResources();
		Configuration conf = res.getConfiguration();
		conf.locale = Locale.TRADITIONAL_CHINESE;
		DisplayMetrics dm = res.getDisplayMetrics();
		res.updateConfiguration(conf, dm);

		setContentView(R.layout.activity_main);
		initViews();
//		restorePrefs();
	    setListensers();
	}

	//Restore preferences
	private void restorePrefs() {
		// TODO Auto-generated method stub
//		SharedPreferences setting = getSharedPreferences(PREF, 0);
//		String pref_height = setting.getString(PREF_HEIGHT, "");
		String pref_height = Pref.getHeight(this);
		if(! "".equals(pref_height)){
			num_height.setText(pref_height);
			num_weight.requestFocus();
		}
	}

	private Button button_calc;
	private Button button_takepic;
	private EditText num_height;
	private EditText num_weight;
	private TextView show_result;
	private TextView show_suggest;

	File pictureFile;
	
	private void initViews() {
	  Log.d(TAG, "init Views, Peter MainActivity");
	  button_calc = (Button) findViewById(R.id.submit);
	  button_takepic = (Button) findViewById(R.id.button);
	  num_height = (EditText) findViewById(R.id.height);
	  num_weight = (EditText) findViewById(R.id.weight);
	  show_result = (TextView) findViewById(R.id.result);
	  show_suggest = (TextView) findViewById(R.id.suggest);
	}

	// Listen for button clicks
	private void setListensers() {
		Log.d(TAG, "set Listensers, Peter MainActivity");
	    button_calc.setOnClickListener(calcBMI);
		button_takepic.setOnClickListener(takepicture);
    }
	
	private Button.OnClickListener calcBMI = new Button.OnClickListener() { 
			
//		public void onClick(View v) {
//			//Switch to report page
//	        Intent intent = new Intent();
//	        intent.setClass(MainActivity.this, ReportActivity.class);
//	        Bundle bundle = new Bundle();
//	        bundle.putString("KEY_HEIGHT", num_height.getText().toString());
//	        bundle.putString("KEY_WEIGHT", num_weight.getText().toString());
//	        intent.putExtras(bundle);
////	        startActivity(intent);
//	        startActivityForResult(intent, ACTIVITY_REPORT);
//            if(num_height.getText().toString().equals(""))
//            {
//            	num_height.setText("0");
//            	Log.i("0", "0");
//            }
//
//            if(num_weight.getText().toString().equals(""))
//            {
//            	num_weight.setText("0");
//            	Log.i("0", "0");
//            }
//		}

		public void onClick(View v) {
			new BmiCalcTask().execute();
		}
    };

	private class BmiCalcTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
		Double BMI;
		Double height;
		Double weight;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Dialog.setMessage("calc...");
			Dialog.show();


			if(num_height.getText().toString().equals(""))
			{
				num_height.setText("1");
			}

			if(num_weight.getText().toString().equals(""))
			{
				num_weight.setText("1");
			}

			height = Double.parseDouble(num_height.getText().toString()) / 100;
			weight = Double.parseDouble(num_weight.getText().toString());
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			BMI = weight / (height * height);
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			// TODO Auto-generated method stub
			Dialog.dismiss();
			DecimalFormat nf = new DecimalFormat("0.00");
			show_result.setText(getText(R.string.bmi_result) + nf.format(BMI));

			// record calcBMI result to db
			mDbHelper = new DB(MainActivity.this);
			try {
				try {
					mDbHelper.open();
				} catch (java.sql.SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			mDbHelper.create(nf.format(BMI));
			mDbHelper.close();

			// Give health advice
			if (BMI > 25) {
				show_suggest.setText(R.string.advice_heavy);
			} else if (BMI < 20) {
				show_suggest.setText(R.string.advice_light);
			} else {
				show_suggest.setText(R.string.advice_average);
			}
		}
	}

	private Button.OnClickListener takepicture = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 照片檔案名稱
			pictureFile = configFileName("P", ".jpg");
			Uri uri = Uri.fromFile(pictureFile);
			// 設定檔案名稱
			intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			 //啟動相機元件
//			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(uri));
			startActivityForResult(intentCamera, 100);
//			startActivity(intentCamera);
		}
	};

//    protected static final int MENU_SETTINGS = Menu.FIRST;
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
//		menu.add(0, MENU_SETTINGS, 100, R.string.action_settings);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case R.id.action_about:
//            openOptionsDialog();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(MainActivity.this, Pref.class);
			startActivity(intent);
            break;
	   	case R.id.action_close:
	            //finish();
			Intent intent1_history = new Intent(Intent.ACTION_VIEW); //根據intent提供的內容不同,自動開啟相對應的程式來檢視內容//
			intent1_history.setClass(MainActivity.this, HistoryActivity.class); //intent from main activity to history activity//
			startActivity(intent1_history);
	         break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void openOptionsDialog() {
		AlertDialog.Builder dialog = new  AlertDialog.Builder(MainActivity.this);
	    dialog.setTitle(R.string.about_title);
	    dialog.setMessage(R.string.about_msg);
	    dialog.setPositiveButton(android.R.string.ok,
	    	new DialogInterface.OnClickListener(){
	    	    public void onClick(DialogInterface dialoginterface, int i){}
	        });
	    dialog.setNegativeButton(R.string.label_homepage,
		    	new DialogInterface.OnClickListener(){
		    	    public void onClick(DialogInterface dialoginterface, int i){
		    	    	// open browser
//		                Uri uri = Uri.parse("http://android.gasolin.idv.tw/");
		    	    	// open map
//		    	    	Uri uri = Uri.parse("geo:25.047192, 121.516981?z=17");
		    	    	// phone call
//		    	        Uri uri = Uri.parse("tel:12345678"); 
		    	    	Uri uri = Uri.parse(getString(R.string.homepage_uri));
		                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		                startActivity(intent);
		    	    }
		        });
	    dialog.show();
	};

//	private void openOptionsDialog() {
//	    Toast popup = Toast.makeText(MainActivity.this, R.string.app_name, Toast.LENGTH_SHORT);
//	    popup.show();
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent intent) 
	{
	    super.onActivityResult(requestCode, resultCode, intent);
	    if (resultCode == RESULT_OK) {
	        if (requestCode == ACTIVITY_REPORT) 
	        {
	        	Bundle bundle = intent.getExtras();
	        	String bmi = bundle.getString("BMI");     	
	        	show_suggest.setText(getString(R.string.advice_history) + bmi);
//	        	num_height.setText(R.string.input_empty);  //輸出結果返回上一頁 height數值清空//
				num_weight.setText(R.string.input_empty);  //輸出結果返回上一頁 weight數值清空//
				num_weight.requestFocus();  //輸出結果返回上一頁 輸入指標放在weight欄位//
//	        	num_height.requestFocus();  //輸出結果返回上一頁 輸入指標放在height欄位//
	        }
			else if(requestCode == 100)
			{
//			Uri uri = Uri.fromFile(pictureFile);
//				Toast popup = Toast.makeText(MainActivity.this, "sendbroadcast = " + requestCode, Toast.LENGTH_SHORT);
//				popup.show();
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(Uri.fromFile(pictureFile)));
			}
	    }
	}

	public void onRestart() {
        super.onRestart();
        Log.v(TAG, "onReStart, Peter MainActivity");
    }

    public void onStart() {
		super.onStart();
		Log.v(TAG, "onStart, Peter MainActivity");
    }

    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume, Peter MainActivity");
		restorePrefs();
    }

    public void onPause() {
		super.onPause();
        Log.v(TAG,"onPause, Peter MainActivity");
        //Save user preferences . use Editor object to make changes. //
//        SharedPreferences setting = getSharedPreferences(PREF, 0);
//        Editor editor = setting.edit();
//        editor.putString(PREF_HEIGHT, num_height.getText().toString());
//        editor.commit();
        Pref.setHeight(this, num_height.getText().toString());
    }

    public void onStop() {
		super.onStop();
        Log.v(TAG, "onStop, Peter MainActivity");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy, Peter MainActivity");
    }

	private File configFileName(String prefix, String extension) {
		// 如果記事資料已經有檔案名稱
		fileName = FileUtil.getUniqueFileName();
		return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR), prefix + fileName + extension);
	}
}
