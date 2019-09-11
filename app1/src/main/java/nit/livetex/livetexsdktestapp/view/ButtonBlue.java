package nit.livetex.livetexsdktestapp.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by 1 on 21.09.15.
 */
public class ButtonBlue extends AppCompatButton {


    public ButtonBlue(Context context) {
        super(context);
    }

    public ButtonBlue(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
        setTypeface(myTypeface);
    }
}
