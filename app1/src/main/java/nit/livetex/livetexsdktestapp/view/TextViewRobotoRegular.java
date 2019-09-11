package nit.livetex.livetexsdktestapp.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by user on 21.09.15.
 */
public class TextViewRobotoRegular extends AppCompatTextView {

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
