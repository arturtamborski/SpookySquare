package pl.arturtamborski.spookysquare;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

    private static final int FPS_MAX = 60;
    private static final int MIN_WAIT_TIME = 5;
    private static final int FRAME_PERIOD = 1000 / FPS_MAX;

    private int mFps;
    private boolean mRunning;

    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private MainSurfaceView mMainSurfaceView;

    public MainThread(MainSurfaceView mainSurfaceView, SurfaceHolder surfaceHolder) {
        super();

        mSurfaceHolder = surfaceHolder;
        mMainSurfaceView = mainSurfaceView;
    }

    @Override
    public void start() {
        super.start();

        mRunning = true;
    }

    @Override
    public void run() {
        long startTime, waitTime, totalTime = 0;
        int frameCount = 0;

        while (mRunning) {
            mCanvas = null;
            startTime = System.currentTimeMillis();
            mMainSurfaceView.update();

            try {
                mCanvas = mSurfaceHolder.lockCanvas();
                synchronized (mSurfaceHolder) {
                    mMainSurfaceView.draw(mCanvas);
                }
            } catch (Exception e) {
            } finally {
                if (mCanvas != null) {
                    try {
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    } catch (Exception e) {
                    }
                }
            }

            waitTime = FRAME_PERIOD - (System.currentTimeMillis() - startTime);
            if (waitTime >= MIN_WAIT_TIME) {
                try {
                    sleep(waitTime);
                } catch (InterruptedException e) {
                    interrupt();
                }
            }

            frameCount++;
            totalTime += System.currentTimeMillis() - startTime;
            if (frameCount == FPS_MAX) {
                mFps = (int)(1000 / (totalTime / frameCount));
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();

        mRunning = false;
    }

    public int getFps() {
        return mFps;
    }
}