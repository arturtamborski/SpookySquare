package pl.arturtamborski.spookysquare;

import java.util.Random;

import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Canvas;

public class SpookyRoad {

    private boolean mClicked;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mCurrentSegment;
    private int mLastSegment;
    private int mNumSegments;
    private int mSquareEdge;
    private int mHalfSquareEdge;

    private Paint mPaint;
    private Random mRandom;
    private RoadSegment[] mSegments;

    public SpookyRoad(int screenWidth, int screenHeight, int squareEdge) {
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mNumSegments = 100;
        mSquareEdge = squareEdge;
        mHalfSquareEdge = squareEdge / 2;

        mPaint = new Paint();
        mRandom = new Random();
        mSegments = new RoadSegment[mNumSegments];
        mSegments[0] = new RoadSegment(mSegments[0], 0);
        for (int i = 1; i < mNumSegments; i++) {
            mSegments[i] = new RoadSegment(mSegments[i-1], i);
        }

        mLastSegment = mRandom.nextInt(4) + 2;
    }

    public void update() {
        if (mClicked) {
            mClicked = false;

            mLastSegment++;
            mCurrentSegment++;
            mCurrentSegment %= mNumSegments;
            mLastSegment = Math.min(mLastSegment, mNumSegments-1);

            if (mCurrentSegment == mLastSegment) {
                mLastSegment = 3;
            }
        }
    }

    public void draw(Canvas canvas) {
        for (int i = mCurrentSegment; i < mLastSegment; i++) {
            mSegments[i].draw(canvas);
        }
    }

    public boolean contains(Rect rect, float minRatio) {
        Rect inter = new Rect(mSegments[mCurrentSegment].getSegment());

        if (inter.intersect(rect)) {
            int area = inter.width() * inter.height();
            float ratio = ((area * 100) / (rect.width() * rect.height())) / 100f;

            return ratio >= minRatio;
        }

        return false;
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setClicked(boolean clicked) {
        mClicked = clicked;
    }

    public Point getStartPosition() {
        return mSegments[0].getFirstPoint();
    }

    public int getStartDirection() {
        return mSegments[0].getDirection();
    }

    public int getNextDirection() {
        int index = Math.min(mCurrentSegment+1, mLastSegment);
        return mSegments[index].getDirection();
    }

    class RoadSegment {

        private int mIndex;
        private int mLength;
        private int mDirection;

        private Rect mSegment;

        private class Direction {
            public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
        }

        public RoadSegment(RoadSegment roadSegment, int index) {
            Point previous;
            mIndex = index;

            // loop few times because java.util.Random is not random
            while (true) {
                if (roadSegment == null) {
                    findDirection(mRandom.nextInt(4));
                    previous = findFirstPoint();
                } else {
                    findDirection(roadSegment.getDirection());
                    previous = roadSegment.getSecondPoint();
                }

                if (findLength(previous)) {
                    break;
                }
            }
            Point next = findSecondPoint(previous);
            mSegment = makeSegment(previous, next);
        }

        public void draw(Canvas canvas) {
            int color = mPaint.getColor();

            mPaint.setColor(MainActivity.bleach(color, 0.3f));
            canvas.drawRect(mSegment, mPaint);

            mPaint.setColor(color);
        }

        public Rect makeSegment(Point previous,  Point next) {
            Point temp = new Point(previous);
            Rect rect = new Rect();

            if (mDirection == Direction.RIGHT || mDirection == Direction.DOWN) {
                previous = next;
                next = temp;
            }

            rect.set(next.x - mHalfSquareEdge, next.y - mHalfSquareEdge,
                    previous.x + mHalfSquareEdge, previous.y + mHalfSquareEdge);

            return rect;
        }

        public int getDirection() {
            return mDirection;
        }

        public Rect getSegment() {
            return mSegment;
        }

        public Point getFirstPoint() {
            Point point = new Point();

            point.x = mSegment.right - mHalfSquareEdge;
            point.y = mSegment.bottom - mHalfSquareEdge;

            if (mDirection == Direction.RIGHT || mDirection == Direction.DOWN) {
                point.x = mSegment.left + mHalfSquareEdge;
                point.y = mSegment.top + mHalfSquareEdge;

            }

            return point;
        }

        public Point getSecondPoint() {
            Point point = new Point();

            point.x = mSegment.left + mHalfSquareEdge;
            point.y = mSegment.top + mHalfSquareEdge;

            if (mDirection == Direction.RIGHT || mDirection == Direction.DOWN) {
                point.x = mSegment.right - mHalfSquareEdge;
                point.y = mSegment.bottom - mHalfSquareEdge;
            }

            return point;
        }

        private void findDirection(int previous) {
            mDirection = mRandom.nextInt(2) * 2;

            if (previous == Direction.UP || previous == Direction.DOWN) {
                mDirection++;
            }
        }

        private boolean findLength(Point previous) {
            int screen = 0;

            switch (mDirection) {
                case Direction.UP:
                    screen = mScreenHeight - (mScreenHeight - previous.y);
                    break;
                case Direction.DOWN:
                    screen = mScreenHeight - previous.y;
                    break;
                case Direction.RIGHT:
                    screen = mScreenWidth - previous.x;
                    break;
                case Direction.LEFT:
                    screen = mScreenWidth - (mScreenWidth - previous.x);
                    break;
            }

            screen -= mHalfSquareEdge;
            if (screen < mSquareEdge) {
                return false;
            }

            mLength = mSquareEdge * (1 + mRandom.nextInt(screen / mSquareEdge));
            if (mIndex == mNumSegments-2) {
                mLength = screen;
            }

            return true;
        }

        private Point findFirstPoint() {
            Point point = new Point();

            switch (mDirection) {
                case Direction.UP:
                    point.x = mRandom.nextInt(mScreenWidth - mSquareEdge) + mHalfSquareEdge;
                    point.y = mScreenHeight - mHalfSquareEdge;
                    break;
                case Direction.RIGHT:
                    point.x = mHalfSquareEdge;
                    point.y = mRandom.nextInt(mScreenHeight - mSquareEdge) + mHalfSquareEdge;
                    break;
                case Direction.DOWN:
                    point.x = mRandom.nextInt(mScreenWidth - mSquareEdge) + mHalfSquareEdge;
                    point.y = mHalfSquareEdge;
                    break;
                case Direction.LEFT:
                    point.x = mScreenWidth - mHalfSquareEdge;
                    point.y = mRandom.nextInt(mScreenHeight - mSquareEdge) + mHalfSquareEdge;
                    break;
            }

            return point;
        }

        private Point findSecondPoint(Point previous) {
            Point point = new Point();

            switch (mDirection) {
                case Direction.UP:
                    point.x = previous.x;
                    point.y = previous.y - mLength;
                    break;
                case Direction.RIGHT:
                    point.x = previous.x + mLength;
                    point.y = previous.y;
                    break;
                case Direction.DOWN:
                    point.x = previous.x;
                    point.y = previous.y + mLength;
                    break;
                case Direction.LEFT:
                    point.x = previous.x - mLength;
                    point.y = previous.y;
                    break;
            }

            return point;
        }
    }
}
