package pl.arturtamborski.spookysquare;

import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Canvas;

public class SpookySquare {

    private boolean mClicked;
    private int mX, mY;
    private int mSpeed = 3;
    private int mDirection;
    private int mHalfEdgeLength;

    private Rect mRect;
    private Paint mPaint;

    private class Direction {
        public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
    }

    public SpookySquare(int edgeLength) {
        mHalfEdgeLength = edgeLength / 2;

        mRect = new Rect(mX, mY, edgeLength, edgeLength);
        mPaint = new Paint();
        mPaint.setColor(MainActivity.getRandomColor());
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(mRect, mPaint);
    }

    public void update() {
        switch (mDirection) {
            case Direction.UP:      mY -= mSpeed; break;
            case Direction.LEFT:    mX -= mSpeed; break;
            case Direction.DOWN:    mY += mSpeed; break;
            case Direction.RIGHT:   mX += mSpeed; break;
        }

        if (mClicked) {
            mClicked = false;
            mSpeed++;
        }

        mRect.offsetTo(mX - mHalfEdgeLength, mY - mHalfEdgeLength);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setClicked(boolean clicked) {
        mClicked = clicked;
    }

    public void setEdgeLength(int length) {
        mHalfEdgeLength = length / 2;
    }

    public int getEdgeLength() {
        return mHalfEdgeLength * 2;
    }

    public void setPosition(Point position) {
        mX = position.x;
        mY = position.y;
        mRect.set(mX - mHalfEdgeLength, mY - mHalfEdgeLength,
                mX + mHalfEdgeLength, mY + mHalfEdgeLength);
    }

    public void setDirection(int direction) {
        mDirection = direction;
    }

    public Rect getRect() {
        return mRect;
    }
}
