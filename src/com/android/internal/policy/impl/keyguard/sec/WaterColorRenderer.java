package com.android.internal.policy.impl.keyguard.sec;

import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.allen.hq.R;
import com.allen.hq.keygurad.LockAdapter.UnlockCallBack;
import com.android.internal.policy.impl.keyguard.sec.WaterBrushView.OnTriggerListener;

/*<---[don't suport] ZhouXF 2013-09-26 {
 import android.os.DVFSHelper;
 }--->*/
public class WaterColorRenderer implements GLSurfaceView.Renderer {
	public static final int PORTRAIT_MODE = 0;
	public static final int TABLET_MODE = 1;
	private static JniWaterColorRenderer mJniWaterColor;
	final int DISTANCE_MAX_OF_DRAG = 30;
	final int SOUND_ID_TAB = 0;
	final int SOUND_ID_UNLOC = 1;
	private final String TAG = "WaterEffect";
	private final long UNLOCK_SOUND_PLAY_TIME = 2000L;
	private boolean calledIsScreenTurnedOn = false;
	private final long DRAG_MIN_DISTANCE= 50L;
	private final long DRAG_MAX_DISTANCE = 450L;
	private final long UNLOCK_DELAY_TIME = 1200L;
	
	private float defaultX = 0.0F;
	private float defaultY = 0.0F;
	private int fpsCount = 0;

	private boolean isCleanup = false;
	private boolean isDraged = false;
	private boolean isEndEffectStart = false;
	boolean isFirstSurfaceChanged = true;
	private boolean isFirstTouched = true;
	private boolean isFpsChecked = true;
	boolean isSurfaceCreated = true;
	int logCount = 0;
	Context mContext;
	int mCountOfDirtyMode = 0;
	private Bitmap mCurrentBG = null;
	private Runnable mDefaultRunnable;
	GLSurfaceView mParent;
	private SoundPool mSoundPool = null;
	private float mSound_left_max = 1.0F;
	private float mSound_right_max = 1.0F;
	private int mSound_tap_id = 0;
	private int mSound_unlock_id = 0;
	private int mTabletMode = 0;
	final int[] pointer_xpos = new int[10];
	final int[] pointer_ypos = new int[10];
	private long prevPressTime = 0L;
	private int prevTouchMoveX = 0;
	private int prevTouchMoveY = 0;
	private Runnable releaseSoundRunnable;
	private long sleepTime = 0L;
	private int[] sounds = null;
	private long startTime = 0L;
	int[] supportedCPUClockTable = null;
	int[] supportedGPUFreqTable = null;

	class ClearWaterColorRunnable implements Runnable {
		public void run() {
			Log.d(TAG, "postDelayed");
			clearWaterColor();
			return;
		}
	}

	class ReleaseSoundRunnable implements Runnable {
		public void run() {
			if (mSoundPool != null) {
				Log.d(TAG, "WaterColor sound : release");
				mSoundPool.release();
				mSoundPool = null;
			}
			releaseSoundRunnable = null;
		}
	}

	class DefaultRunnable implements Runnable {
		public void run() {
			Log.d(TAG, "DefaultRunnable");
			mParent.setRenderMode(1);
			mJniWaterColor.onTouchEvent(0, 1, 0, pointer_xpos, pointer_ypos);
			mDefaultRunnable = null;
			setFalseDefaultEffectFlag();
			mOnTriggerListener.onTrigger();
		}
	}

	public WaterColorRenderer(Context paramContext,
			GLSurfaceView paramGLSurfaceView,
			JniWaterColorRenderer paramJniWaterColorRenderer, int paramInt,
			Bitmap paramBitmap) {
		Log.d(TAG, "WaterColorRender Constructor");
		this.mContext = paramContext;
		this.mParent = paramGLSurfaceView;
		mJniWaterColor = paramJniWaterColorRenderer;
		this.mCurrentBG = paramBitmap;
		this.mTabletMode = paramInt;
		mJniWaterColor.Init_PhysicsEngineJNI();
		this.isFirstSurfaceChanged = true;
		this.sleepTime = 0L;
	}


	private void playSound(int paramInt) {
		if (this.mSoundPool != null){
			this.mSoundPool.play(this.sounds[paramInt], this.mSound_left_max,
				this.mSound_right_max, 0, 0, 1.0F);
		}
	}

	private void removeDefaultRunnable() {
		if (this.mDefaultRunnable != null){
		Log.d(TAG,
				"mDefaultRunnable isn't null, mParent.removeCallbacks(mDefaultRunnable)");
		this.mParent.removeCallbacks(this.mDefaultRunnable);
		this.mDefaultRunnable = null;
		}
	}

