package com.allen.hq.keygurad;


import com.allen.hq.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SettingActivity extends Activity {
	Spinner spinner =null;
	int curSelect = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.setting_activity);
		spinner = (Spinner) this.findViewById(R.id.id_selectSpinner);
		 

		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.keyguard_type, android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  

		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				//Log.d("SettingActivity","onItemSelected:arg1:"+arg1+"arg2:"+arg2+"arg3:"+arg3);
				curSelect = arg2;
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);
				 Editor pEdit = prefs.edit();
				 pEdit.putInt("lockType", curSelect);
				 pEdit.commit();

			}
			

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		Button button  = (Button)this.findViewById(R.id.id_startButton);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				launchService();
			}
		});
	}
	
	Handler mHandler=new Handler();
	private void launchService(){
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, com.allen.hq.keygurad.HQLockServer.class);
				SettingActivity.this.startService(intent);
			}
	
		});

	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
}
