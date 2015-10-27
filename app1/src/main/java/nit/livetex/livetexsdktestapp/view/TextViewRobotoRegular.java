package nit.livetex.livetexsdktestapp.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by user on 21.09.15.
 */
public class TextViewRobotoRegular extends TextView {

    public TextViewRobotoRegular(Context context) {
        super(context);
    }

    public TextViewRobotoRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        setTypeface(myTypeface);
    }
}
