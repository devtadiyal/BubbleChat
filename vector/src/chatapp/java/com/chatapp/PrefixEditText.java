package com.chatapp;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import im.vector.R;

public class PrefixEditText extends AppCompatEditText {

    private float mOriginalLeftPadding = -1;
    private String mPrefix;



    public PrefixEditText(Context context) {
        super(context);
    }

    public PrefixEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrefixEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if(attributeSet != null){
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EditText);
            mPrefix = typedArray.getString(R.styleable.EditText_prefix);
            typedArray.recycle();
        }
    }

    public void setPrefix(String prefix){
        mPrefix = prefix;
        invalidate();
    }

    public String getPrefix(){
        return mPrefix;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mPrefix != null){
            calculatePrefix();
            canvas.drawText(mPrefix, mOriginalLeftPadding,getLineBounds(0, null), getPaint());
        }
    }

    private void calculatePrefix() {
        if (mOriginalLeftPadding == -1) {
            float[] widths = new float[mPrefix.length()];
            getPaint().getTextWidths(mPrefix, widths);
            float textWidth = 0;
            for (float w : widths) {
                textWidth += w;
            }
            mOriginalLeftPadding = getCompoundPaddingLeft();
            setPadding((int) (textWidth + mOriginalLeftPadding),
                    getPaddingRight(), getPaddingTop(),
                    getPaddingBottom());
        }
    }

    public String getCompletedText(){
        return mPrefix + getText().toString();
    }
}