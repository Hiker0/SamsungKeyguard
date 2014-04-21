package com.android.internal.policy.impl.keyguard.sec;

import java.io.InputStream;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Region;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.allen.hq.R;
import com.allen.hq.keygurad.LockAdapter.UnlockCallBack;
import com.allen.hq.keygurad.UnlockView;


public class WaterBrushView extends GLSurfaceView
  implements UnlockView
{
  @SuppressWarnings("unused")
private static final String DEFAULT_WALLPAPER_IMAGE_PATH = "/system/wallpaper/lockscreen_default_wallpaper.jpg";
  private static final String DEFAULT_WALLPAPER_IMAGE_PATH_MULTI_CSC = "/system/csc_contents/lockscreen_default_wallpaper.jpg";
  private static final String DEFAULT_WALLPAPER_IMAGE_PATH_MULTI_CSC_PNG = "/system/csc_contents/lockscreen_default_wallpaper.png";
  private static final String DEFAULT_WALLPAPER_IMAGE_PATH_PNG = "/system/wallpaper/lockscreen_default_wallpaper.png";
  private static final String PORTRAIT_WALLPAPER_IMAGE_PATH = "/data/data/com.sec.android.gallery3d/lockscreen_wallpaper.jpg";
  private static WaterBrushView sWaterBrushView;
  private final boolean DBG = true;
  private final String TAG = "WaterEffect";
  private float leftVolumeMax = 1.0F;
  private Context mContext;
  private Bitmap mCurrentBG = null;
  JniWaterColorRenderer mJniWaterColor;
  private WaterColorRenderer mRenderer;
  private String mWallpaperPath = null;
  private float rightVolumeMax = 1.0F;

  public WaterBrushView(Context paramContext)
  {
    super(paramContext);
    Log.d(TAG, "ColorBrushView Constructor");
    this.mContext = paramContext;
    this.mCurrentBG = setWaterColorBackground();

    boolean isTablet = false;
    
     this.mJniWaterColor = new JniWaterColorRenderer();
     
    if (isTablet)
    {
      Log.d(TAG, "isTablet is true");
      mRenderer = new WaterColorRenderer(this.mContext, this, this.mJniWaterColor, 1, this.mCurrentBG);
    }else{
      Log.d(TAG, "isTablet is false");
      mRenderer = new WaterColorRenderer(this.mContext, this, this.mJniWaterColor, 0, this.mCurrentBG);   
    }

      mRenderer.setSoundRID(R.raw.watercolor_tap,R.raw.watercolor_unlock, this.leftVolumeMax, this.rightVolumeMax);
      if (detectOpenGLES20()){
            super.setEGLContextClientVersion(2);
            super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            super.setRenderer(this.mRenderer);
            super.getHolder().setFormat(3);
      }else{
            Log.e("WaterEffect", "this machine does not support OpenGL ES2.0");
       }
  }

  private boolean detectOpenGLES20()
  {
    boolean i = false;
    ConfigurationInfo localConfigurationInfo = ((ActivityManager)this.mContext.getSystemService("activity")).getDeviceConfigurationInfo();
    if ((localConfigurationInfo != null) && (localConfigurationInfo.reqGlEsVersion >= 0x20000)){
      i = true;
    }
    return i;
  }

  public static WaterBrushView getInstance()
  {
    return sWaterBrushView;
  }

  public static WaterBrushView getInstance(Context paramContext)
  {
    if (sWaterBrushView == null)
      sWaterBrushView = new WaterBrushView(paramContext);
    return sWaterBrushView;
  }

  private Bitmap setWaterColorBackground()
  {
    Log.d(TAG, "setWaterColorBackground");
    Bitmap localBitmap1 = null;
    Bitmap localBitmap2;
    try
    {

    	InputStream localObject = null;

		InputStream localInputStream = this.mContext.getResources().openRawResource(R.drawable.keyguard_default_wallpaper);
        localObject = localInputStream;
        localBitmap1 = BitmapFactory.decodeStream((InputStream)localObject);

        ((InputStream)localObject).close();
    }
    catch (Exception localException)
    {
    	localException.printStackTrace();
    }

    localBitmap2 = localBitmap1;
    return (Bitmap)localBitmap2;
  }

  public void cleanUp()
  {
    Log.d(TAG, "cleanUp");
    this.mRenderer.cleanUp();
  }

  public boolean gatherTransparentRegion(Region paramRegion)
  {
    return false;
  }

  public long getUnlockDelay()
  {
    return 400L;
  }

  public boolean handleHoverEvent(MotionEvent paramMotionEvent)
  {
    Log.d(TAG, "handleHoverEvent event : " + paramMotionEvent.getAction());
    if (super.getRenderMode() == 0);
    super.setRenderMode(1);
    this.mRenderer.onTouchEvent(paramMotionEvent);
    return false;
  }
  public boolean handleTouchEvent(MotionEvent paramMotionEvent)
  {
    Log.d(TAG, "handleTouchEvent event : " + paramMotionEvent.getAction());
    if (super.getRenderMode() == 0);
    super.setRenderMode(1);
    this.mRenderer.onTouchEvent(paramMotionEvent);
    return true;
  }
  
  public boolean handleTouchEvent(View paramView, MotionEvent paramMotionEvent)
  {
    Log.d(TAG, "handleTouchEvent event : " + paramMotionEvent.getAction());
    if (super.getRenderMode() == 0);
    super.setRenderMode(1);
    this.mRenderer.onTouchEvent(paramMotionEvent);
    return true;
  }

  public boolean handleTouchEventForPatternLock(View paramView, MotionEvent paramMotionEvent)
  {
    Log.d(TAG, "handleTouchEventForPatternLock event : " + paramMotionEvent.getAction());
    if (super.getRenderMode() == 0);
    super.setRenderMode(1);
    this.mRenderer.onTouchEventForPatternLock(paramMotionEvent);
    return true;
  }

  public void handleUnlock(View paramView, MotionEvent paramMotionEvent)
  {
    super.setRenderMode(1);
    this.mRenderer.unlockWaterColor();
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    Log.d(TAG, "onDetachedFromWindow");
    this.mRenderer.destroyed();
  }

  public void onPause()
  {
  }

  public void onResume()
  {
  }

  protected void onWindowVisibilityChanged(int paramInt)
  {
    if (paramInt != 0)
      return;
    super.onWindowVisibilityChanged(paramInt);
  }

  public void playLockSound()
  {
  }

  public void reset()
  {
    Log.d(TAG, "reset");
    this.mRenderer.reset();
    Log.d(TAG, "requestRender()");
    super.requestRender();
  }

  public void screenTurnedOn()
  {
    Log.d(TAG, "screenTurnedOn");
    this.mRenderer.screenTurnedOn();
  }

  public void setBackground()
  {
    Log.d(TAG, "changeWaterBrushBackground");
    this.mCurrentBG = setWaterColorBackground();
    this.mRenderer.changeBackground(this.mCurrentBG);
    super.requestRender();
  }

  public void show()
  {
/*<---[We don't use flipboard wallwallpaper] ZhouXF 2013-09-26 {
    if (LockscreenWallpaper.isFlipboardWallpaper(this.mContext)){
      setBackground();
      }
}--->*/
    Log.d(TAG, "show");
    this.mRenderer.show();
  }

  public void showUnlockAffordance(long paramLong, Rect paramRect)
  {
    Log.d(TAG, "showUnlockAffordance startDelay : " + paramLong);
    this.mRenderer.showUnlockAffordance(paramLong);
  }
  
  public interface OnTriggerListener {
      public void onTrigger();
      public void onReady();
  }
  public void setOnTriggerListener(UnlockCallBack listener) {
      this.mRenderer.setOnTriggerListener(listener);
  }
}

/* Location:           E:\adt-bundle-windows-x86-20130219\decode\dex2jar-0.0.9.9\policy_dex2jar.jar
 * Qualified Name:     com.android.internal.policy.impl.keyguard.sec.WaterBrushView
 * JD-Core Version:    0.5.4
 */