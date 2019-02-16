
# PopupWindow-Common
a easy way to use PopupWindow, just same as using AlertDialog.

### sample
```java
  new AlertPopupWindow.Builder(MainActivity.this)
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
        .setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_drawable))
        .setAnchorView(v)
        .show();
```

### 注意事项
1. showAtLocation 和 showAsDropDown 的区别 

    * showAtLocation 位置固定； showAsDropDown 位置是相对与AnchorView；
    * showAsDropDown 拥有**自我调节位置的能力**； 优先向下弹，如果AnchorView的下面空间不够，则尝试向AnchorView上面弹。如果AnchorView的上面空间也不够，那么还是向下弹，但是会保证最底部可见,这里只是大概描述，**详见PopupWindow.findDropDownPosition()**；

2. Gravity.DISPLAY_CLIP_VERTICAL 和 Gravity.DISPLAY_CLIP_HORIZONTAL

    showAsDropDown 默认会含有Gravity.DISPLAY_CLIP_VERTICAL，showAtLocation 默认都不会有；详见下图

3. showAtLocation 和 showAsDropDown 为什么设置同样的 Gravity 得到不一样的效果

    首先Gravity属性是作用在Window中的

   ``` java
      final WindowManager.LayoutParams p = new WindowManager.LayoutParams();
      p.gravity = computeGravity();
      mWindowManager.addView(decorView, p);
   ```  

   记住只要设置Gravity.Right|Gravity.Top 那么该Window就会被放在右上方；
   那么为什么showAsDropDown(anchor,x,y,gravity) 这样设置后效果就不一样呢，源码告诉了我们一切

   ```java
      mAnchoredGravity = gravity; //这个Gravity并没有设置在WindowManager.LayoutParams p中
   ```

   这个 mAnchoredGravity 是为了帮助我们计算位置的，而真正作用在Window的Gravity被默认设置成了Gravity.START | Gravity.TOP | Gravity.DISPLAY_CLIP_VERTICAL
    有兴趣的可以看看源码。

4. **PopupWindow窗口类型是子Window， Dialog的窗口类型属于应用类型的和Activity的Window同级.**
