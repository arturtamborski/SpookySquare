package pl.arturtamborski.spookysquare;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.util.AttributeSet;

public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private int mColor;
    private boolean mRunning;
    private float mMinArea;

    private MainThread mMainThread;
    private SpookyRoad mSpookyRoad;
    private SpookySquare mSpookySquare;

    public MainSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        setFocusable(true);
        getHolder().addCallback(this);
    }

    public void start() {
        int screenX = getWidth();
        int screenY = getHeight();
        int difficulty = new Random().nextInt(200) + 50;
        mMinArea = new Random().nextFloat();

        mRunning = true;
        mColor = MainActivity.getRandomColor();

        mSpookyRoad = new SpookyRoad(screenX, screenY, difficulty);
        mSpookySquare = new SpookySquare(difficulty / 2);
        mSpookySquare.setDirection(mSpookyRoad.getStartDirection());
        mSpookySquare.setPosition(mSpookyRoad.getStartPosition());

        mMainThread = new MainThread(this, getHolder());
        mMainThread.setPriority(Thread.MAX_PRIORITY);
        mMainThread.start();
    }

    public void stop() {
        boolean retry = true;
        mRunning = false;

        mMainThread.interrupt();
        while (retry) {
            try {
                mMainThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mMainThread = null;
        mSpookyRoad = null;
        mSpookySquare = null;
    }

    public void input() {
        if (mRunning) {
            mColor = MainActivity.getRandomColor();
            mSpookyRoad.setColor(mColor);
            mSpookySquare.setColor(MainActivity.getRandomColor());
            mSpookyRoad.setClicked(true);
            mSpookySquare.setClicked(true);
            mSpookySquare.setDirection(mSpookyRoad.getNextDirection());
        }
    }

    public void update() {
        if (mRunning) {
            mSpookyRoad.update();
            mSpookySquare.update();

            if (!mSpookyRoad.contains(mSpookySquare.getRect(), mMinArea)) {
                mRunning = false;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(mColor);

        mSpookyRoad.draw(canvas);
        mSpookySquare.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                input();
                break;
        }

        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();

        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stop();
    }
}