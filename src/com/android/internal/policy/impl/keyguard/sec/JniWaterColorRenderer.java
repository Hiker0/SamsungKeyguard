package com.android.internal.policy.impl.keyguard.sec;

import android.graphics.Bitmap;
import android.util.Log;

public class JniWaterColorRenderer
{
  private final String TAG = "WaterEffect";
  public int mNativeJNI = -1;

  static
  {
    System.loadLibrary("WaterColorEffect");
  }

  public JniWaterColorRenderer()
  {
    Log.e(TAG, "JniWaterColorRenderer is called");
  }

  private static native void native_DeInit_JNI(int paramInt);

  private static native void native_Draw_PhysicsEngine(int paramInt);

  private static native int native_Init_JNI();

  private static native void native_Init_PhysicsEngine(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  private static native void native_SetTexture(int paramInt, String paramString, Bitmap paramBitmap, boolean paramBoolean);

  private static native int native_isEmpty(int paramInt);

  private static native void native_onCustomEvent(int paramInt1, int paramInt2, float paramFloat);

  private static native void native_onKeyEvent(int paramInt1, int paramInt2);

  private static native void native_onSensorEvent(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3);

  private static native void native_onSurfaceChangedEvent(int paramInt1, int paramInt2, int paramInt3);

  private static native void native_onTouchEvent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2);

  public void DeInit_PhysicsEngineJNI()
  {
    native_DeInit_JNI(this.mNativeJNI);
  }

  public void Draw_PhysicsEngine()
  {
    native_Draw_PhysicsEngine(this.mNativeJNI);
  }

  public void Init_PhysicsEngine(int paramInt1, int paramInt2, int paramInt3)
  {
    native_Init_PhysicsEngine(this.mNativeJNI, paramInt1, paramInt2, paramInt3);
  }

  public void Init_PhysicsEngineJNI()
  {
    Log.e(TAG, "Init_PhysicsEngineJNI is called");
    this.mNativeJNI = native_Init_JNI();
  }

  public void SetTexture(String paramString, Bitmap paramBitmap, boolean paramBoolean)
  {
    Log.i(TAG, "SetBitmapData " + paramBoolean);
    native_SetTexture(this.mNativeJNI, paramString, paramBitmap, paramBoolean);
  }

  public int isEmpty()
  {
    return native_isEmpty(this.mNativeJNI);
  }

  public void onCustomEvent(int paramInt, float paramFloat)
  {
    native_onCustomEvent(this.mNativeJNI, paramInt, paramFloat);
  }

  public void onKeyEvent(int paramInt)
  {
    native_onKeyEvent(this.mNativeJNI, paramInt);
  }

  public void onSensorEvent(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    native_onSensorEvent(this.mNativeJNI, paramInt, paramFloat1, paramFloat2, paramFloat3);
  }

  public void onSurfaceChangedEvent(int paramInt1, int paramInt2)
  {
    native_onSurfaceChangedEvent(this.mNativeJNI, paramInt1, paramInt2);
  }

  public void onTouchEvent(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    native_onTouchEvent(this.mNativeJNI, paramInt1, paramInt2, paramInt3, paramArrayOfInt1, paramArrayOfInt2);
  }
}

/* Location:           E:\adt-bundle-windows-x86-20130219\decode\dex2jar-0.0.9.9\policy_dex2jar.jar
 * Qualified Name:     com.android.internal.policy.impl.keyguard.sec.JniWaterColorRenderer
 * JD-Core Version:    0.5.4
 */