	private void setFalseDefaultEffectFlag() {
		Log.d(TAG, "setFalseDefaultEffectFlag()");
		this.isFirstTouched = false;
		this.isEndEffectStart = false;
		this.calledIsScreenTurnedOn = false;
	}

	private void setSound() {
		stopReleaseSound();
		if (this.mSoundPool == null) {
			Log.d(TAG, "MotionEvent.ACTION_DOWN mSoundPool == null");
			this.mSoundPool = new SoundPool(10, 1, 0);
			this.sounds = new int[2];
			this.sounds[0] = this.mSoundPool.load(this.mContext,
					this.mSound_tap_id, 1);
			this.sounds[1] = this.mSoundPool.load(this.mContext,
					this.mSound_unlock_id, 1);
		}
	}

	private void stopReleaseSound() {
		if (this.releaseSoundRunnable != null) {
			this.mParent.removeCallbacks(this.releaseSoundRunnable);
			this.releaseSoundRunnable = null;
		}
	}

	public void changeBackground(Bitmap paramBitmap) {
		this.mCurrentBG = paramBitmap;
		mJniWaterColor.SetTexture("LockScreenBG", this.mCurrentBG, false);
	}

	public void changeNoiseTexture() {
		try {
			InputStream localInputStream = this.mContext.getResources()
					.openRawResource(R.drawable.watercolor_noise);
			mJniWaterColor.SetTexture("Noise",
					BitmapFactory.decodeStream(localInputStream), false);
			localInputStream.close();
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	void changeWaterColorBackground(Bitmap paramBitmap) {
		Log.d(TAG, "changeWaterColorBackground");
		mJniWaterColor.SetTexture("LockScreenBG", paramBitmap, false);
	}

	public void cleanUp() {
		Log.d(TAG, "cleanUp");
		removeDefaultRunnable();
		setFalseDefaultEffectFlag();
		this.isCleanup = true;
		this.sleepTime = 5L;
		this.mParent.postDelayed(new ClearWaterColorRunnable(), 50L);
		stopReleaseSound();
		this.releaseSoundRunnable = new ReleaseSoundRunnable();
		this.mParent.postDelayed(this.releaseSoundRunnable, UNLOCK_SOUND_PLAY_TIME);
	}

	public void clearWaterColor() {
		mJniWaterColor.onKeyEvent(90);
	}

	public void destroyed() {

	}

	public void onDrawFrame(GL10 paramGL10) {
		this.logCount = (1 + this.logCount);
		SystemClock.sleep(this.sleepTime);
		if (mJniWaterColor.isEmpty() == 1) {
			Log.d(TAG, "mJniWaterColor is Empty");
			this.mCountOfDirtyMode = (1 + this.mCountOfDirtyMode);
			if (this.mCountOfDirtyMode >= 2) {
				Log.d(TAG, "Drity Mode");
				this.mParent.setRenderMode(0);
				this.mCountOfDirtyMode = 0;
			}
		} else if (this.logCount > 100) {
			Log.d(TAG, "mJniWaterColor is not Empty");
			this.logCount = 0;
		}
		mJniWaterColor.Draw_PhysicsEngine();
		if (this.isFpsChecked) {
			this.fpsCount = (1 + this.fpsCount);
			long l = System.currentTimeMillis();
			if (l - this.startTime >= 100L) {
				Log.d(TAG, "fps = " + this.fpsCount);
				this.startTime = l;
				this.fpsCount = 0;
			}
		}
		return;
	}

	public void onKeyEvent(int paramInt) {
		mJniWaterColor.onKeyEvent(paramInt);
	}

	public void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2) {
		Log.d(TAG, "onSurfaceChanged, width = " + paramInt1 + ", height = "
				+ paramInt2);
		if (this.mTabletMode != 0) {
			if (this.isSurfaceCreated) {
				preSetTexture();
				mJniWaterColor.Init_PhysicsEngine(this.mTabletMode, paramInt1,
						paramInt2);
				this.isSurfaceCreated = false;
			} else {
				changeNoiseTexture();
				mJniWaterColor.onSurfaceChangedEvent(paramInt1, paramInt2);
			}
		} else {
			if (this.isFirstSurfaceChanged) {
				if (paramInt1 <= paramInt2) {
					mJniWaterColor.Init_PhysicsEngine(this.mTabletMode,
							paramInt1, paramInt2);
				} else {
					mJniWaterColor.Init_PhysicsEngine(this.mTabletMode,
							paramInt2, paramInt1);
				}
				this.isFirstSurfaceChanged = false;
			}

		}
	}

	public void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig) {
		Log.d(TAG, "onSurfaceCreated");
		if (this.mTabletMode == 0)
			preSetTexture();
		this.isSurfaceCreated = true;
	}

