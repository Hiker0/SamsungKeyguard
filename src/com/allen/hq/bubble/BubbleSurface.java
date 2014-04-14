package com.allen.hq.bubble;

import java.util.ArrayList;
import java.util.Random;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.allen.hq.R;


public class BubbleSurface extends SurfaceView 
			implements SurfaceHolder.Callback {
	private static final String TAG = "BubbleSurface";
	private final int[] COLOUR = {0xffffff,0xffE717b6,
			0xffffff,0xffffff,0xff1fffda,0xffffff,0xffffff
			,0xffffed7a,0xffffff,0xffffff,};
	
	private final int DEFAULTCORLOR = 0xffffff;
	private final int coverCORLOR = 0x00444444;
	private SurfaceHolder mHolder=null; 
	private DrawThread drawThread;
	private Bitmap backgroundImage;
	//private Bitmap bubbleImage;
	private Rect   Drawrect = null;
	//private Bubble mBubble;
	private ArrayList<Bubble> mBubbleList;

	private static final int STATE_NORMAL = 0;
 	private static final int STATE_UNLOCKING = 1;
	private static final int STATE_UNLOCKED = 2;
	private int lockState = STATE_NORMAL;

	private float orignPointX,orignPointY;

	private BubbleCallBack mCallback;
	private SoundPool mSoundPool = null;
	private int[] sounds = null;
	private Object lock = new Object();
//	private int mSound_tap_id = 0;
//	private int mSound_unlock_id = 0;
	
	private Context mContext = null;
	
	Handler handle = new Handler();
	
	Runnable runnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mCallback != null){
				Log.d(TAG,"Callback ");
                drawThread.stoped=true;
				lockState = STATE_UNLOCKED;
				mCallback.onTrigger();
			}
		}
		
	};
	
	public BubbleSurface(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mHolder=getHolder();
		setZOrderOnTop(true);
		mHolder.setFormat(PixelFormat.TRANSPARENT);
		mHolder.addCallback(this);
		mBubbleList = new ArrayList<Bubble>(){};
		lockState = STATE_NORMAL;
		mContext = context;
		setSound();
		//bubbleImage = BitmapFactory.decodeResource(getResources(), R.drawable.bg);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
				Log.d(TAG,"surfaceChanged ");
		// TODO Auto-generated method stub
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//drawThread.stoped = true;
		lockState = STATE_UNLOCKED;
		Log.d(TAG,"surfaceDestroyed ");
		onStop();
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG,"surfaceCreated ");
		Drawrect = holder.getSurfaceFrame();
		lockState = STATE_NORMAL;
		setSound();
		backgroundImage = createBitmap();
		
		mCallback.onReady();
        	if(drawThread == null){
			drawThread = new DrawThread();
			drawThread.start();
		}
		
	}

	private void  createBubbles(float x, float y,int number,boolean isScreenOn){
		int size = mBubbleList.size();	
		if(size > 350){
			return;
		}
		for(int i = 0;i < number; i++){
			Bubble bubble = createBubble(x,y);
			mBubbleList.add(bubble);
		}
		
	}
	private Bubble  createBubble(float x, float y){
		
		Bubble mBubble = new Bubble();
		
		
		mBubble.life  = 0;
		
		Random r=new Random();
		
		mBubble.color = getColor(backgroundImage, (int)x, (int)y)+coverCORLOR;
		
		mBubble.alpha = 80 + r.nextInt(100);
		
		mBubble.death = 100 + r.nextInt(300);
		
		
		int size = 10 +  r.nextInt(22);
		if(size % 4 < 3 && size >15){
			size = size - 8;
		}
		mBubble.size = size;
		
		mBubble.x = x+r.nextInt(30);
		mBubble.y = y+r.nextInt(30);
		
		mBubble.speedy = (float) (3.0f+0.1f * r.nextInt(80));

		mBubble.speedx = (float) (3.0f-0.1f * r.nextInt(60));
		
		return mBubble;
	}

	
	private void doDraw(Canvas canvas){
		super.draw(canvas);
		//Log.d(TAG,"doDraw");
		Paint p = new Paint(); 
		canvas.drawBitmap(backgroundImage, 0, 0, p);
		//canvas.drawBitmap(bubbleImage, 0, 0, p);
		//canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		for(int i = mBubbleList.size()-1;i>=0; i--){
			Bubble bubble = mBubbleList.get(i);
			drawBubble(bubble,canvas,p);
		}
		
	}
	
	private void drawBubble(Bubble bubble, Canvas canvas, Paint paint){
		
		if(bubble.life == bubble.death ){
			mBubbleList.remove(bubble);
			return;
		}
		paint.setStyle(Paint.Style.FILL);
		

		paint.setColor(bubble.color);
	
		int alpha = (int) (bubble.alpha*(1.0f - 0.8f*bubble.life/bubble.death));
		
		
		paint.setAlpha(alpha);
		paint.setAntiAlias(true);
		
		canvas.drawCircle(bubble.x, bubble.y, bubble.size, paint);
		
		Random r=new Random(); 
		float speedy = (float) (bubble.speedy+0.25f-0.1f*r.nextInt(5));
		float speedx  = (float) (bubble.speedx+0.25f-0.1f*r.nextInt(5));
		
		bubble.y-=speedy;
		bubble.x-=speedx;
		bubble.life+=1;
		
		if(bubble.y < 0 || bubble.x < 0){
			bubble.life = bubble.death;
		}

		if(lockState == STATE_UNLOCKING){
			bubble.speedy*=1.2;
			bubble.speedx*=1.2;
		}
		
	}
	
	
	private void speedBubble(){
		Log.d(TAG,"speedBubble");
		for(int i = mBubbleList.size()-1;i>0; i--){
			Bubble bubble = mBubbleList.get(i);
			bubble.speedy = 2 * bubble.speedy;
			bubble.speedx = 2 * bubble.speedx;
		}
	}
	
	private void unlockBubble(){
		Log.d(TAG,"unlockBubble");
		lockState = STATE_UNLOCKING;
		handle.postDelayed(runnable,600);
		playSound(1);
		for(int i = mBubbleList.size()-1;i>0; i--){
			Bubble bubble = mBubbleList.get(i);
			bubble.speedy = 4 * bubble.speedy;
			bubble.speedx = 4 * bubble.speedx;
		}
		
		//handle.postDelayed(runnable,300);
	}  
	

	
	private void setSound() {

		if (mSoundPool == null) {
			mSoundPool = new SoundPool(10, 1, 0);
			sounds = new int[2];
			sounds[0] = mSoundPool.load(mContext,
					R.raw.walercolor_tap, 1);
			sounds[1] = mSoundPool.load(mContext,
					R.raw.walercolor_unlock, 1);
		}
	}
	
	private void playSound(int paramInt) {
		ContentResolver localContentResolver = mContext.getContentResolver();
    int j = Settings.System.getInt(localContentResolver, "lockscreen_sounds_enabled",0);
    
		AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
      int RingerMode = audio.getRingerMode();
      if(RingerMode == AudioManager.RINGER_MODE_SILENT 
              || RingerMode == AudioManager.RINGER_MODE_VIBRATE || j != 1){
          return;
      }
		if (mSoundPool != null){
			mSoundPool.play(sounds[paramInt], 1.0f,
					1.0f, 0, 0, 1.0F);
		}
	}
	
	private void releaseSound() {
		
		if (mSoundPool != null){
			mSoundPool.release();
			mSoundPool = null;
		}
	}
	
    public  Bitmap createBitmap() {
    	
    	Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.s5_wallpaper);
        int x, y;
       
        x = Math.abs(bitmap.getWidth() - Drawrect.width()) / 2;
        y = Math.abs(bitmap.getHeight() - Drawrect.height()) / 2;

        if(x > 0 || y > 0) {
            Bitmap bitmap2 = Bitmap.createBitmap( bitmap, x, y, Drawrect.width(),Drawrect.height());
            bitmap.recycle();
            return bitmap2;
        }

        return bitmap;
    }
	public int getColor(Bitmap bitmap,int x,int y){
		
		if(bitmap != null){
			int rgbPixel = bitmap.getPixel(x, y);
			return rgbPixel;
		}
		return DEFAULTCORLOR;
		
	}
