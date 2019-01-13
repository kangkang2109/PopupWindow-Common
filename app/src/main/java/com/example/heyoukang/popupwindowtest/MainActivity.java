package com.example.heyoukang.popupwindowtest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.popup.AlertPopupWindow;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PopupActivity";
    private View mPopupView;
    private int mGravity;
    private boolean mIsAnchor;
    private boolean mIsClipV;
    private boolean mIsClipH;
    private EditText x;
    private EditText y;
    private Set<Integer> mSet = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mPopupView = findViewById(R.id.popup);
        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        ViewGroup horizontal = findViewById(R.id.horizontal);
        for (int i = 0; i < horizontal.getChildCount(); i++) {
            View child = horizontal.getChildAt(i);
            child.setOnClickListener(this);
        }

        ViewGroup vertical = findViewById(R.id.vertical);
        for (int i = 0; i < vertical.getChildCount(); i++) {
            View child = vertical.getChildAt(i);
            child.setOnClickListener(this);
        }
        mPopupView = findViewById(R.id.popup);
        mPopupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGravity = 0;
                mSet.forEach(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        mGravity |= integer;
                    }
                });
                Log.e(TAG, "onClick: " + mGravity );
                int xo = x.getText() == null || x.getText().toString().equals("")  ? 0 : Integer.parseInt(x.getText().toString());
                int yo = y.getText() == null || y.getText().toString().equals("")  ? 0 : Integer.parseInt(y.getText().toString());
                AlertPopupWindow.Builder builder = new AlertPopupWindow.Builder(MainActivity.this)
                        .setGravity(mGravity)
                        .setOffset(xo, yo)
                        .setView(R.layout.activity_test)
                        .setOutsideTouchable(true)
                        .setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                Toast.makeText(MainActivity.this, "dismiss", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setClipHorizontalEnabled(mIsClipH)
                        .setClipVerticalEnabled(mIsClipV)
                        .setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_drawable));
                        //.setAnimationStyle()
                        //.setFocusable(false)

                if (mIsAnchor) {
                    builder.setAnchorView(v).show();
                } else {
                    builder.setParentView(v).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.anchor) {
            mIsAnchor = ((CheckBox)v).isChecked();
            return;
        }
        if (v.getId() == R.id.clip_hor) {
            mIsClipH = ((CheckBox)v).isChecked();
            return;
        }
        if (v.getId() == R.id.clip_ver) {
            mIsClipV = ((CheckBox)v).isChecked();
            return;
        }
        int flag = 0;
        switch (v.getId()) {
            case R.id.left :
                flag = Gravity.LEFT;
                break;
            case R.id.center_hor :
                flag = Gravity.CENTER_HORIZONTAL;
                break;
            case R.id.right :
                flag = Gravity.RIGHT;
                break;
            case R.id.top :
                flag = Gravity.TOP;
                break;
            case R.id.center_ver :
                flag = Gravity.CENTER_VERTICAL;
                break;
            case R.id.bottom :
                flag = Gravity.BOTTOM;
                break;
            case R.id.center :
                flag = Gravity.CENTER;
                break;
        }
        CheckBox cv = (CheckBox)v;
        if (cv.isChecked()) {
            mSet.add(flag);
        } else {
            mSet.remove(flag);
        }
        Log.e(TAG, "onClick: " + mSet);
    }
}