	public void onTouchEvent(MotionEvent paramMotionEvent) {
		int action = paramMotionEvent.getAction();
		this.pointer_xpos[0] = (int) paramMotionEvent.getRawX();
		this.pointer_ypos[0] = (int) paramMotionEvent.getRawY();
		Log.d(TAG, "onTouchEvent action = " + action);
		Log.d(TAG, "ievent.getX() = " + paramMotionEvent.getRawX());
		Log.d(TAG, "ievent.getY() = " + paramMotionEvent.getRawY());
		if (this.isCleanup) {
			Log.d(TAG, "isCleanup is true");
			return;
		}

		this.mParent.setRenderMode(1);
		if (this.isFirstTouched) {
			Log.d(TAG, "isFirstTouched is true");
			removeDefaultRunnable();
			setFalseDefaultEffectFlag();
		}
		switch (action) {

		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "ACTION_DOWN");
			mJniWaterColor.onTouchEvent(0, 1, 0, this.pointer_xpos,
					this.pointer_ypos);
			playSound(0);
			this.prevPressTime = SystemClock.uptimeMillis();
			this.prevTouchMoveX = this.pointer_xpos[0];
			this.prevTouchMoveY = this.pointer_ypos[0];
			this.isDraged = false;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "ACTION_MOVE");
			if (!this.isDraged) {
				float f1 = this.pointer_xpos[0] - this.prevTouchMoveX;
				float f2 = this.pointer_ypos[0] - this.prevTouchMoveY;
				int j = (int) Math
						.sqrt(Math.pow(f1, 2.0D) + Math.pow(f2, 2.0D));
				if (j > DRAG_MIN_DISTANCE) {
					Log.d(TAG, "distanceForDragSound = " + j);
					this.isDraged = true;
				}
				
			}else{
				float f1 = this.pointer_xpos[0] - this.prevTouchMoveX;
				float f2 = this.pointer_ypos[0] - this.prevTouchMoveY;
				int j = (int) Math
						.sqrt(Math.pow(f1, 2.0D) + Math.pow(f2, 2.0D));
				
				if(j > DRAG_MAX_DISTANCE){
					
					unlockWaterColor();
				}
			}
			mJniWaterColor.onTouchEvent(0, 1, 2, this.pointer_xpos,
					this.pointer_ypos);
			break;
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "ACTION_UP");
			long l = SystemClock.uptimeMillis() - this.prevPressTime;
			if ((this.isDraged) || (l <= DRAG_MAX_DISTANCE)) {
				break;
			}
			Log.d(TAG, "LONG PRESS UP");
			mJniWaterColor.onTouchEvent(0, 1, 1, this.pointer_xpos,
					this.pointer_ypos);
			playSound(0);
			break;
		case MotionEvent.ACTION_HOVER_ENTER:
			Log.d(TAG, "ACTION_HOVER_ENTER");
			mJniWaterColor.onTouchEvent(0, 1, 9, this.pointer_xpos,
					this.pointer_ypos);
			break;
		case MotionEvent.ACTION_HOVER_MOVE:
			Log.d(TAG, "ACTION_HOVER_MOVE");
			mJniWaterColor.onTouchEvent(0, 1, 7, this.pointer_xpos,
					this.pointer_ypos);
			break;

		default:
			break;
		}
		Log.d(TAG, "ACTION_HOVER_EXIT");
		mJniWaterColor.onTouchEvent(0, 1, 10, this.pointer_xpos,
				this.pointer_ypos);
	}

	public void onTouchEventForPatternLock(MotionEvent paramMotionEvent) {
		int i = paramMotionEvent.getAction();
		this.pointer_xpos[0] = (int) paramMotionEvent.getRawX();
		this.pointer_ypos[0] = (int) paramMotionEvent.getRawY();
		Log.d(TAG, "onTouchEventForPatternLock action = " + i);
		Log.d(TAG, "ievent.getX() = " + paramMotionEvent.getRawX());
		Log.d(TAG, "ievent.getY() = " + paramMotionEvent.getRawY());
		if (this.isCleanup) {
			Log.d(TAG, "isCleanup is true");
			return;
		}

		this.mParent.setRenderMode(1);
		if (this.isFirstTouched) {
			Log.d(TAG, "isFirstTouched is true");
			removeDefaultRunnable();
			setFalseDefaultEffectFlag();
		}
		switch (i) {
		default:
			break;
		case 0:
			Log.d(TAG, "ACTION_DOWN => ACTION_HOVER_ENTER");
			mJniWaterColor.onTouchEvent(0, 1, 9, this.pointer_xpos,
					this.pointer_ypos);
			break;
		case 2:
			Log.d(TAG, "ACTION_MOVE => ACTION_HOVER_MOVE");
			mJniWaterColor.onTouchEvent(0, 1, 7, this.pointer_xpos,
					this.pointer_ypos);
			break;
		case 1:
		}
		Log.d(TAG, "ACTION_UP");
		if (SystemClock.uptimeMillis() - this.prevPressTime <= 600L) {
			return;
		}
		Log.d(TAG, "LONG PRESS, ACTION_UP => ACTION_HOVER_EXIT");
		mJniWaterColor.onTouchEvent(0, 1, 10, this.pointer_xpos,
				this.pointer_ypos);
	}

	public void preSetTexture() {
		try {
			InputStream localInputStream1 = this.mContext.getResources()
					.openRawResource(R.drawable.watercolor_mask1);
			InputStream localInputStream2 = this.mContext.getResources()
					.openRawResource(R.drawable.watercolor_mask2);
			InputStream localInputStream3 = this.mContext.getResources()
					.openRawResource(R.drawable.watercolor_mask3);
			InputStream localInputStream4 = this.mContext.getResources()
					.openRawResource(R.drawable.watercolor_tube);
			InputStream localInputStream5 = this.mContext.getResources()
					.openRawResource(R.drawable.watercolor_noise);
			mJniWaterColor.SetTexture("Mask1",
					BitmapFactory.decodeStream(localInputStream1), false);
			mJniWaterColor.SetTexture("Mask2",
					BitmapFactory.decodeStream(localInputStream2), false);
			mJniWaterColor.SetTexture("Mask3",
					BitmapFactory.decodeStream(localInputStream3), false);
			mJniWaterColor.SetTexture("Tube",
					BitmapFactory.decodeStream(localInputStream4), false);
			mJniWaterColor.SetTexture("Noise",
					BitmapFactory.decodeStream(localInputStream5), false);
			localInputStream1.close();
			localInputStream2.close();
			localInputStream3.close();
			localInputStream4.close();
			localInputStream5.close();
			mJniWaterColor.SetTexture("LockScreenBG", this.mCurrentBG, false);

			Log.d(TAG, "this.mCurrentBG:" + this.mCurrentBG);
			return;
		} catch (Exception localException) {
			Log.d(TAG, "preSetTexture erro");
			localException.printStackTrace();
		}
	}


	public void reset() {
		Log.d(TAG, "reset");
		clearWaterColor();
		removeDefaultRunnable();
		setFalseDefaultEffectFlag();
	}

	public void screenTurnedOn() {
		Log.d(TAG, "screenTurnedOn");
		removeDefaultRunnable();
		this.isFirstTouched = true;
		this.mParent.setRenderMode(1);
		return;
	}

	public void setSoundRID(int paramInt1, int paramInt2, float paramFloat1,
			float paramFloat2) {
		this.mSound_tap_id = paramInt1;
		this.mSound_unlock_id = paramInt2;
		this.mSound_left_max = paramFloat1;
		this.mSound_right_max = paramFloat2;
	}

	public void show() {
		Log.d(TAG, "show");
		setSound();
		removeDefaultRunnable();
		setFalseDefaultEffectFlag();
		this.isEndEffectStart = false;
		this.isCleanup = false;
		clearWaterColor();
		this.mParent.setRenderMode(1);
		this.sleepTime = 5L;
	}

	
	public void showUnlockAffordance(long paramLong) {
		Log.d(TAG, "showUnlockAffordance()");
		Log.d(TAG, "calledIsScreenTurnedOn = " + this.calledIsScreenTurnedOn);
		removeDefaultRunnable();
		if (this.mDefaultRunnable == null) {
			Log.d(TAG, "mDefaultRunnable,  new Runnable()!!!");
			this.mDefaultRunnable = new DefaultRunnable();
		}
		Log.d(TAG, "mDefaultRunnable, postDelayed()!!!");
		this.mParent.postDelayed(this.mDefaultRunnable, paramLong);
		this.mParent.setRenderMode(1);
	}
	public void unlockWaterColor() {
		if(! this.isEndEffectStart)
		{
		this.isEndEffectStart = true;
		this.mParent.setRenderMode(1);
		mJniWaterColor.onKeyEvent(91);
		playSound(1);
		showUnlockAffordance(UNLOCK_DELAY_TIME);
		}
	}
	  private UnlockCallBack mOnTriggerListener;
	  public void setOnTriggerListener(UnlockCallBack listener) {
	      mOnTriggerListener = listener;
	  }
}

/*
 * Location:
 * E:\adt-bundle-windows-x86-20130219\decode\dex2jar-0.0.9.9\policy_dex2jar.jar
 * Qualified Name:
 * com.android.internal.policy.impl.keyguard.sec.WaterColorRenderer JD-Core
 * Version: 0.5.4
 */