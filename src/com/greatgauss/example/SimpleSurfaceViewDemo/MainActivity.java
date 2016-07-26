package com.greatgauss.example.SimpleSurfaceViewDemo; 

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.graphics.Rect;  
import android.content.Context;

import android.util.Log;

public class MainActivity extends Activity {
	private final String TAG = "main";

    private void printStackTrace() {
        StackTraceElement st[]= Thread.currentThread().getStackTrace();
        for(int i=2;i<st.length;i++)
            Log.d(TAG, i+":"+st[i]);

        Log.d(TAG, "\n\n");
    }

    private void printThreadId() {
        Log.d(TAG, "Thread " + Thread.currentThread().getName() +"(#" + Thread.currentThread().getId() + ")");
    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
        printThreadId();
        Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(new MySurfaceView(this));
	}

    public class MyThread extends Thread
    {
    private SurfaceHolder holder;
    private boolean run;

    public MyThread(SurfaceHolder holder)
    {
        this.holder = holder;
        run = true;
    }

    @Override
    public void run()
    {
        int counter = 0;
        Canvas canvas = null;
        Log.d(TAG, "Thread " + Thread.currentThread().getName() +"(#" + Thread.currentThread().getId() + ")");
        while(run) {
            try {
                // get Canvas Object and lock it
                canvas= holder.lockCanvas();

                // set background color
                canvas.drawColor(Color.WHITE);

                Paint p = new Paint();

                p.setColor(Color.RED);
                p.setTextSize(30);

                Rect rect = new Rect(100, 50, 500, 300);
                canvas.drawRect(rect,p);

                p.setColor(Color.BLACK);
                canvas.drawText("Interval = " + (counter++) + " seconds.", 100, 410, p);
                Thread.sleep(1000);
            }  
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                if(canvas != null) {
                    holder.unlockCanvasAndPost(canvas); 
                }
            }
        }  
    }  

    public boolean isRun()
    {
        return run;
    }

    public void setRun(boolean run)
    {
        this.run = run;
    }
    }

  
    public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback
    {  
        private SurfaceHolder holder;  
        private MyThread myThread;  

        public MySurfaceView(Context context)
        {
            super(context);

            holder = this.getHolder();

            holder.addCallback(this);

            myThread = new MyThread(holder);
        }  

        //Implement the APIs of Interface SurfaceHolder.Callback
        //The methods are called in MainThread
        
        @Override  
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)  
        {  
            Log.d(TAG, "surfaceChanged");
            printThreadId();
            printStackTrace();
        }  

        @Override 
        public void surfaceCreated(SurfaceHolder holder) 
        {  
            Log.d(TAG, "surfaceCreated. Can draw on the canvas of the Surface");
            printThreadId();
            printStackTrace();
            myThread.setRun(true);
            myThread.start();
        }  

        @Override
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            Log.d(TAG, "surfaceDestroyed. SHOULD NOT draw on the canvas of the Surface");
            printThreadId();
            printStackTrace();
            myThread.setRun(false);
        } 
        }
}
