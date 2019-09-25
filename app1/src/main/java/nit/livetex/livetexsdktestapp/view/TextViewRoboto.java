package nit.livetex.livetexsdktestapp.view;

import android.content.Context;
import android.graphics.Typeface;

import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by user on 21.09.15.
 */
public class TextViewRoboto extends AppCompatTextView {
    public TextViewRoboto(Context context) {
        super(context);
    }

    public TextViewRoboto(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
        setTypeface(myTypeface);
    }
}