//	public void setSoundRID(int tap_id, int unlock_id) {
//		mSound_tap_id = tap_id;
//		mSound_unlock_id = unlock_id;
//	}
	private void onStop(){
		Log.d(TAG,"onStop");
		
		handle.removeCallbacks(runnable);
		drawThread.stoped=true;
		drawThread = null;
				
		releaseSound();
		backgroundImage.recycle();
		mBubbleList.clear();
		
	}
	
	public void onResume(){
		Log.d(TAG,"onResume drawThread="+drawThread);
		lockState = STATE_NORMAL;
		/*
		setSound();
		backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.keyguard_default_wallpaper);
		createBubbles(Drawrect.right/2 , Drawrect.bottom/2, 40);
		playSound(1);
		mCallback.onReady();
		drawThread = new DrawThread();
		drawThread.start();*/
	}	
	
	
		public void onScreenTurnOn(){
		Log.d(TAG,"onScreenTurnOn");
		
			createBubbles(Drawrect.right/2 , Drawrect.bottom/2, 45,true);
		

	}	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		handleTouchEvent(event);
		return true;
	}

	public boolean  handleTouchEvent(MotionEvent event){
		if(lockState != STATE_NORMAL){
			Log.d(TAG,"Keyguard unlocked");
			return true;
		}
		float x = event.getRawX();
		float y = event.getRawY();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			createBubbles(x,y,23,false);
			playSound(0);
			orignPointX = x;
			orignPointY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			createBubbles(x,y,1,false);
			break;
		case MotionEvent.ACTION_UP:
			float distance = (orignPointX -x)*(orignPointX -x)+(orignPointY-y)*(orignPointY-y);
			if(distance > 40000){
				//unlockBubble();
			}else{
				//speedBubble();
			}
			break;
		}
		return true;
	}



	class DrawThread extends Thread{
		public boolean stoped = false;
		public DrawThread (){
			Log.d(TAG,"new DrawThread");
		}
		@Override
		public void run() {
		Log.d(TAG,"Thread start run");
		    // TODO Auto-generated method stub
		    while (!stoped) {
		    	synchronized(mHolder){
		         try {
								if(lockState != STATE_UNLOCKED){
									Canvas c = mHolder.lockCanvas();
									//invalidate();
									doDraw(c);
									mHolder.unlockCanvasAndPost(c);
								}
				     		Thread.sleep(20);
		        	} catch (Exception e) {
								Log.d(TAG,"Thread error");
								if(mCallback != null){
			        //drawThread.stoped=true;
							//lockState = STATE_UNLOCKED;
							//mCallback.onTrigger();
							}	
		        }//end try
		    	}	//end 	synchronized    
			}//end while
		}//end run
}

	
	class Bubble {
		
		public float x,y,z;
		public int color;
		public int alpha;
		public float speedx;
		public float speedy;
		public float scale;
		public int life;
		public int death;
		public int size;
		
	}

	public interface BubbleCallBack{
		
      public void onTrigger();
      public void onReady();
	}
	
	public void SetCallBack(BubbleCallBack callback){
		
		mCallback = callback;
	}

}
