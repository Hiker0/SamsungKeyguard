/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allen.hq.keygurad;

import static android.os.BatteryManager.BATTERY_HEALTH_UNKNOWN;
import static android.os.BatteryManager.BATTERY_STATUS_UNKNOWN;
import static android.os.BatteryManager.EXTRA_HEALTH;
import static android.os.BatteryManager.EXTRA_LEVEL;
import static android.os.BatteryManager.EXTRA_PLUGGED;
import static android.os.BatteryManager.EXTRA_STATUS;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.hq.R;


public class KeyguardStatusView extends GridLayout {

	private static final String TAG = "KeyguardStatusView";

	public static final int LOCK_ICON = 0; // R.drawable.ic_lock_idle_lock;
	public static final int ALARM_ICON = R.drawable.ic_lock_idle_alarm;
	public static final int CHARGING_ICON = 0; // R.drawable.ic_lock_idle_charging;
	public static final int BATTERY_LOW_ICON = 0; // R.drawable.ic_lock_idle_low_battery;
	
    private static final int MSG_MISSEDCALL_UPDATE = 401;
    private static final int MSG_MISSEDMMS_UPDATE= 402;
    private static final int MSG_TIME_UPDATE = 301;
    private static final int MSG_BATTERY_UPDATE = 302;
    private static final int MSG_CARRIER_INFO_UPDATE = 303;
    private static final int MSG_SIM_STATE_CHANGE = 304;
    private static final int MSG_RINGER_MODE_CHANGED = 305;
    private static final int MSG_PHONE_STATE_CHANGED = 306;
    private static final int MSG_CLOCK_VISIBILITY_CHANGED = 307;
    private static final int MSG_DEVICE_PROVISIONED = 308;
    private static final int MSG_DPM_STATE_CHANGED = 309;
    private static final int MSG_USER_SWITCHED = 310;
    private static final int MSG_USER_REMOVED = 311;
    private static final int MSG_KEYGUARD_VISIBILITY_CHANGED = 312;
    private static final int MSG_BOOT_COMPLETED = 313;
    
	
    private static boolean USE_UPPER_CASE = true;

	private CharSequence mDateFormatString;

	private TextView mDateView;
	private TextView mAlarmStatusView;
	private ClockView mClockView;

	// moon add start

	private ClockView mUnreadClockView;

	private LinearLayout mNoUnreadEventLinearLayout;
	private LinearLayout mUnreadEventLinearLayout;
	// private TextView mClockTextView;
	private TextView mDateTextView;

	private LinearLayout mUnreadPhoneEventLayout;
	private TextView mUnreadPhoneLabelTextView;
	private TextView mUnreadPhoneNumberTextView;
	private TextView mUnreadPhoneLastDateTextView;

