package com.example.popup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 *  注意点 ：
 *  1. Gravity.DISPLAY_CLIP_VERTICAL 和 Gravity.DISPLAY_CLIP_HORIZONTAL
 *  showAsDropDown 默认Gravity会添加DISPLAY_CLIP_VERTICAL,导致子Window会超出屏幕之外
 *  showAtLocation 默认Gravity不会添加下面两个标记,所以子Window不会超出屏幕范围之外
 *  Gravity.DISPLAY_CLIP_VERTICAL
 *  Gravity.DISPLAY_CLIP_HORIZONTAL
 *  2. showAtLocation 和 showAsDropDown 为什么设置同样的Gravity得到不一样的效果
 *
 *  3. showAsDropDown如何设置个具体的Width或者Height 那么它会自我调整位置
 *      优先向下调整,如果anchor的下方位置不够;那么朝anchor的上方弹,如果上方还不够,那么尝试
 *  4. showAsDropDown 会根据Window位置变化而进行调整PopupWindow的位置;
 *      showAtLocation比较简单,位置是屏幕的绝对坐标;
 *  5.
 *
 */

public class AlertPopupWindow extends PopupWindow {

    public static final String TAG = "AlertPopupWindow";

    private Builder mBuilder;
    private int mContentViewWidth;
    private int mContentViewHeight;

    private void apply(Builder builder) {
        mBuilder = builder;
        setHeight(mBuilder.mHeight);
        setWidth(mBuilder.mWidth);
        setContentView(mBuilder.mContentView);
        setOutsideTouchable(mBuilder.mIsOutTouchable);
        setAnimationStyle(mBuilder.mAnimationStyle);
        setOnDismissListener(mBuilder.mOnDismissListener);
        setBackgroundDrawable(mBuilder.mBackground);
        setFocusable(mBuilder.mFocusable);
        setTouchable(mBuilder.mTouchable);
    }

    public void show() {
        if (mBuilder.mBaseView == null) {
            return;
        }
        View view = mBuilder.mContentView;
        measureView(view);
        showInner();
    }

    private void measureView(View view) {
        int mode = View.MeasureSpec.UNSPECIFIED;
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, mode);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, mode);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        mContentViewWidth = view.getMeasuredWidth();
        mContentViewHeight = view.getMeasuredHeight();
        Log.e(TAG, "measureView: width:" + mContentViewWidth + " height:" + mContentViewHeight);
    }

    private void showInner() {
        View v = mBuilder.mBaseView;
        int x = mBuilder.mXOffset;
        int y = mBuilder.mYOffset;
        int gravity = mBuilder.mGravity;
        if (!mBuilder.mIsAnchor) {
            showAtLocation(v, gravity, x, y);
            return;
        }

        final int absGravity = Gravity.getAbsoluteGravity(gravity, v.getLayoutDirection());
        if (absGravity == Gravity.CENTER) {
            showAtLocation(v, gravity, x, y);
            return;
        }

        final int hgrav = absGravity & Gravity.HORIZONTAL_GRAVITY_MASK;
        switch (hgrav) {
            default:
            case Gravity.RIGHT:
                // Do nothing, we're already in position.
                break;
            case Gravity.LEFT:
                x = -(mContentViewWidth + x);
                break;
            case Gravity.CENTER_HORIZONTAL:
                x -= mContentViewWidth / 2 - v.getMeasuredWidth() / 2;
                break;
        }

        final int vgrav = absGravity & Gravity.VERTICAL_GRAVITY_MASK;

        switch (vgrav) {
            default:
            case Gravity.BOTTOM:
                // Do nothing, we're already in position.
                break;
            case Gravity.TOP:
                setOverlapAnchor(true);
                y = -(mContentViewHeight + y);
                break;
            case Gravity.CENTER_VERTICAL:
                y -= mContentViewHeight / 2 + v.getMeasuredHeight() / 2;
                break;
        }

        showAsDropDown(v, x, y, gravity);
    }

    public static class Builder {
        private Context mContext;
        private LayoutInflater mInflater;
        private View mContentView;
        private int mWidth = WindowManager.LayoutParams.WRAP_CONTENT;
        private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT;
        private View mBaseView;
        private int mXOffset;
        private int mYOffset;
        private boolean mIsOutTouchable;
        private boolean mClipHEnable;
        private boolean mClipVEnable;
        private int mAnimationStyle;
        private int mGravity;
        private boolean mIsAnchor;
        private boolean mFocusable;
        private boolean mTouchable;
        private Drawable mBackground;

        private PopupWindow.OnDismissListener mOnDismissListener;

        public Builder(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        public Builder setView(View contentView) {
            mContentView = contentView;
            return this;
        }

        public Builder setView(int layoutId) {
            mContentView = mInflater.inflate(layoutId, null);
            return this;
        }

        public Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public Builder setHeight(int height) {
            mHeight = height;
            return this;
        }

        public Builder setWidthAndHeight(int width, int height) {
            setWidth(width);
            setHeight(height);
            return this;
        }

        public Builder setGravity(int gravity) {
            mGravity = gravity;
            return this;
        }

        public Builder setAnchorView(View v) {
            mBaseView = v;
            mIsAnchor = true;
            return this;
        }

        public Builder setParentView(View v) {
            mBaseView = v;
            mIsAnchor = false;
            return this;
        }

        public Builder setXOffset(int x) {
            mXOffset = x;
            return this;
        }

        public Builder setYOffset(int y) {
            mYOffset = y;
            return this;
        }

        public Builder setOffset(int x, int y) {
            mXOffset = x;
            mYOffset = y;
            return this;
        }

        public Builder setOutsideTouchable(boolean touchable) {
            mIsOutTouchable = touchable;
            return this;
        }

        public Builder setAnimationStyle(int style) {
            mAnimationStyle = style;
            return this;
        }

        public Builder setClipHorizontalEnabled(boolean enabled) {
            mClipHEnable = enabled;
            return this;
        }

        public Builder setClipVerticalEnabled(boolean enabled) {
            mClipVEnable = enabled;
            return this;
        }

        public Builder setFocusable(boolean focusable) {
            mFocusable = focusable;
            return this;
        }

        public Builder setTouchable(boolean focusable) {
            mTouchable = focusable;
            return this;
        }

        public Builder setBackgroundDrawable(Drawable d) {
            mBackground = d;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener listener) {
            mOnDismissListener = listener;
            return this;
        }

        public AlertPopupWindow create() {
            AlertPopupWindow alertPopupWindow = new AlertPopupWindow();
            if (mClipVEnable) {
                mGravity |= Gravity.DISPLAY_CLIP_VERTICAL;
            }
            if (mClipHEnable) {
                mGravity |= Gravity.DISPLAY_CLIP_HORIZONTAL;
            }
            alertPopupWindow.apply(this);
            return alertPopupWindow;
        }

        public void show() {
            AlertPopupWindow popupWindow = create();
            popupWindow.show();
        }
    }
}

