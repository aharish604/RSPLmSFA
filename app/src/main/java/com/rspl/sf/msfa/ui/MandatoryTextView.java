package com.rspl.sf.msfa.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by e10847 on 22-12-2017.
 */

@SuppressLint("AppCompatCustomView")
public class MandatoryTextView extends TextView {
    public MandatoryTextView(Context context) {
        super(context);
    }

    public MandatoryTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MandatoryTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        String texts = "<font color=#ff0000>*</font>";
        text = "<font color=#000033>"+text+"</font>";
        text = text+""+texts;
        super.setText(Html.fromHtml(text.toString()), type);
    }
}