	private LinearLayout mUnreadMmsEventLayout;
	private TextView mUnreadMmsLabelTextView;
	private TextView mUnreadMmsNumberTextView;
	private TextView mUnreadMmsLastDateTextView;
	private KeyguardMessageArea mKeyguardMessageArea;
	MissedCallIntentReceiver missedCallIntentReceiver;
	MissedMmsIntentReceiver missedMmsIntentReceiver;
	BatteryInfoReceiver batteryInfoReceiver;
	BatteryStatus mBatteryStatus;
	

	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_MISSEDCALL_UPDATE:
				Bundle data = msg.getData();
				int missedCallCount = data.getInt("missedCallCount");
				String missedCallPhoneNumber = data
						.getString("missedCallPhoneNumber");
				String missedCallLastDate = data
						.getString("missedCallLastDate");
				updateUnReadCallEvent(missedCallCount, missedCallPhoneNumber,
						missedCallLastDate);
				break;
			case MSG_MISSEDMMS_UPDATE:
				Bundle missedMmsdata = msg.getData();
				int missedMmsCount = missedMmsdata.getInt("missedMmsCount");
				String missedMmsLastDate = missedMmsdata
						.getString("missedMmsLastDate");
				updateUnReadMmsEvent(missedMmsCount, missedMmsLastDate);
				break;
            case MSG_BATTERY_UPDATE:
                handleBatteryUpdate((BatteryStatus) msg.obj);
                break;
			}

		}
	};

	// moon add end .

	/* <-- wangmingdong -5-7-2013 16:37:07 */
	private Context mContext;
	private ContentResolver mContentResolver;

	/* --> wangmingdong */

	public KeyguardStatusView(Context context) {
		this(context, null, 0);
	}

	public KeyguardStatusView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public KeyguardStatusView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		/* <-- wangmingdong -5-7-2013 16:37:36 */
		mContext = context;
		mContentResolver = context.getContentResolver();
		mBatteryStatus = new BatteryStatus(BATTERY_STATUS_UNKNOWN, 100, 0, 0);
		/* --> wangmingdong */
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		Resources res = getContext().getResources();
		mDateFormatString = res.getText(R.string.abbrev_wday_month_day_no_year);
		mDateView = (TextView) findViewById(R.id.date);
		mAlarmStatusView = (TextView) findViewById(R.id.alarm_status);
		mClockView = (ClockView) findViewById(R.id.clock_view);

		// Use custom font in mDateView
		mDateView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);

		// Required to get Marquee to work.
		final View marqueeViews[] = { mDateView, mAlarmStatusView };
		for (int i = 0; i < marqueeViews.length; i++) {
			View v = marqueeViews[i];
			if (v == null) {
				throw new RuntimeException("Can't find widget at index " + i);
			}
			v.setSelected(true);
		}

		// moon add start

		mNoUnreadEventLinearLayout = (LinearLayout) findViewById(R.id.no_unread_event);
		mUnreadEventLinearLayout = (LinearLayout) findViewById(R.id.unread_event);

		// mClockTextView = (TextView) findViewById(R.id.clock_text);
		mUnreadClockView = (ClockView) findViewById(R.id.unread_clock_view);
		mDateTextView = (TextView) findViewById(R.id.unread_date);

		mUnreadPhoneEventLayout = (LinearLayout) findViewById(R.id.unread_phone_event);
		// mUnreadPhoneEventLayout.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		//
		// }

		mUnreadPhoneLabelTextView = (TextView) findViewById(R.id.unread_phone_label);
		mUnreadPhoneNumberTextView = (TextView) findViewById(R.id.unread_phone_number);
		mUnreadPhoneLastDateTextView = (TextView) findViewById(R.id.unread_phone_last_date);

		mUnreadMmsEventLayout = (LinearLayout) findViewById(R.id.unread_mms_event);
		mUnreadMmsLabelTextView = (TextView) findViewById(R.id.unread_mms_label);
		mUnreadMmsNumberTextView = (TextView) findViewById(R.id.unread_mms_number);
		mUnreadMmsLastDateTextView = (TextView) findViewById(R.id.unread_mms_last_date);
		
		mKeyguardMessageArea = (KeyguardMessageArea)findViewById(R.id.keyguard_message_area);
		// moon add end .
 
		/* <-- wangmingdong -5-6-2013 20:15:50 */
		userInfo = (TextView) findViewById(R.id.lockscreen_user_info);
		Log.i("doom-lockscreen", "keyguardstatusview userInfo" + userInfo);
