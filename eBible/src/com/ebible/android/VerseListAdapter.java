package com.ebible.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

class VerseListAdapter extends TextView
{
	Paint mStrokePaint;
public VerseListAdapter(Context context) {
	super(context);
	this.setupPaint();
}
public VerseListAdapter(Context context, AttributeSet attr)
{
	super(context, attr);
}
@Override
protected void onDraw(Canvas canvas) {
	// TODO Auto-generated method stub
	super.onDraw(canvas);
	/*// Get the text to print
    final float textSize = super.getTextSize();
    final String text = super.getText().toString();

    // setup stroke
    Paint mStrokePaint = new Paint();
    mStrokePaint.setUnderlineText(true);
    mStrokePaint.setStrokeWidth(textSize);
    mStrokePaint.setTextSize(textSize);
    mStrokePaint.setFlags(super.getPaintFlags());
    mStrokePaint.setTypeface(super.getTypeface());

    // Figure out the drawing coordinates
    Rect mTextBounds = new Rect();
    super.getPaint().getTextBounds(text, 0, text.length(), mTextBounds);

    // draw everything
    canvas.drawText(text,
            super.getWidth(), (super.getHeight()),
            mStrokePaint);*/
	String text = super.getText().toString();
	Rect rect = new Rect();
    Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL_AND_STROKE);
    paint.setColor(Color.GREEN);
    paint.setUnderlineText(true);
    paint.setStrokeWidth(1);
    getLocalVisibleRect(rect);
    
    super.getPaint().getTextBounds(text, 0, text.length(), rect);
    canvas.drawRect(rect, paint);
    
}
@Override
public boolean onTouchEvent(MotionEvent event) {
	// TODO Auto-generated method stub
	return super.onTouchEvent(event);
}
private final void setupPaint() {
    mStrokePaint.setAntiAlias(true);
    mStrokePaint.setStyle(Paint.Style.STROKE);
    mStrokePaint.setTextAlign(Paint.Align.CENTER);
}
/*private final void setupAttributes(Context context, AttributeSet attrs) {
    final TypedArray array = context.obtainStyledAttributes(attrs,
            R.styleable.OutlinedTextView);
    mOutlineColor = array.getColor(
            R.styleable.OutlinedTextView_outlineColor, 0x00000000);
    array.recycle(); 

    // Force this text label to be centered
    super.setGravity(Gravity.CENTER_HORIZONTAL);
}*/
}
