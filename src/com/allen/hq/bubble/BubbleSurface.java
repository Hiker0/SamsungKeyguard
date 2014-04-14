package com.allen.hq.bubble;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.media.SoundPool;
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
	
	
	private SurfaceHolder mHolder=null; //øÿ÷∆∂‘œÛ
	private DrawThread drawThread;
	private Bitmap backgroundImage;
	//private Bitmap bubbleImage;
	private Rect   Drawrect = null;
	//private Bubble mBubble;
	private ArrayList<Bubble> mBubbleList;
	private boolean unlocked;
	
	private float orignPointX,orignPointY;

	private BubbleCallBack mCallback;
	private SoundPool mSoundPool = null;
	private int[] sounds = null;
//	private int mSound_tap_id = 0;
//	private int mSound_unlock_id = 0;
	
	private Context mContext = null;
	
	public BubbleSurface(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mHolder=getHolder();
		mHolder.addCallback(this);
		mBubbleList = new ArrayList<Bubble>(){};
		unlocked = false;
		mContext = context;
		setSound();
		//bubbleImage = BitmapFactory.decodeResource(getResources(), R.drawable.bg);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		backgroundImage.recycle();
		drawThread.stoped=true;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Drawrect = holder.getSurfaceFrame();
		backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.s5_wallpaper);
		playSound(1);
		
		drawThread = new DrawThread();
		drawThread.start();
		//drawThread.getState()


	}

	private void  createBubbles(float x, float y,int number){
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
		
		mBubble.color = r.nextInt(10);
		
		mBubble.alpha = 50 + r.nextInt(100);
		
		mBubble.death = 100 + r.nextInt(300);
		
		
		int size = 10 +  r.nextInt(30);
		if(size % 2 == 1 && size >25){
			size = size - 10;
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
		
		Paint p = new Paint(); // ± ¥•
		canvas.drawBitmap(backgroundImage, 0, 0, p);
		//canvas.drawBitmap(bubbleImage, 0, 0, p);
		int size = mBubbleList.size();
		if(unlocked && size==0){
			onStop();

		}
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
		

		paint.setColor(COLOUR[bubble.color]);
	
		int alpha = (int) (bubble.alpha*(1.0f - 0.8f*bubble.life/bubble.death));
		
		Log.d(TAG,"alpha = "+ alpha);
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

		if(unlocked){
			bubble.speedy*=1.2;
			bubble.speedx*=1.2;
		}
		
	}
	
	
	private void speedBubble(){
		
		for(int i = mBubbleList.size()-1;i>0; i--){
			Bubble bubble = mBubbleList.get(i);
			bubble.speedy = 2 * bubble.speedy;
			bubble.speedx = 2 * bubble.speedx;
		}
	}
	
	private void unlockBubble(){
		unlocked = true;
		playSound(1);
		for(int i = mBubbleList.size()-1;i>0; i--){
			Bubble bubble = mBubbleList.get(i);
			bubble.speedy = 4 * bubble.speedy;
			bubble.speedx = 4 * bubble.speedx;
		}
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
	
	private void onStop(){
		
		drawThread.stoped = true;
		if(mCallback != null){
			mCallback.onUnlock();
		}
		releaseSound();
	}
	private void playSound(int paramInt) {
		
		if (mSoundPool != null){
			mSoundPool.play(sounds[paramInt], 1.0f,
					1.0f, 0, 0, 1.0F);
		}
	}
	
	private void releaseSound() {
		
		if (mSoundPool != null){
			mSoundPool.release();
		}
	}
	
//	public void setSoundRID(int tap_id, int unlock_id) {
//		mSound_tap_id = tap_id;
//		mSound_unlock_id = unlock_id;
//	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		return handleTouchEvent(event);
	}

	public boolean  handleTouchEvent(MotionEvent event){
		if(unlocked){
			return true;
		}
		float x = event.getX();
		float y = event.getY();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			createBubbles(x,y,13);
			playSound(0);
			orignPointX = x;
			orignPointY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			createBubbles(x,y,1);
			break;
		case MotionEvent.ACTION_UP:
			float distance = (orignPointX -x)*(orignPointX -x)+(orignPointY-y)*(orignPointY-y);
			if(distance > 40000){
				unlockBubble();
			}else{
				speedBubble();
			}
			break;
		}
		return true;
	}


	class DrawThread extends Thread{
		public boolean stoped = false;
		public DrawThread(){
			
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (!stoped) {
				try {
					Canvas c = mHolder.lockCanvas();
					
					doDraw(c);
					
					mHolder.unlockCanvasAndPost(c);
					Thread.sleep(20);
				} catch (Exception e) {

				}
			}
		}
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
		
		void onUnlock();	
	}
	
	public void SetCallBack(BubbleCallBack callback){
		
		mCallback = callback;
	}
	
	public void onResume(){
		if(drawThread!=null){
			createBubbles(Drawrect.right/2 , Drawrect.bottom/2, 40);
		}
	}
 

}