//		Typeface typeface = Typeface.createFromFile("/system/fonts/kaiti.ttf");
		Typeface typeface = Typeface.createFromAsset(this.getResources().getAssets(), "fonts/kaiti.ttf");
		Log.d("doom-lockscreen", "keyguardstatusview typeface:" + typeface);
		userInfo.setTypeface(typeface);
		/* --> wangmingdong */

		refresh();

	}

	/* <-- wangmingdong -5-6-2013 20:18:09 */
	private TextView userInfo;

	private static float sPixelDensity = -1f;

	public static float dpToPixel(Context context, float dp) {
		synchronized (KeyguardStatusView.class) {
			if (sPixelDensity < 0) {
				DisplayMetrics metrics = new DisplayMetrics();
				final WindowManager wm = (WindowManager) context
						.getSystemService(Context.WINDOW_SERVICE);
				wm.getDefaultDisplay().getMetrics(metrics);
				sPixelDensity = metrics.density;
			}
			return sPixelDensity * dp;
		}
	}

	public static int dpToPixel(Context context, int dp) {
		return (int) (dpToPixel(context, (float) dp) + .5f);
	}


	protected void refresh() {
		mClockView.updateTime();
		mUnreadClockView.updateTime(); // moon
		refreshDate();
		refreshAlarmStatus(); // might as well

	}

	void refreshAlarmStatus() {
		// Update Alarm status
		String nextAlarm = getNextAlarm();
		if (!TextUtils.isEmpty(nextAlarm)) {
			maybeSetUpperCaseText(mAlarmStatusView, nextAlarm);
			mAlarmStatusView.setCompoundDrawablesWithIntrinsicBounds(
					ALARM_ICON, 0, 0, 0);
			mAlarmStatusView.setVisibility(View.VISIBLE);
		} else {
			mAlarmStatusView.setVisibility(View.GONE);
		}
	}
    private void handleBatteryUpdate(BatteryStatus status) {

        final boolean batteryUpdateInteresting = isBatteryUpdateInteresting(mBatteryStatus, status);
        mBatteryStatus = status;
        mKeyguardMessageArea.onRefreshBatteryInfo(mBatteryStatus);
    }
    
	// lock screen date format
	private String getDateFormat() {
		return Settings.System.getString(mContext.getContentResolver(),
				Settings.System.DATE_FORMAT);
	}

	// lock screen date format end

	void refreshDate() {
		String mLockDateFormat = getDateFormat();
		if (mLockDateFormat == null || "".equals(mLockDateFormat)) {
			maybeSetUpperCaseText(mDateView,
					DateFormat.format(mDateFormatString, new Date()));
		} else {
			maybeSetUpperCaseText(mDateView,
					DateFormat.format(mLockDateFormat, new Date()));
		}
		maybeSetUpperCaseText(mDateTextView,
				DateFormat.format(mDateFormatString, new Date())); // moon add
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		missedCallIntentReceiver = new MissedCallIntentReceiver();
		IntentFilter myFilter = new IntentFilter();
		myFilter.addAction("com.android.phone.NotificationMgr.MissedCall_intent");
		getContext().registerReceiver(missedCallIntentReceiver, myFilter);

		missedMmsIntentReceiver = new MissedMmsIntentReceiver();
		IntentFilter mmsFilter = new IntentFilter();
		mmsFilter.addAction("com.android.phone.NotificationMgr.MissedMms_intent");
		getContext().registerReceiver(missedMmsIntentReceiver, mmsFilter);
		
		batteryInfoReceiver = new BatteryInfoReceiver();
		IntentFilter batteryFilter = new IntentFilter();
		batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);;
		getContext().registerReceiver(batteryInfoReceiver, batteryFilter);
		
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		getContext().unregisterReceiver(missedCallIntentReceiver);
		getContext().unregisterReceiver(missedMmsIntentReceiver);
		getContext().unregisterReceiver(batteryInfoReceiver);
		
	}

	public int getAppWidgetId() {
		return -2;
	}

	private void maybeSetUpperCaseText(TextView textView, CharSequence text) {
		
        if (USE_UPPER_CASE
                && textView.getId() != R.id.lockscreen_user_info) { // currently only required for date view
            textView.setText(text != null ? text.toString().toUpperCase() : null);
        } else {
            textView.setText(text);
        }
        

	}

	/*
	 * M: For CR ALPS00333114
	 * 
	 * We need update updateStatusLines when dialog dismiss which is in font of
	 * lock screen.
	 * 
	 * @see android.view.View#onWindowFocusChanged(boolean)
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus) {
			refresh();
		}
	}

	// moon add start
	private class MissedCallIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("tag", "MissedCallIntentReceiver intent.getAction():"
					+ intent.getAction());
			if (intent.getAction().equals(
					"com.android.phone.NotificationMgr.MissedCall_intent")) {
				int missedCallCount = intent.getIntExtra("MissedCallNumber", 0);
				String missedCallPhoneNumber = intent
						.getStringExtra("MissedCallPhoneNumber");
				String missedCallLastDate = intent
						.getStringExtra("MissedCallLastDate");
				Log.d("tag", "MissedCallIntentReceiver mMissedCallCount:"
						+ missedCallCount);
				if (missedCallCount > 0) {
					// mHandler.sendEmptyMessage(1);
					Message msg = new Message();
					// msg.arg1 = missedCallCount;
					msg.what = MSG_MISSEDCALL_UPDATE;
					Bundle data = new Bundle();
					data.putInt("missedCallCount", missedCallCount);
					data.putString("missedCallPhoneNumber",
							missedCallPhoneNumber);
					data.putString("missedCallLastDate", missedCallLastDate);
					msg.setData(data);
					mHandler.sendMessage(msg);
				} else {

				}

			}
		}
	}

	private class BatteryInfoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {   
			
		final int status = intent.getIntExtra(EXTRA_STATUS, BATTERY_STATUS_UNKNOWN);
        final int plugged = intent.getIntExtra(EXTRA_PLUGGED, 0);
        final int level = intent.getIntExtra(EXTRA_LEVEL, 0);
        final int health = intent.getIntExtra(EXTRA_HEALTH, BATTERY_HEALTH_UNKNOWN);
        final Message msg = mHandler.obtainMessage(
                MSG_BATTERY_UPDATE, new BatteryStatus(status, level, plugged, health));
        mHandler.sendMessage(msg);}
	}

	
	private void updateUnReadCallEvent(int missedCallCount,
			String missedCallPhoneNumber, String missedCallLastDate) {
		mNoUnreadEventLinearLayout.setVisibility(View.GONE);
		mUnreadEventLinearLayout.setVisibility(View.VISIBLE);
		mUnreadPhoneEventLayout.setVisibility(View.VISIBLE);
		if (missedCallCount == 1) {
			mUnreadPhoneLabelTextView
					.setText(R.string.lockscreen_unread_phone_label);
		} else {
			mUnreadPhoneLabelTextView.setText(mContext.getString(
					R.string.lockscreen_unread_phone_count, missedCallCount));
		}
		mUnreadPhoneNumberTextView.setText(missedCallPhoneNumber);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		if (missedCallLastDate != null && !missedCallLastDate.isEmpty()) {
			mUnreadPhoneLastDateTextView.setText(format.format(Long
					.parseLong(missedCallLastDate)));
		}
	}

	private class MissedMmsIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("tag",
					"MissedMmsIntentReceiver intent.getAction():"
							+ intent.getAction());
			if (intent.getAction().equals(
					"com.android.phone.NotificationMgr.MissedMms_intent")) {
				int missedMmsCount = intent.getIntExtra("MissedMmsNumber", 0);
				String missedMmsLastDate = intent
						.getStringExtra("MissedMmsLastDate");
				Log.d("tag", "MissedCallIntentReceiver mMissedMmsCount:"
						+ missedMmsCount);
				if (missedMmsCount > 0) {
					// mHandler.sendEmptyMessage(2);
					Message msg = new Message();
					// msg.arg1 = missedMmsCount;
					msg.what = MSG_MISSEDMMS_UPDATE;
					Bundle data = new Bundle();
					data.putInt("missedMmsCount", missedMmsCount);
					data.putString("missedMmsLastDate", missedMmsLastDate);
					msg.setData(data);
					mHandler.sendMessage(msg);
				}

			}
		}
	}

	private void updateUnReadMmsEvent(int missedMmsCount,
			String missedMmsLastDate) {
		mNoUnreadEventLinearLayout.setVisibility(View.GONE);
		mUnreadEventLinearLayout.setVisibility(View.VISIBLE);
		mUnreadMmsEventLayout.setVisibility(View.VISIBLE);
		mUnreadMmsLabelTextView.setText(R.string.lockscreen_unread_mms_label);
		mUnreadMmsNumberTextView.setText(mContext.getString(
				R.string.lockscreen_unread_mms_count, missedMmsCount));
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		if (missedMmsLastDate != null && !missedMmsLastDate.isEmpty()) {
			mUnreadMmsLastDateTextView.setText(format.format(Long
					.parseLong(missedMmsLastDate)));
		}

	}

	// moon add end .

	private String getNextAlarm() {
		String nextAlarm = Settings.System.getString(mContentResolver,
				Settings.System.NEXT_ALARM_FORMATTED);
		if (nextAlarm == null || TextUtils.isEmpty(nextAlarm)) {
			return null;
		}
		return nextAlarm;
	}
	
    private static boolean isBatteryUpdateInteresting(BatteryStatus old, BatteryStatus current) {
        final boolean nowPluggedIn = current.isPluggedIn();
        final boolean wasPluggedIn = old.isPluggedIn();
        final boolean stateChangedWhilePluggedIn =
            wasPluggedIn == true && nowPluggedIn == true
            && (old.status != current.status);

        // change in plug state is always interesting
        if (wasPluggedIn != nowPluggedIn || stateChangedWhilePluggedIn) {
            return true;
        }

        // change in battery level while plugged in
        if (nowPluggedIn && old.level != current.level) {
            return true;
        }

        // change where battery needs charging
        if (!nowPluggedIn && current.isBatteryLow() && current.level != old.level) {
            return true;
        }
        return false;
    }

}
